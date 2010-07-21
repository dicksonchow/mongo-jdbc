package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

//import org.eclipse.datatools.connectivity.Version;
//import org.eclipse.datatools.connectivity.drivers.jdbc.IJDBCConnectionProfileConstants;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

public class MongoDatabaseMetaData implements DatabaseMetaData {

	public static final String JDBC_TABLEDEFS_COLLECTION = "jdbc.tabledefs";

	private MongoConnection connection;

	public MongoDatabaseMetaData(MongoConnection conn) {
		this.connection = conn;
	}

	@Override
	public boolean allProceduresAreCallable() throws SQLException {
		return false;
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException {
		return true;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return true;
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean deletesAreDetected(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getAttributes(String arg0, String arg1, String arg2,
			String arg3) throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public ResultSet getBestRowIdentifier(String arg0, String arg1,
			String arg2, int arg3, boolean arg4) throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		return "/";
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		return "Catalog";
	}

	@Override
	public ResultSet getCatalogs() throws SQLException {
		// TODO cache the catalog list
		System.out.println("MongoDatabaseMetaData.getCatalogs()");
		Set<String> names = connection.getDb().getCollectionNames();
		Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
		for (String tableCat : names) {
			Map<String, Object> value = new HashMap<String, Object>();
			value.put("TABLE_CAT", tableCat);
			values.add(value);
		}
		DBObject cols = new BasicDBObject();
		cols.put("TABLE_CAT", "");
		return new SimpleResultSet(connection.getDb(), cols, values);
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public ResultSet getColumnPrivileges(String arg0, String arg1, String arg2,
			String arg3) throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	/*
	 * catalog - a catalog name; must match the catalog name as it is stored in
	 * the database; "" retrieves those without a catalog; null means that the
	 * catalog name should not be used to narrow the search schemaPattern - a
	 * schema name pattern; must match the schema name as it is stored in the
	 * database; "" retrieves those without a schema; null means that the schema
	 * name should not be used to narrow the search tableNamePattern - a table
	 * name pattern; must match the table name as it is stored in the database
	 * columnNamePattern - a column name pattern; must match the column name as
	 * it is stored in the database
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultSet getColumns(String catalog, String schemaPattern,
			String tableNamePattern, String columnNamePattern)
			throws SQLException {
		DBObject cols = new MongoColumnMetaData(catalog, schemaPattern, tableNamePattern, columnNamePattern, java.sql.Types.CHAR, 0).getColumn();
		ResultSet ret = null;
		if (connection.getDb().collectionExists(JDBC_TABLEDEFS_COLLECTION)) {
			DBCollection collection = connection.getDb().getCollection(JDBC_TABLEDEFS_COLLECTION);
			DBObject filter = new BasicDBObject();
			filter.put("TABLE_NAME", tableNamePattern);
			DBObject orderBy = new BasicDBObject();
			orderBy.put("ORDINAL_POSITION", "");
			DBCursor cursor = collection.find(filter).sort(orderBy);
			if (cursor.hasNext()) {
				MongoResultSetMetaData rsMeta = new MongoResultSetMetaData(connection.getDb(), MongoColumnMetaData.getDummyObject(), MongoColumnMetaData.getKeys(), JDBC_TABLEDEFS_COLLECTION);
				ret = new MongoResultSet(connection.getDb(), cursor, rsMeta);
			}
		}
		if (null == ret) {
			Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
			DBCollection collection = connection.getDb().getCollection(
					tableNamePattern);
			DBObject obj = collection.findOne();
			if (null == obj) {
				//no jdbc definition and no object so we cannot determine meta data
				return new EmptyResultSet(connection.getDb());
			}
			Set<String> names = obj.keySet();
			int ordinalPosition = 0;
			for (String columnName : names) {
				ordinalPosition++;
				int dataType = java.sql.Types.CHAR;
				if (obj.get(columnName) == null) {
					dataType = java.sql.Types.CHAR;
				} else if (obj.get(columnName) instanceof java.lang.String) {
					dataType = java.sql.Types.CHAR;
				} else if (obj.get(columnName) instanceof com.mongodb.BasicDBObject) {
					dataType = java.sql.Types.JAVA_OBJECT;
				} else if (obj.get(columnName) instanceof org.bson.types.ObjectId) {
					//TODO is this correct
					dataType = java.sql.Types.JAVA_OBJECT;
				} else if (obj.get(columnName) instanceof java.lang.Integer) {
					dataType = java.sql.Types.INTEGER;
				} else {
					System.out.println("Unhandled column type for " + columnName + " = " + obj.get(columnName).getClass().getName());
				}
				MongoColumnMetaData col = new MongoColumnMetaData(catalog, schemaPattern, tableNamePattern, columnName, dataType, ordinalPosition);
				//Map<String, Object> column = new HashMap<String, Object>();
				//column.put("TABLE_CAT", catalog);
				//column.put("TABLE_SCHEM", schemaPattern);
				//column.put("TABLE_NAME", tableNamePattern);
				//column.put("COLUMN_NAME", columnName);
				//column.put("DATA_TYPE", dataType);
				//column.put("TYPE_NAME", "CHAR");
				//column.put("COLUMN_SIZE", 65000);
				//column.put("DECIMAL_DIGITS", null);
				//column.put("NULLABLE", 1);
				values.add(col.getColumn().toMap());
			}
			ret = new SimpleResultSet(connection.getDb(), cols, values);
		}
		return ret;
	}

	public void createTable(MongoTableMetaData table) {
		DBCollection collection = connection.getDb().getCollection(JDBC_TABLEDEFS_COLLECTION);
		//first delete any existing column definitions
		DBObject filter = new BasicDBObject();
		filter.put("TABLE_NAME", table.getTableName());
		DBCursor cursor = collection.find(filter);
		if (cursor.hasNext()) {
			while (cursor.hasNext()) {
				collection.remove(cursor.next());
			}
		}
		//then create the new column definitions
		for (MongoColumnMetaData column : table.getColumns()) {
			collection.insert(column.getColumn());
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}

	@Override
	public ResultSet getCrossReference(String arg0, String arg1, String arg2,
			String arg3, String arg4, String arg5) throws SQLException {
		//TODO do we somehow want to support foreign keys
		return new EmptyResultSet(connection.getDb());

	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException {
		return connection.getConnectionInfo().getMajorVersionInt();
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException {
		return connection.getConnectionInfo().getMinorVersionInt();
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return connection.getConnectionInfo().getProductName();
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return connection.getConnectionInfo().getVersion();
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		return java.sql.Connection.TRANSACTION_NONE;
	}

	@Override
	public int getDriverMajorVersion() {
		return connection.getConnectionInfo().getMajorVersionInt();
	}

	@Override
	public int getDriverMinorVersion() {
		return connection.getConnectionInfo().getMinorVersionInt();
	}

	@Override
	public String getDriverName() throws SQLException {
		return connection.getConnectionInfo().getProductName();
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return connection.getConnectionInfo().getVersion();
	}

	@Override
	public ResultSet getExportedKeys(String arg0, String arg1, String arg2)
			throws SQLException {
		//TODO if we support foreign keys we need to implement
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		return "~`!@#$%^&*()-+={[}]:;',.?/";
	}

	@Override
	public ResultSet getFunctionColumns(String arg0, String arg1, String arg2,
			String arg3) throws SQLException {
		// no support for function columns
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public ResultSet getFunctions(String arg0, String arg1, String arg2)
			throws SQLException {
		// no support for functions
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		return "'";
	}

	@Override
	public ResultSet getImportedKeys(String arg0, String arg1, String arg2)
			throws SQLException {
		// TODO implement if we have FKs
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public ResultSet getIndexInfo(String catalogName, String schemaName, String tableName,
			boolean onlyUnique, boolean approx) throws SQLException {
		List<DBObject> indexInfo = connection.getDb().getCollection(tableName).getIndexInfo();
		for (DBObject dbObject : indexInfo) {
			System.out.println("MongoDatabaseMetaData.getIndexInfo() " + dbObject.toString());
		}
		// TODO return index info
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException {
		return 0;
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxConnections() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxStatements() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		return 1;
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		return 0;
	}

	@Override
	public String getNumericFunctions() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getPrimaryKeys(String arg0, String arg1, String arg2)
			throws SQLException {
		// return ResultSet with "_id"
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public ResultSet getProcedureColumns(String arg0, String arg1, String arg2,
			String arg3) throws SQLException {
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		return "Procedure";
	}

	@Override
	public ResultSet getProcedures(String arg0, String arg1, String arg2)
			throws SQLException {
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		//TODO do we have any specific words we want to use
		return "";
	}

	@Override
	public int getSQLStateType() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		return "Schema";
	}

	@Override
	public ResultSet getSchemas() throws SQLException {
		// TABLE_SCHEM and TABLE_CATALOG
		// TODO cache the schema list
		System.out.println("MongoDatabaseMetaData.getSchemas()");
		Set<String> names = connection.getDb().getCollectionNames();
		Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
		for (String tableCat : names) {
			Map<String, Object> value = new HashMap<String, Object>();
			value.put("TABLE_SCHEM", tableCat);
			value.put("TABLE_CATALOG", tableCat);
			values.add(value);
		}
		DBObject cols = new BasicDBObject();
		cols.put("TABLE_SCHEM", "");
		cols.put("TABLE_CATALOG", "");
		return new SimpleResultSet(connection.getDb(), cols, values);
	}

	@Override
	public ResultSet getSchemas(String catalogName, String schemaName)
			throws SQLException {
		//throw new NotYetImplementedException("Not yet implemented");
		//TODO filter schemas
		return getSchemas();
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public String getStringFunctions() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getSuperTables(String arg0, String arg1, String arg2)
			throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public ResultSet getSuperTypes(String arg0, String arg1, String arg2)
			throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		//throw new NotYetImplementedException("Not yet implemented");
		return "";
	}

	@Override
	public ResultSet getTablePrivileges(String arg0, String arg1, String arg2)
			throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		//ResultSet res = new SimpleResultSet();
		//Set<String> names = connection.getDb().getCollectionNames();
		Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
		Map<String, Object> value = new HashMap<String, Object>();
		value.put("TABLE_TYPE", "TABLE");
		values.add(value);
		value = new HashMap<String, Object>();
		value.put("TABLE_TYPE", "SYSTEM TABLE");
		values.add(value);
		DBObject cols = new BasicDBObject();
		cols.put("TABLE_TYPE", "");
		return new SimpleResultSet(connection.getDb(), cols, values);
		//throw new NotYetImplementedException("Not yet implemented");
	}

	/*
	 * catalog - a catalog name; must match the catalog name as it is stored in
	 * the database; "" retrieves those without a catalog; null means that the
	 * catalog name should not be used to narrow the search. schemaPattern - a
	 * schema name pattern; must match the schema name as it is stored in the
	 * database; "" retrieves those without a schema; null means that the schema
	 * name should not be used to narrow the search tableNamePattern - a table
	 * name pattern; must match the table name as it is stored in the database
	 * types - a list of table types, which must be from the list of table types
	 * returned from getTableTypes(); null returns all types
	 */
	@Override
	public ResultSet getTables(String catalog, String schemaPattern,
			String tableNamePattern, String[] types) throws SQLException {
		/*
		 * TABLE_CAT String => table catalog (may be null) TABLE_SCHEM String =>
		 * table schema (may be null) TABLE_NAME String => table name TABLE_TYPE
		 * String => table type. Typical types are "TABLE", "VIEW",
		 * "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
		 * "SYNONYM". REMARKS String => explanatory comment on the table
		 * TYPE_CAT String => the types catalog (may be null) TYPE_SCHEM String
		 * => the types schema (may be null) TYPE_NAME String => type name (may
		 * be null) SELF_REFERENCING_COL_NAME String => name of the designated
		 * "identifier" column of a typed table (may be null) REF_GENERATION
		 * String => specifies how values in SELF_REFERENCING_COL_NAME are
		 * created. Values are "SYSTEM", "USER", "DERIVED". (may be null)
		 */
		System.out.println("MongoDatabaseMetaData.getTables() " + catalog+";" + schemaPattern+";" +tableNamePattern);
		// TODO cache the table list
		Set<String> names = connection.getDb().getCollectionNames();
		Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
		for (String name : names) {
			Map<String, Object> obj = new HashMap<String, Object>();
			obj.put("TABLE_CAT", catalog);
			obj.put("TABLE_SCHEM", schemaPattern);
			obj.put("TABLE_NAME", name);
			if (name.startsWith("system.")) {
				obj.put("TABLE_TYPE", "SYSTEM TABLE");
			} else if (name.equals(JDBC_TABLEDEFS_COLLECTION)) {
				obj.put("TABLE_TYPE", "SYSTEM TABLE");
			} else {
				obj.put("TABLE_TYPE", "TABLE");
			}
			obj.put("REMARKS", name);
			obj.put("TYPE_CAT", null);
			obj.put("TYPE_SCHEM", null);
			obj.put("TYPE_NAME", null);
			obj.put("SELF_REFERENCING_COL_NAME", null);
			obj.put("REF_GENERATION", null);
			if (null == types) {
				values.add(obj);
			} else {
				for (String type : types) {
					if (type.equals(obj.get("TABLE_TYPE"))) {
						values.add(obj);
						break;
					}
				}
			}
		}
		DBObject cols = new BasicDBObject();
		cols.put("TABLE_CAT", "");
		cols.put("TABLE_SCHEM", "");
		cols.put("TABLE_NAME", "");
		cols.put("TABLE_TYPE", "");
		cols.put("REMARKS", "");
		cols.put("TYPE_CAT", "");
		cols.put("TYPE_SCHEM", "");
		cols.put("TYPE_NAME", "");
		cols.put("SELF_REFERENCING_COL_NAME", "");
		cols.put("REF_GENERATION", "");
		return new SimpleResultSet(connection.getDb(), cols, values);
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		//we don't currently support time or date functions
		return "";
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException {
		return MongoDatabaseTypeInfo.getTypeInfo(connection.getDb());
	}

	@Override
	public ResultSet getUDTs(String arg0, String arg1, String arg2, int[] arg3)
			throws SQLException {
		//we don't currently support user defined types so we return an empty result set
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public String getURL() throws SQLException {
		return connection.getConnectionInfo().getUrl();
	}

	@Override
	public String getUserName() throws SQLException {
		return connection.getConnectionInfo().getUsername();
	}

	@Override
	public ResultSet getVersionColumns(String arg0, String arg1, String arg2)
			throws SQLException {
		return new EmptyResultSet(connection.getDb());
	}

	@Override
	public boolean insertsAreDetected(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		return false;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException {
		return false;
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		return true;
	}

	@Override
	public boolean othersDeletesAreVisible(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean othersInsertsAreVisible(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean othersUpdatesAreVisible(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean ownDeletesAreVisible(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean ownInsertsAreVisible(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean ownUpdatesAreVisible(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsConvert(int arg0, int arg1) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions()
			throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly()
			throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsResultSetConcurrency(int arg0, int arg1)
			throws SQLException {
		return false;
	}

	@Override
	public boolean supportsResultSetHoldability(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsResultSetType(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSavepoints() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTransactionIsolationLevel(int arg0)
			throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		return false;
	}

	@Override
	public boolean updatesAreDetected(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		return false;
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return false;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new NotYetImplementedException("Not yet implemented");
	}

}
