package com.mongodb.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public abstract class UnimplementedResultSet implements ResultSet {

	public void setFetchSize(int rows) {
		throw new MongoUnsupportedOperationException();
	}

	public int getFetchSize() {
		throw new MongoUnsupportedOperationException();
	}

	public Statement getStatement() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean absolute(int row) {
		throw new MongoUnsupportedOperationException();
	}

	public void afterLast() {
		throw new MongoUnsupportedOperationException();
	}

	public void beforeFirst() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean first() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean isAfterLast() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean isBeforeFirst() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean isFirst() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean isLast() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean last() {
		throw new MongoUnsupportedOperationException();
	}

	public void moveToCurrentRow() {
		throw new MongoUnsupportedOperationException();
	}

	public void moveToInsertRow() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean previous() {
		throw new MongoUnsupportedOperationException();
	}

	public void refreshRow() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean relative(int rows) {
		throw new MongoUnsupportedOperationException();
	}

	public boolean rowDeleted() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean rowInserted() {
		throw new MongoUnsupportedOperationException();
	}

	public boolean rowUpdated() {
		throw new MongoUnsupportedOperationException();
	}

	// modifications

	public void insertRow() {
		throw new MongoUnsupportedOperationException();
	}

	public void cancelRowUpdates() {
		throw new MongoUnsupportedOperationException();
	}

	public void deleteRow() {
		throw new MongoUnsupportedOperationException();
	}

	public void updateRow() {
		throw new MongoUnsupportedOperationException();
	}

	// field updates

	public void updateArray(int columnIndex, Array x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateArray(String columnName, Array x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateAsciiStream(int columnIndex, InputStream x, int length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateAsciiStream(String columnName, InputStream x, int length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateAsciiStream(String columnName, InputStream x, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateAsciiStream(int columnIndex, InputStream x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateAsciiStream(String columnName, InputStream x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBigDecimal(int columnIndex, BigDecimal x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBigDecimal(String columnName, BigDecimal x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBinaryStream(int columnIndex, InputStream x, int length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBinaryStream(String columnName, InputStream x, int length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBinaryStream(String columnName, InputStream x, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBinaryStream(int columnIndex, InputStream x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBinaryStream(String columnName, InputStream x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBlob(int columnIndex, Blob x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBlob(String columnName, Blob x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBlob(int columnIndex, InputStream x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBlob(String columnName, InputStream x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBlob(int columnIndex, InputStream x, long l) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBlob(String columnName, InputStream x, long l) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBoolean(int columnIndex, boolean x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBoolean(String columnName, boolean x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateByte(int columnIndex, byte x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateByte(String columnName, byte x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBytes(int columnIndex, byte[] x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateBytes(String columnName, byte[] x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateCharacterStream(int columnIndex, Reader x, int length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateCharacterStream(String columnName, Reader reader,
			int length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateCharacterStream(String columnName, Reader reader,
			long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateCharacterStream(int columnIndex, Reader x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateCharacterStream(String columnName, Reader reader) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateClob(int columnIndex, Clob x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateClob(String columnName, Clob x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateClob(int columnIndex, Reader x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateClob(String columnName, Reader x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateClob(int columnIndex, Reader x, long l) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateClob(String columnName, Reader x, long l) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateDate(int columnIndex, Date x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateDate(String columnName, Date x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateDouble(int columnIndex, double x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateDouble(String columnName, double x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateFloat(int columnIndex, float x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateFloat(String columnName, float x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateInt(int columnIndex, int x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateInt(String columnName, int x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateLong(int columnIndex, long x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateLong(String columnName, long x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNull(int columnIndex) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNull(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateObject(int columnIndex, Object x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateObject(int columnIndex, Object x, int scale) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateObject(String columnName, Object x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateObject(String columnName, Object x, int scale) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateRef(int columnIndex, Ref x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateRef(String columnName, Ref x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateRowId(int columnIndex, RowId x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateRowId(String columnName, RowId x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateShort(int columnIndex, short x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateShort(String columnName, short x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateSQLXML(String columnName, SQLXML xmlObject) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateString(int columnIndex, String x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateString(String columnName, String x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateTime(int columnIndex, Time x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateTime(String columnName, Time x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateTimestamp(int columnIndex, Timestamp x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateTimestamp(String columnName, Timestamp x) {
		throw new MongoUnsupportedOperationException();
	}

	public Array getArray(String colName) {
		throw new MongoUnsupportedOperationException();
	}

	public InputStream getAsciiStream(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public InputStream getBinaryStream(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Blob getBlob(String colName) {
		throw new MongoUnsupportedOperationException();
	}

	public byte getByte(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Reader getCharacterStream(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Clob getClob(String colName) {
		throw new MongoUnsupportedOperationException();
	}

	public Date getDate(String columnName, Calendar cal) {
		throw new MongoUnsupportedOperationException();
	}

	public Object getObject(String colName,
			@SuppressWarnings("rawtypes") Map map) {
		throw new MongoUnsupportedOperationException();
	}

	public Ref getRef(String colName) {
		throw new MongoUnsupportedOperationException();
	}

	public RowId getRowId(String name) {
		throw new MongoUnsupportedOperationException();
	}

	public SQLXML getSQLXML(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Time getTime(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Time getTime(String columnName, Calendar cal) {
		throw new MongoUnsupportedOperationException();
	}

	public Timestamp getTimestamp(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Timestamp getTimestamp(String columnName, Calendar cal) {
		throw new MongoUnsupportedOperationException();
	}

	public InputStream getUnicodeStream(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public String getNString(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public NClob getNClob(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public Reader getNCharacterStream(String columnName) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNCharacterStream(int columnIndex, Reader x) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNClob(int columnIndex, NClob nClob) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNClob(int columnIndex, Reader reader) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNClob(int columnIndex, Reader reader, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNClob(String columnLabel, NClob nClob) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNClob(String columnLabel, Reader reader) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNClob(String columnLabel, Reader reader, long length) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNString(int columnIndex, String nString) {
		throw new MongoUnsupportedOperationException();
	}

	public void updateNString(String columnLabel, String nString) {
		throw new MongoUnsupportedOperationException();
	}

	// random stuff

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new MongoUnsupportedOperationException();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new MongoUnsupportedOperationException();
	}

}
