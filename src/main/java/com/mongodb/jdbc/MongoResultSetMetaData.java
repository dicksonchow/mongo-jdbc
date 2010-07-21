package com.mongodb.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoResultSetMetaData implements ResultSetMetaData {

	private DBObject obj;
	private Map<Integer, String> keys;
	private String tableName;
	private Map<String, DBObject> definitions;

	public MongoResultSetMetaData(DB db, DBObject obj, Map<Integer, String> keys,
			String tableName) {
		System.out.println("MongoResultSetMetaData Table: " + tableName);
		System.out.println("MongoResultSetMetaData Keys: " + keys.size());
		System.out.println("MongoResultSetMetaData Obj: " + obj);
		if ((null != db) && db.collectionExists("jdbc.tabledefs") && (null != tableName) && (tableName.length() > 0)) {
			DBCollection collection = db.getCollection("jdbc.tabledefs");
			DBObject filter = new BasicDBObject();
			filter.put("TABLE_NAME", tableName);
			DBObject orderBy = new BasicDBObject();
			orderBy.put("COLUMN_NAME", "");
			DBCursor cursor = collection.find(filter).sort(orderBy);
			if (cursor.hasNext()) {
				definitions = new HashMap<String, DBObject>();
				while (cursor.hasNext()) {
					DBObject coldefn = cursor.next();
					definitions.put((String) coldefn.get("COLUMN_NAME"), coldefn);
				}
			}
		}
		this.obj = obj;
		this.keys = keys;
		this.tableName = tableName;
	}

	@Override
	public int getColumnCount() throws SQLException {
		return keys.size();
		//return obj.keySet().size();
	}

	@Override
	public boolean isAutoIncrement(int column) throws SQLException {
		if (column > getColumnCount()) {
			System.out.println("ERROR: MongoResultSetMetaData.isAutoIncrement() column " + column + " : " + keys.size() + " : " + tableName);
			System.out.println("ERROR: MongoResultSetMetaData.isAutoIncrement() obj: " + obj);
			//throw new SQLException("Column index " + column + " exceeds column count " + getColumnCount());
			return true;
		}
		return keys.get(column).equals("_rev");
	}

	/**
	 * All columns are case Sensitive
	 * 
	 * @param column
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean isCaseSensitive(int column) throws SQLException {
		return true;
	}

	/**
	 * All columns are searchble
	 * 
	 * @param column
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean isSearchable(int column) throws SQLException {
		return true;
	}

	/**
	 * Currency value is not supported
	 * 
	 * @param column
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean isCurrency(int column) throws SQLException {
		return false;
	}

	@Override
	public int isNullable(int column) throws SQLException {
		return columnNullable;
	}

	@Override
	public boolean isSigned(int column) throws SQLException {
		return false;
	}

	@Override
	public int getColumnDisplaySize(int column) throws SQLException {
		return 0;
	}

	@Override
	public String getColumnLabel(int column) throws SQLException {
		if (keys.size() < column) {
			//TODO we should actually allow an unknown column, but how do we get the label/name
			throw new SQLException("Column " + column
					+ " is outside maximum for query: " + keys.size());
		}
		return keys.get(column);
	}

	@Override
	public String getColumnName(int column) throws SQLException {
		return getColumnLabel(column);
	}

	@Override
	public String getSchemaName(int column) throws SQLException {
		return tableName;
	}

	public int getPrecision(int column) throws SQLException {
		return 0;
	}

	public int getScale(int column) throws SQLException {
		return 0;
	}

	@Override
	public String getTableName(int column) throws SQLException {
		return tableName;
	}

	@Override
	public String getCatalogName(int column) throws SQLException {
		return getSchemaName(column);
	}

	@Override
	public int getColumnType(int column) throws SQLException {
		if (null != definitions) {
			//Long type = (Long) definitions.get(getColumnName(column)).get("DATA_TYPE");
			//if (null != type) {
			//	return type.intValue();
			//}
			DBObject col = definitions.get(getColumnName(column));
			//System.out.println("MongoResultSetMetaData.getColumnType(): " + col);
			Integer type = (Integer) col.get(MongoColumnMetaData.DATA_TYPE);
			if (null != type) {
				return type.intValue();
			}
		}
		Object val = obj.get(keys.get(column));
		int dataType = java.sql.Types.CHAR;
		if (val == null) {
			//
		} else if (val instanceof java.lang.String) {
			dataType = java.sql.Types.CHAR;
		} else if (val instanceof com.mongodb.BasicDBObject) {
			dataType = java.sql.Types.JAVA_OBJECT;
		} else if (val instanceof org.bson.types.ObjectId) {
			// TODO this seems to be com.mongodb.ObjectId
			dataType = java.sql.Types.ROWID;
		} else if (val instanceof java.lang.Integer) {
			dataType = java.sql.Types.INTEGER;
		} else {
			// TODO other datatypes 
			try {
				System.out.println(column + " = " + obj.get(getColumnName(column)).getClass().getName());
			} catch (Exception e) {
				//ignore
			}
		}
		//System.out.println("MongoResultSetMetaData.getColumnType() " + keys.get(column) + " = " + dataType + " from " + val);
		return dataType;
	}

	@Override
	public String getColumnTypeName(int column) throws SQLException {
		if (null != definitions) {
			DBObject col = definitions.get(getColumnName(column));
			System.out.println("MongoResultSetMetaData.getColumnTypeName(): " + col);
			String typeName = (String) col.get(MongoColumnMetaData.TYPE_NAME);
			if (null != typeName) {
				return typeName;
			}
		}
		try {
			String key = keys.get(column);
			if (null != key) {
				Object keyObj = obj.get(key);
				if (null != keyObj) {
					return keyObj.getClass().getSimpleName();
				} else {
					//TODO 
					return "TODO Unknown String";
				}
			} else {
				throw new SQLException(column + " exceeds number of columns " + getColumnCount());
			}
		} catch (Exception ex) {
			throw new SQLException(ex);
		}
	}

	/**
	 * Nothing is readonly
	 * 
	 * @param column
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean isReadOnly(int column) throws SQLException {
		return false;
	}

	/**
	 * All are writable
	 * 
	 * @param column
	 * @return
	 * @throws java.sql.SQLException
	 */
	@Override
	public boolean isWritable(int column) throws SQLException {
		return true;
	}

	public boolean isDefinitelyWritable(int column) throws SQLException {
		return true;
	}

	@Override
	public String getColumnClassName(int column) throws SQLException {
		int type = getColumnType(column);
		String name = Object.class.getName();
		switch (type) {
		case java.sql.Types.CHAR:
			name = String.class.getName();
			break;
		case java.sql.Types.INTEGER:
			name = Integer.class.getName();
			break;
		case java.sql.Types.JAVA_OBJECT:
			name = DBObject.class.getName();
			break;
		default:
			break;
		}
		return name;
		//try {
		//	return obj.get(keys.get(column)).getClass().toString();
		//} catch (Exception ex) {
		//	throw new SQLException(ex);
		//}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object unwrap(Class iface) throws SQLException {
		throw new MongoUnsupportedOperationException("Not supported yet.");
	}

	@SuppressWarnings("rawtypes")
	public boolean isWrapperFor(Class iface) throws SQLException {
		throw new MongoUnsupportedOperationException("Not supported yet.");
	}

	public int getColumnPosition(String columnName) throws SQLException {
		if (!keys.containsValue(columnName)) {
			throw new SQLException(columnName + " does not exist in meta data");
		}
		int val = 0;
		//TODO should keep this in a reverse may
		for (Entry<Integer, String> entry : keys.entrySet()) {
			if (entry.getValue().equals(columnName)) {
				val = entry.getKey();
				break;
			}
		}
		return val;
	}

}
