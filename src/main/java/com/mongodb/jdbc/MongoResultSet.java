// MongoResultSet.java

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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class MongoResultSet extends UnimplementedResultSet implements ResultSet {

	// members
	private MongoResultSetMetaData resultSetMetaData = null;
	final DB _db;
	final DBCursor _cursor; // a null _cursor represents an empty ResultSet
	//final MongoFieldLookup _fields;
	DBObject _cur;
	int _row = 0;
	boolean _closed = false;
	boolean _empty = false;

	public MongoResultSet(DB db, DBCursor cursor, MongoResultSetMetaData resultSetMetaData) {
		if (null == resultSetMetaData) {
			throw new RuntimeException("ResultSetMetaData is null");
		}
		_cursor = cursor;
		_db = db;
		this.resultSetMetaData = resultSetMetaData;
		if (_cursor == null) {
			_empty = true;
			//_fields = new MongoFieldLookup(null);
			//TODO how to get the table name
			//if (null == this.resultSetMetaData) {
			//	this.resultSetMetaData = new MongoResultSetMetaData(null, new BasicDBObject(), _fields.ids, "");
			//}
		} else {
			DBObject refObj = null;
			if (null != cursor.getKeysWanted()) {
				refObj = cursor.getKeysWanted();
			} else {
				refObj = cursor.iterator().next();
			}
			//_fields = new MongoFieldLookup(refObj.keySet());
			//if (null == this.resultSetMetaData) {
			//	this.resultSetMetaData = new MongoResultSetMetaData(_db, refObj, _fields.ids, (String) refObj.get("_ns"));
			//}
		}
	}

	public void clearWarnings() {
		// NO-OP
	}

	public void close() {
		_closed = true;
	}

	public boolean isClosed() {
		return _closed;
	}

	// meta data

	public int getConcurrency() {
		return CONCUR_READ_ONLY;
	}

	public int getType() {
		return TYPE_FORWARD_ONLY;
	}

	public void setFetchDirection(int direction) {
		if (direction == getFetchDirection())
			return;
		throw new MongoUnsupportedOperationException();
	}

	public int getFetchDirection() {
		return 1;
	}

	public String getCursorName() {
		if (_empty) {
			return "MongoResultSet: empty";
		}
		return "MongoResultSet: " + _cursor.toString();
	}

	public int getHoldability() {
		return ResultSet.HOLD_CURSORS_OVER_COMMIT;
	}

	// cursor moving methods

	public int getRow() {
		return _row;
	}

	// accessors
	public Array getArray(int i) {
		return getArray(_find(i));
	}

	public InputStream getAsciiStream(int columnIndex) {
		return getAsciiStream(_find(columnIndex));
	}

	public BigDecimal getBigDecimal(int columnIndex) {
		return getBigDecimal(_find(columnIndex));
	}

	public BigDecimal getBigDecimal(int columnIndex, int scale) {
		return getBigDecimal(_find(columnIndex), scale);
	}

	public InputStream getBinaryStream(int columnIndex) {
		return getBinaryStream(_find(columnIndex));
	}

	public Blob getBlob(int i) {
		return getBlob(_find(i));
	}

	public boolean getBoolean(int columnIndex) {
		return getBoolean(_find(columnIndex));
	}

	public boolean getBoolean(String columnName) {
		Object x = _cur.get(columnName);
		if (x == null)
			return false;
		return (Boolean) x;
	}

	public byte getByte(int columnIndex) {
		return getByte(_find(columnIndex));
	}

	public byte[] getBytes(int columnIndex) {
		return getBytes(_find(columnIndex));
	}

	public byte[] getBytes(String columnName) {
		return (byte[]) _cur.get(columnName);
	}

	public Reader getCharacterStream(int columnIndex) {
		return getCharacterStream(_find(columnIndex));
	}

	public Clob getClob(int i) {
		return getClob(_find(i));
	}

	public Date getDate(int columnIndex) {
		return getDate(_find(columnIndex));
	}

	public Date getDate(int columnIndex, Calendar cal) {
		return getDate(_find(columnIndex), cal);
	}

	public Date getDate(String columnName) {
		return (Date) _cur.get(columnName);
	}

	public double getDouble(int columnIndex) {
		return getDouble(_find(columnIndex));
	}

	public double getDouble(String columnName) {
		return _getNumber(columnName).doubleValue();
	}

	public float getFloat(int columnIndex) {
		return getFloat(_find(columnIndex));
	}

	public float getFloat(String columnName) {
		return _getNumber(columnName).floatValue();
	}

	public int getInt(int columnIndex) {
		return getInt(_find(columnIndex));
	}

	public int getInt(String columnName) {
		return _getNumber(columnName).intValue();
	}

	public long getLong(int columnIndex) {
		return getLong(_find(columnIndex));
	}

	public long getLong(String columnName) {
		return _getNumber(columnName).longValue();
	}

	public short getShort(int columnIndex) {
		return getShort(_find(columnIndex));
	}

	public short getShort(String columnName) {
		return _getNumber(columnName).shortValue();
	}

	Number _getNumber(String n) {
		Number x = null;
		try {
			x = (Number) (_cur.get(n));
		} catch (ClassCastException e) {
			//TODO need to remove this when we know what is wrong
			System.out.println("ERROR: MongoResultSet._getNumber() " + n);
			e.printStackTrace();
		}
		if (x == null)
			return 0;
		return x;
	}

	public Object getObject(int columnIndex) {
		if (columnIndex == 0)
			return _cur;
		return getObject(_find(columnIndex));
	}

	public Object getObject(int i, @SuppressWarnings("rawtypes") Map map) {
		if (i == 0)
			return _cur;
		return getObject(_find(i), map);
	}

	public Object getObject(String columnName) {
		return _cur.get(columnName);
	}

	public Ref getRef(int i) {
		return getRef(_find(i));
	}

	public RowId getRowId(int i) {
		return getRowId(_find(i));
	}

	public SQLXML getSQLXML(int columnIndex) {
		return getSQLXML(_find(columnIndex));
	}

	public String getString(int columnIndex) {
		return getString(_find(columnIndex));
	}

	public String getString(String columnName) {
		Object x = _cur.get(columnName);
		if (x == null)
			return null;
		return x.toString();
	}

	public Time getTime(int columnIndex) {
		return getTime(_find(columnIndex));
	}

	public Time getTime(int columnIndex, Calendar cal) {
		return getTime(_find(columnIndex), cal);
	}

	public Timestamp getTimestamp(int columnIndex) {
		return getTimestamp(_find(columnIndex));
	}

	public Timestamp getTimestamp(int columnIndex, Calendar cal) {
		return getTimestamp(_find(columnIndex), cal);
	}

	public InputStream getUnicodeStream(int columnIndex) {
		return getUnicodeStream(_find(columnIndex));
	}

	public URL getURL(int columnIndex) throws SQLException {
		return getURL(_find(columnIndex));
	}

	public URL getURL(String columnName) throws SQLException {
		try {
			return new URL(getString(columnName));
		} catch (java.net.MalformedURLException m) {
			throw new SQLException("bad url [" + getString(columnName) + "]");
		}
	}

	// N stuff

	public String getNString(int columnIndex) {
		return getNString(_find(columnIndex));
	}

	public NClob getNClob(int columnIndex) {
		return getNClob(_find(columnIndex));
	}

	public Reader getNCharacterStream(int columnIndex) {
		return getNCharacterStream(_find(columnIndex));
	}

	// column <-> int mapping

	public int findColumn(String columnName) {
		try {
			return resultSetMetaData.getColumnPosition(columnName);
		} catch (SQLException e) {
			//TODO how do we handle this
			e.printStackTrace();
		}
		return -1;
	}

	public String _find(int i) {
		try {
			return resultSetMetaData.getColumnName(i);
		} catch (SQLException e) {
			//TODO how do we handle this
			e.printStackTrace();
		}
		return null;
	}

	// moving throgh cursor

	public boolean next() {
		if (_empty) {
			return false;
		}
		if (!_cursor.hasNext()) {
			return false;
		}
		_cur = _cursor.next();
		//System.out.println("MongoResultSet.next() " + _cur);
		return true;
	}

	public ResultSetMetaData getMetaData() {
		return resultSetMetaData;
	}

	public boolean wasNull() {
		//TODO implement wasNull
		return false;
	}

	public BigDecimal getBigDecimal(String columnName) {
		Number num = _getNumber(columnName);
		return new BigDecimal(num.longValue());
	}

	public BigDecimal getBigDecimal(String columnName, int scale) {
		BigDecimal num = new BigDecimal(_getNumber(columnName).floatValue());
		return num.setScale(scale);
	}

	public SQLWarning getWarnings() {
		//TODO _db.getLastError();
		return null;
	}

}
