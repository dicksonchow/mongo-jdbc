// MongoConnection.java

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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class MongoConnection implements Connection {

	private DB db;
	private Properties clientInfo;
	private boolean readOnly = false;
	private DatabaseMetaData databaseMetaData = null;
	private MongoConnectionInfo connectionInfo = new MongoConnectionInfo();

	public MongoConnection(DB db) {
		this.db = db;
		// TODO proper initialisation of the connection info
		connectionInfo.setMajorVersion("" + Mongo.MAJOR_VERSION);
		connectionInfo.setMinorVersion("" + Mongo.MINOR_VERSION);
		connectionInfo.setDatabase(db.getName());
	}

	public SQLWarning getWarnings() throws SQLException {
		if (isClosed()) {
			throw new MongoSQLException("connection is closed");
		}
		String err = (String) db.getLastError().get("err");
		if (null != err) {
			return new SQLWarning(db.getLastError().toString());
		}
		return null;
	}

	public void clearWarnings() {
		if (!isClosed()) {
			db.resetError();
		}
	}

	// ---- state -----

	public void close() {
		db = null;
	}

	public boolean isClosed() {
		return db == null;
	}

	// --- commit ----

	public void commit() {
		// NO-OP
	}

	public boolean getAutoCommit() {
		return true;
	}

	public void rollback() {
		throw new MongoRuntimeException("can't rollback");
	}

	public void rollback(Savepoint savepoint) {
		throw new MongoRuntimeException("can't rollback");
	}

	public void setAutoCommit(boolean autoCommit) {
		if (!autoCommit) {
			throw new MongoRuntimeException("autoCommit has to be on");
		}
	}

	public void releaseSavepoint(Savepoint savepoint) {
		throw new MongoRuntimeException("no savepoints");
	}

	public Savepoint setSavepoint() {
		throw new MongoRuntimeException("no savepoints");
	}

	public Savepoint setSavepoint(String name) {
		throw new MongoRuntimeException("no savepoints");
	}

	public void setTransactionIsolation(int level) {
		throw new MongoRuntimeException("no TransactionIsolation");
	}

	// --- create ----

	public Array createArrayOf(String typeName, Object[] elements) {
		throw new MongoRuntimeException("no create*");
	}

	public Struct createStruct(String typeName, Object[] attributes) {
		throw new MongoRuntimeException("no create*");
	}

	public Blob createBlob() {
		throw new MongoRuntimeException("no create*");
	}

	public Clob createClob() {
		throw new MongoRuntimeException("no create*");
	}

	public NClob createNClob() {
		throw new MongoRuntimeException("no create*");
	}

	public SQLXML createSQLXML() {
		throw new MongoRuntimeException("no create*");
	}

	// ------- meta data ----

	public String getCatalog() {
		return null;
	}

	public void setCatalog(String catalog) {
		throw new MongoRuntimeException("can't set catalog");
	}

	public Properties getClientInfo() {
		return clientInfo;
	}

	public String getClientInfo(String name) {
		return (String) clientInfo.get(name);
	}

	public void setClientInfo(String name, String value) {
		clientInfo.put(name, value);
	}

	public void setClientInfo(Properties properties) {
		clientInfo = properties;
	}

	public int getHoldability() {
		return ResultSet.HOLD_CURSORS_OVER_COMMIT;
	}

	public void setHoldability(int holdability) {
	}

	public int getTransactionIsolation() {
		return TRANSACTION_NONE;
		//throw new MongoRuntimeException("not done yet");
	}

	public DatabaseMetaData getMetaData() {
		if (null == databaseMetaData) {
			databaseMetaData = new MongoDatabaseMetaData(this);
		}
		return databaseMetaData;
	}

	public boolean isValid(int timeout) {
		return db != null;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		db.setReadOnly(readOnly);
	}

	public Map<String, Class<?>> getTypeMap() {
		throw new MongoRuntimeException("not done yet");
	}

	public void setTypeMap(Map<String, Class<?>> map) {
		throw new MongoRuntimeException("not done yet");
	}

	// ---- Statement -----

	public Statement createStatement() {
		return createStatement(0, 0, 0);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) {
		return createStatement(resultSetType, resultSetConcurrency, 0);
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) {
		return new MongoStatement(this, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	// --- CallableStatement

	public CallableStatement prepareCall(String sql) {
		return prepareCall(sql, 0, 0, 0);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) {
		return prepareCall(sql, resultSetType, resultSetConcurrency, 0);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability) {
		throw new MongoRuntimeException("CallableStatement not supported");
	}

	// ---- PreparedStatement
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return prepareStatement(sql, 0, 0, 0);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) {
		throw new MongoRuntimeException("no PreparedStatement yet");
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) {
		throw new MongoRuntimeException("no PreparedStatement yet");
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return prepareStatement(sql, resultSetType, resultSetConcurrency, 0);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return new MongoPreparedStatement(this, resultSetType,
				resultSetConcurrency, resultSetHoldability, sql);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) {
		throw new MongoRuntimeException("no PreparedStatement yet");
	}

	// ---- random ----

	public String nativeSQL(String sql) {
		return sql;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new MongoUnsupportedOperationException();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new MongoUnsupportedOperationException();
	}

	public DBCollection getCollection(String name) {
		return db.getCollection(name);
	}

	public DB getDb() {
		return db;
	}

	public MongoConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

}
