// Executor.java

/**
 *      Copyright (C) 2008 10gen Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.mongodb.jdbc;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Executor {

	static final boolean DEBUG = false;

	final DB _db;
	final String _sql;
	final Statement _statement;
	final Connection _connection;
	SelectHolder select = null;
	private ResultSet keys = null;
	List<Object> _params;
	int _pos;

	private MongoResultSetMetaData resultSetMetaData;

	// TODO
	Executor(Connection connection, DB db, String sql) throws MongoSQLException {
		_connection = connection;
		_db = db;
		_sql = sql;
		_statement = parse(sql);
		if (_statement instanceof Select) {
			select = new SelectHolder((Select) _statement);
		}
		resultSetMetaData = createResultSetMetaData();
		if (DEBUG) {
			System.out.println(sql);
		}
	}

	private MongoResultSetMetaData createResultSetMetaData() {
		MongoResultSetMetaData meta = null;
		if (_statement instanceof Select) {
			// if (DEBUG) {
			System.out.println("Executor.createResultSetMetaData() "
					+ select.getFields());
			System.out.println("Executor.createResultSetMetaData() "
					+ select.getFieldOrder());
			System.out.println("Executor.createResultSetMetaData() "
					+ select.table);
			// }
			meta = new MongoResultSetMetaData(_db, select.getFields(),
					select.getFieldOrder(), select.table.toString());
		}

		return meta;
	}

	void setParams(List<Object> params) {
		_pos = 1;
		_params = params;
	}

	DBCursor query() throws MongoSQLException {
		keys = null;
		if (!(_statement instanceof Select))
			throw new IllegalArgumentException("not a query sql statement");

		DBCursor c = select.getDBCollection().find(select.getQuery(),
				select.getFields());

		{ // order by
			@SuppressWarnings("rawtypes")
			List orderBylist = select.getOrderByElements();
			if (orderBylist != null && orderBylist.size() > 0) {
				BasicDBObject order = new BasicDBObject();
				for (int i = 0; i < orderBylist.size(); i++) {
					OrderByElement o = (OrderByElement) orderBylist.get(i);
					order.put(o.getColumnReference().toString(), o.isAsc() ? 1
							: -1);
				}
				c.sort(order);
			}
		}

		return c;
	}

	public boolean isQuery() throws MongoSQLException {
		if (null == _statement) {
			throw new MongoSQLException("statement is null");
		}
		if (_statement instanceof Select) {
			return true;
		}
		return false;
	}

	int writeop() throws MongoSQLException {
		keys = null;
		if (_statement instanceof Insert)
			return insert((Insert) _statement);
		else if (_statement instanceof Update)
			return update((Update) _statement);
		else if (_statement instanceof Drop)
			return drop((Drop) _statement);

		throw new MongoRuntimeException("unknown write: "
				+ _statement.getClass());
	}

	int insert(Insert in) throws MongoSQLException {

		if (in.getColumns() == null)
			throw new MongoSQLException.BadSQL(
					"have to give column names to insert");

		DBCollection coll = getCollection(in.getTable());
		if (DEBUG) {
			System.out.println("\t" + "table: " + coll);
		}
		DBObject typeObj = coll.findOne();

		if (!(in.getItemsList() instanceof ExpressionList))
			throw new MongoUnsupportedOperationException("need ExpressionList");

		BasicDBObject o = new BasicDBObject();

		@SuppressWarnings("rawtypes")
		List valueList = ((ExpressionList) in.getItemsList()).getExpressions();
		if (in.getColumns().size() != valueList.size())
			throw new MongoSQLException.BadSQL(
					"number of values and columns have to match");

		for (int i = 0; i < valueList.size(); i++) {
			String key = in.getColumns().get(i).toString();
			if (DEBUG) {
				System.out.println("Executor.insert() " + key);
			}
			Object val = null;
			if (null == typeObj) {
				val = toConstant(null, (Expression) valueList.get(i));
			} else {
				val = toConstant(typeObj.get(key),
						(Expression) valueList.get(i));
			}
			o.put(key, val);
		}

		coll.insert(o);
		// System.out.println("Executor.insert() _id = " + o.get("_id"));
		if (DEBUG) {
			System.out.println("Executor.insert() new id = "
					+ o.getString("_id") + " AND _ns = " + o.getString("_ns"));
		}
		// Set<String> names = connection.getDb().getCollectionNames();
		Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
		Map<String, Object> value = new HashMap<String, Object>();
		if (DEBUG) {
			System.out.println("Executor.insert() _id type = "
					+ o.get("_id").getClass().getName());
		}
		value.put("_id", o.get("_id"));
		values.add(value);
		DBObject cols = new BasicDBObject();
		cols.put("_id", o.get("_id"));
		keys = new SimpleResultSet(_db, cols, values);
		return 1; // TODO - this is wrong
	}

	int update(Update up) throws MongoSQLException {

		DBCollection coll = getCollection(up.getTable());
		DBObject typeObj = coll.findOne();

		DBObject query = new BasicDBObject();
		parseWhere(query, up.getWhere());

		BasicDBObject set = new BasicDBObject();

		for (int i = 0; i < up.getColumns().size(); i++) {
			String k = up.getColumns().get(i).toString();
			Expression v = (Expression) (up.getExpressions().get(i));
			set.put(k.toString(), toConstant(typeObj.get(k), v));
		}

		DBObject mod = new BasicDBObject("$set", set);

		coll.update(query, mod);
		return 1; // TODO
	}

	int drop(Drop d) {
		DBCollection c = _db.getCollection(d.getName());
		c.drop();
		return 1;
	}

	// ---- helpers -----

	String toFieldName(Expression e) {
		if (e instanceof StringValue)
			return e.toString();
		if (e instanceof Column)
			return e.toString();
		throw new MongoUnsupportedOperationException("can't turn [" + e + "] "
				+ e.getClass() + " into field name");
	}

	// TODO the ref object for typing should come from the DB table metadata
	private Object toConstant(Object ref, Expression e) {
		if (e instanceof StringValue) {
			String val = ((StringValue) e).getValue();
			Object res = val;
			if (val.startsWith("{") && val.endsWith("}")) {
				// assume its a json expression
				try {
					res = (DBObject) com.mongodb.util.JSON.parse(val);
					if (res == null) {
						// if the parse results as null, treat it as a string
						res = val;
					}
				} catch (Exception ex) {
					// if we cant parse it, treat it as a string
					res = val;
				}
			}
			return res;
		} else if (e instanceof DoubleValue) {
			return ((DoubleValue) e).getValue();
		} else if (e instanceof LongValue) {
			Long val = ((LongValue) e).getValue();
			Object objval = val;
			if (ref instanceof Integer) {
				objval = new Integer(val.intValue());
			}
			return objval;
		} else if (e instanceof NullValue) {
			return null;
		} else if (e instanceof JdbcParameter) {
			return _params.get(_pos++);
		}
		throw new MongoUnsupportedOperationException("can't turn [" + e + "] "
				+ e.getClass().getName() + " into constant ");
	}

	private void addGTLTFilter(DBObject o, String expr, String field, Object val) {
		DBObject filter = null;
		if (o.containsField(field)) {
			filter = (DBObject) o.get(field);
		} else {
			filter = new BasicDBObject();
		}
		filter.put(expr, val);
		o.put(field, filter);

	}

	private void parseWhere(DBObject o, Expression e) {
		if (e == null)
			return;
		// TODO handle OR
		if (e instanceof EqualsTo) {
			EqualsTo eq = (EqualsTo) e;
			o.put(toFieldName(eq.getLeftExpression()),
					toConstant(null, eq.getRightExpression()));
		} else if (e instanceof AndExpression) {
			parseWhere(o, ((AndExpression) e).getLeftExpression());
			parseWhere(o, ((AndExpression) e).getRightExpression());
		} else if (e instanceof LikeExpression) {
			LikeExpression like = (LikeExpression) e;
			// /^a/
			String field = toFieldName(like.getLeftExpression());
			Object val = toConstant(null, like.getRightExpression());
			if (val instanceof String) {
				String str = (String) val;
				// TODO need to properly convert to regexp
				if (str.endsWith("%")) {
					val = "/^" + str.substring(0, str.length() - 1) + "/";
				} else {
					System.out
							.println("Executor.parseWhere() Unhandled wildcard. Leaving as is!!! "
									+ str);
				}
			}
			System.out.println("Executor.parseWhere() " + field + " LIKE "
					+ val);
			o.put(field, val);
			// throw new
			// MongoUnsupportedOperationException("Like filter not handled: " +
			// like.toString());
		} else if (e instanceof InExpression) {
			InExpression in = (InExpression) e;
			if (in.getItemsList() instanceof ExpressionList) {
				ExpressionList list = (ExpressionList) in.getItemsList();
				DBObject filter = new BasicDBObject();
				List<Object> vals = new ArrayList<Object>();
				for (Object obj : list.getExpressions()) {
					vals.add(toConstant(null, (Expression) obj));
				}
				if (in.isNot()) {
					filter.put("$nin", vals);
				} else {
					filter.put("$in", vals);
				}
				o.put(toFieldName(in.getLeftExpression()), filter);
			} else {
				throw new MongoUnsupportedOperationException(
						"IN filter with unknown item list : "
								+ in.getItemsList().getClass().getName());
			}
		} else if (e instanceof NotEqualsTo) {
			NotEqualsTo expr = (NotEqualsTo) e;
			addGTLTFilter(o, "$ne", toFieldName(expr.getLeftExpression()),
					toConstant(null, expr.getRightExpression()));
		} else if (e instanceof MinorThan) {
			MinorThan expr = (MinorThan) e;
			addGTLTFilter(o, "$lt", toFieldName(expr.getLeftExpression()),
					toConstant(null, expr.getRightExpression()));
		} else if (e instanceof MinorThanEquals) {
			MinorThanEquals expr = (MinorThanEquals) e;
			addGTLTFilter(o, "$lte", toFieldName(expr.getLeftExpression()),
					toConstant(null, expr.getRightExpression()));
		} else if (e instanceof GreaterThan) {
			GreaterThan expr = (GreaterThan) e;
			addGTLTFilter(o, "$gt", toFieldName(expr.getLeftExpression()),
					toConstant(null, expr.getRightExpression()));
		} else if (e instanceof GreaterThanEquals) {
			GreaterThanEquals expr = (GreaterThanEquals) e;
			addGTLTFilter(o, "$gte", toFieldName(expr.getLeftExpression()),
					toConstant(null, expr.getRightExpression()));
		} else {
			throw new MongoUnsupportedOperationException("can't handle: "
					+ e.getClass() + " yet");
		}

		// return o;
	}

	DBObject ORIGparseWhere(Expression e) {
		BasicDBObject o = new BasicDBObject();
		if (e == null)
			return o;

		if (e instanceof EqualsTo) {
			EqualsTo eq = (EqualsTo) e;
			o.put(toFieldName(eq.getLeftExpression()),
					toConstant(null, eq.getRightExpression()));
		} else if (e instanceof AndExpression) {
			((AndExpression) e).getLeftExpression();
		} else if (e instanceof LikeExpression) {
			LikeExpression like = (LikeExpression) e;
			throw new MongoUnsupportedOperationException(
					"Like filter not handled: " + like.toString());
		} else if (e instanceof MinorThan) {
			MinorThan expr = (MinorThan) e;
			// db.collection.find({ "field" : { $lt: value } } ); // less than :
			// field < value
			BasicDBObject filter = new BasicDBObject();
			filter.put("$lt", toConstant(null, expr.getRightExpression()));
			o.put(toFieldName(expr.getLeftExpression()), filter);
		} else if (e instanceof MinorThanEquals) {
			MinorThanEquals expr = (MinorThanEquals) e;
			// db.collection.find({ "field" : { $lte: value } } ); // less than
			// or equal to : field <= value
			BasicDBObject filter = new BasicDBObject();
			filter.put("$lte", toConstant(null, expr.getRightExpression()));
			o.put(toFieldName(expr.getLeftExpression()), filter);
		} else if (e instanceof GreaterThan) {
			GreaterThan expr = (GreaterThan) e;
			// db.collection.find({ "field" : { $gt: value } } ); // greater
			// than : field > value
			BasicDBObject filter = new BasicDBObject();
			filter.put("$gt", toConstant(null, expr.getRightExpression()));
			o.put(toFieldName(expr.getLeftExpression()), filter);
		} else if (e instanceof GreaterThanEquals) {
			GreaterThanEquals expr = (GreaterThanEquals) e;
			// db.collection.find({ "field" : { $gte: value } } ); // greater
			// than or equal to : field >= value
			BasicDBObject filter = new BasicDBObject();
			filter.put("$gte", toConstant(null, expr.getRightExpression()));
			o.put(toFieldName(expr.getLeftExpression()), filter);
		} else {
			throw new MongoUnsupportedOperationException("can't handle: "
					+ e.getClass() + " yet");
		}

		return o;
	}

	Statement parse(String s) throws MongoSQLException {
		s = s.trim();

		try {
			return (new CCJSqlParserManager()).parse(new StringReader(s));
		} catch (Exception e) {
			e.printStackTrace();
			throw new MongoSQLException.BadSQL(s);
		}

	}

	// ----

	DBCollection getCollection(Table t) {
		return _db.getCollection(t.toString());
	}

	public ResultSet getKeys() {
		return keys;
	}

	public MongoResultSetMetaData getMetaData() {
		return resultSetMetaData;
	}

	private class SelectHolder {
		DBCollection coll;
		Table table;
		DBObject fields;
		DBObject query;
		PlainSelect ps;
		Map<Integer, String> fieldOrder = new HashMap<Integer, String>();
		boolean allColumns = false;

		public SelectHolder(Select select) {
			if (!(select.getSelectBody() instanceof PlainSelect))
				throw new MongoUnsupportedOperationException(
						"can only handle PlainSelect so far");

			ps = (PlainSelect) select.getSelectBody();
			if (!(ps.getFromItem() instanceof Table))
				throw new MongoUnsupportedOperationException(
						"can only handle regular tables");
			table = (Table) ps.getFromItem();

			fields = new BasicDBObject();
			int i = 0;
			for (Object o : ps.getSelectItems()) {
				SelectItem si = (SelectItem) o;
				if (si instanceof AllColumns) {
					if (fields.keySet().size() > 0)
						throw new MongoUnsupportedOperationException(
								"can't have * and fields");
					allColumns = true;
					break;
				} else if (si instanceof SelectExpressionItem) {
					SelectExpressionItem sei = (SelectExpressionItem) si;
					// as we don't know the type here we default to a String as
					// its the safest
					fields.put(toFieldName(sei.getExpression()), "1");
					i++;
					fieldOrder.put(new Integer(i),
							toFieldName(sei.getExpression()));
				} else {
					throw new MongoUnsupportedOperationException(
							"unknown select item: " + si.getClass());
				}
			}
			// where
			query = new BasicDBObject();
			parseWhere(query, ps.getWhere());

			if (allColumns) {
				if (null != _connection) {
					try {
						ResultSet cols = _connection.getMetaData().getColumns(
								null, null, table.toString(), "%");
						fields = new BasicDBObject();
						while (cols.next()) {
							fields.put(
									cols.getString(MongoColumnMetaData.COLUMN_NAME),
									"1");
							fieldOrder
									.put(new Integer(
											cols.getInt(MongoColumnMetaData.ORDINAL_POSITION)),
											cols.getString(MongoColumnMetaData.COLUMN_NAME));
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
//				if (false) {
//					DBObject found = _db.getCollection(table.toString())
//							.findOne(query);
//					fields = new BasicDBObject();
//					// System.out.println("Executor.SelectHolder.SelectHolder() "
//					// + fields);
//					int fieldPos = 0;
//					for (String field : found.keySet()) {
//						fieldPos++;
//						if (found.get(field) instanceof DBObject) {
//							// we get an error when we add a field of type
//							// DBObject
//							fields.put(field, "1");
//						} else {
//							fields.put(field, found.get(field));
//						}
//						fieldOrder.put(new Integer(fieldPos), field);
//						System.out
//								.println("Executor.SelectHolder.SelectHolder() Add column: "
//										+ field);
//					}
//					fields.removeField("obj1");
//				}
			}

			coll = getCollection(table);

			// done with basics, build DBCursor
			if (DEBUG)
				System.out.println("\t" + "table: " + coll);
			if (DEBUG)
				System.out.println("\t" + "fields: " + fields);
			if (DEBUG)
				System.out.println("\t" + "query : " + query);

		}

		public Map<Integer, String> getFieldOrder() {
			return fieldOrder;
		}

		@SuppressWarnings("rawtypes")
		public List getOrderByElements() {
			return ps.getOrderByElements();
		}

		public DBObject getFields() {
			return fields;
		}

		public DBObject getQuery() {
			return query;
		}

		public DBCollection getDBCollection() {
			return coll;
		}
	}
}
