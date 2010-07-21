package com.mongodb.jdbc;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoColumnMetaData {
	private static final int MAX_CHAR_FIELD_LENGTH = 4096;
	
	public static final String TABLE_CAT = "TABLE_CAT";
	public static final String TABLE_SCHEM = "TABLE_SCHEM";
	public static final String TABLE_NAME = "TABLE_NAME";
	public static final String COLUMN_NAME = "COLUMN_NAME";
	public static final String DATA_TYPE = "DATA_TYPE";
	public static final String TYPE_NAME = "TYPE_NAME";
	public static final String COLUMN_SIZE = "COLUMN_SIZE";
	public static final String BUFFER_LENGTH = "BUFFER_LENGTH";
	public static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
	public static final String NUM_PREC_RADIX = "NUM_PREC_RADIX";
	public static final String NULLABLE = "NULLABLE";
	public static final String REMARKS = "REMARKS";
	public static final String COLUMN_DEF = "COLUMN_DEF";
	public static final String SQL_DATA_TYPE = "SQL_DATA_TYPE";
	public static final String SQL_DATETIME_SUB = "SQL_DATETIME_SUB";
	public static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";
	public static final String ORDINAL_POSITION = "ORDINAL_POSITION";
	public static final String IS_NULLABLE = "IS_NULLABLE";
	public static final String SCOPE_CATLOG = "SCOPE_CATLOG";
	public static final String SCOPE_SCHEMA = "SCOPE_SCHEMA";
	public static final String SCOPE_TABLE = "SCOPE_TABLE";
	public static final String SOURCE_DATA_TYPE = "SOURCE_DATA_TYPE";
	public static final String IS_AUTOINCREMENT = "IS_AUTOINCREMENT";

	private DBObject column;

	public MongoColumnMetaData(DBObject column) {
		this.column = column;
	}

	public MongoColumnMetaData(String tableCat, String tableSchem, String tableName, String columnName, int dataType, int ordinalPosition) {
		this.column = new BasicDBObject();
		setTableCat(tableCat);
		setTableSchem(tableSchem);
		setTableName(tableName);
		setColumnName(columnName);
		setDataType(dataType);
		setTypeName(getTypeNameFromDataType(dataType));
		setColumnSize(10); //TODO
		setBufferLength(0);
		setDecimalDigits(null); //TODO
		setNumPrecRadix(10);
		setNullable(DatabaseMetaData.columnNullable);
		setRemarks("Column " + columnName);
		setColumnDef(null);
		setSqlDataType(0);
		setSqlDatetimeSub(0);
		setCharOctetLength(MAX_CHAR_FIELD_LENGTH); //TODO
		setOrdinalPosition(ordinalPosition);
		setIsNullable("YES");
		setScopeCatalog(null);
		setScopeSchema(null);
		setScopeType(null);
		setSourceDataType(null);
		setIsAutoincrement("NO");
	}

	private String getTypeNameFromDataType(int dataType) {
		String name = "Unknown " + dataType;
		switch (dataType) {
		case java.sql.Types.CHAR:
			name = "String";
			break;
		case java.sql.Types.INTEGER:
			name = "Integer";
			break;
		case java.sql.Types.JAVA_OBJECT:
			name = "DBObject";
			break;
		default:
			break;
		}
		return name;
	}

	public DBObject getColumn() {
		return column;
	}

	// TABLE_CAT String => table catalog (may be null)
	public String getTableCat() {
		return (String) column.get(TABLE_CAT);
	}

	public void setTableCat(String tableCat) {
		column.put(TABLE_CAT, tableCat);
	}

	// TABLE_SCHEM String => table schema (may be null)
	public String getTableSchem() {
		return (String) column.get(TABLE_SCHEM);
	}

	public void setTableSchem(String tableSchem) {
		column.put(TABLE_SCHEM, tableSchem);
	}

	// TABLE_NAME String => table name
	public String getTableName() {
		return (String) column.get(TABLE_NAME);
	}

	public void setTableName(String tableName) {
		column.put(TABLE_NAME, tableName);
	}

	// COLUMN_NAME String => column name
	public String getColumnName() {
		return (String) column.get(COLUMN_NAME);
	}

	public void setColumnName(String columnName) {
		column.put(COLUMN_NAME, columnName);
	}

	// DATA_TYPE int => SQL type from java.sql.Types
	public int getDataType() {
		// TODO handle case where DATA_TYPE does not exist
		return ((Integer) column.get(DATA_TYPE)).intValue();
	}

	public void setDataType(int dataType) {
		column.put(DATA_TYPE, dataType);
	}

	// TYPE_NAME String => Data source dependent type name,
	// for a UDT the type name is fully qualified
	public String getTypeName() {
		return (String) column.get(TYPE_NAME);
	}

	public void setTypeName(String typeName) {
		column.put(TYPE_NAME, typeName);
	}

	// COLUMN_SIZE int => column size.
	public Integer getColumnSize() {
		return (Integer) column.get(COLUMN_SIZE);
	}

	public void setColumnSize(Integer columnSize) {
		column.put(COLUMN_SIZE, columnSize);
	}

	// BUFFER_LENGTH is not used.
	public int getBufferLength() {
		// TODO handle case where DATA_TYPE does not exist
		return ((Integer) column.get(BUFFER_LENGTH)).intValue();
	}

	public void setBufferLength(int bufferLength) {
		column.put(BUFFER_LENGTH, bufferLength);
	}

	// DECIMAL_DIGITS int => the number of fractional digits. Null is
	// returned for data types where DECIMAL_DIGITS is not applicable.
	public Integer getDecimalDigits() {
		return (Integer) column.get(DECIMAL_DIGITS);
	}

	public void setDecimalDigits(Integer decimalDigits) {
		column.put(DECIMAL_DIGITS, decimalDigits);
	}

	// NUM_PREC_RADIX int => Radix (typically either 10 or 2)
	public Integer getNumPrecRadix() {
		return (Integer) column.get(NUM_PREC_RADIX);
	}

	public void setNumPrecRadix(Integer numPrecRadix) {
		column.put(NUM_PREC_RADIX, numPrecRadix);
	}

	// NULLABLE int => is NULL allowed.
	//   columnNoNulls - might not allow NULL values 
	//   columnNullable - definitely allows NULL values 
	//   columnNullableUnknown - nullability unknown 
	public Integer getNullable() {
		return (Integer) column.get(NULLABLE);
	}

	public void setNullable(Integer nullable) {
		column.put(NULLABLE, nullable);
	}

	// REMARKS String => comment describing column (may be null) 
	public String getRemarks() {
		return (String) column.get(REMARKS);
	}

	public void setRemarks(String remarks) {
		column.put(REMARKS, remarks);
	}

	// COLUMN_DEF String => default value for the column, which should be interpreted as a string when the value is enclosed in single quotes (may be null) 
	public String getColumnDef() {
		return (String) column.get(COLUMN_DEF);
	}

	public void setColumnDef(String columnDef) {
		column.put(COLUMN_DEF, columnDef);
	}

	// SQL_DATA_TYPE int => unused 
	public Integer getSqlDataType() {
		return (Integer) column.get(SQL_DATA_TYPE);
	}

	public void setSqlDataType(Integer sqlDataType) {
		column.put(SQL_DATA_TYPE, sqlDataType);
	}

	// SQL_DATETIME_SUB int => unused 
	public Integer getSqlDatetimeSub() {
		return (Integer) column.get(SQL_DATETIME_SUB);
	}

	public void setSqlDatetimeSub(Integer sqlDatetimeSub) {
		column.put(SQL_DATETIME_SUB, sqlDatetimeSub);
	}
	
	// CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the column 
	public Integer getCharOctetLength() {
		return (Integer) column.get(CHAR_OCTET_LENGTH);
	}

	public void setCharOctetLength(Integer charOctetLength) {
		column.put(CHAR_OCTET_LENGTH, charOctetLength);
	}

	// ORDINAL_POSITION int => index of column in table (starting at 1) 
	public Integer getOrdinalPosition() {
		return (Integer) column.get(ORDINAL_POSITION);
	}

	public void setOrdinalPosition(Integer ordinalPosition) {
		column.put(ORDINAL_POSITION, ordinalPosition);
	}

	// IS_NULLABLE String => ISO rules are used to determine the nullability for a column. 
	//   YES --- if the parameter can include NULLs 
	//   NO --- if the parameter cannot include NULLs 
	//   empty string --- if the nullability for the parameter is unknown 
	public String getIsNullable() {
		return (String) column.get(IS_NULLABLE);
	}

	public void setIsNullable(String isNullable) {
		column.put(IS_NULLABLE, isNullable);
	}

	// SCOPE_CATLOG String => catalog of table that is the scope of a reference attribute (null if DATA_TYPE isn't REF) 
	public String getScopeCatalog() {
		return (String) column.get(SCOPE_CATLOG);
	}

	public void setScopeCatalog(String scopeCatalog) {
		column.put(SCOPE_CATLOG, scopeCatalog);
	}

	// SCOPE_SCHEMA String => schema of table that is the scope of a reference attribute (null if the DATA_TYPE isn't REF) 
	public String getScopeSchema() {
		return (String) column.get(SCOPE_SCHEMA);
	}

	public void setScopeSchema(String scopeSchema) {
		column.put(SCOPE_SCHEMA, scopeSchema);
	}

	// SCOPE_TABLE String => table name that this the scope of a reference attribure (null if the DATA_TYPE isn't REF) 
	public String getScopeType() {
		return (String) column.get(SCOPE_TABLE);
	}

	public void setScopeType(String scopeType) {
		column.put(SCOPE_TABLE, scopeType);
	}

	// SOURCE_DATA_TYPE short => source type of a distinct type or user-generated Ref type, SQL type from java.sql.Types (null if DATA_TYPE isn't DISTINCT or user-generated REF) 
	public Integer getSourceDataType() {
		return (Integer) column.get(SOURCE_DATA_TYPE);
	}

	public void setSourceDataType(Integer sourceDataType) {
		column.put(SOURCE_DATA_TYPE, sourceDataType);
	}

	// IS_AUTOINCREMENT String => Indicates whether this column is auto incremented 
	//   YES --- if the column is auto incremented 
	//   NO --- if the column is not auto incremented 
	//   empty string --- if it cannot be determined whether the column is auto incremented parameter is unknown 
	public String getIsAutoincrement() {
		return (String) column.get(IS_AUTOINCREMENT);
	}

	public void setIsAutoincrement(String isAutoincrement) {
		column.put(IS_AUTOINCREMENT, isAutoincrement);
	}

	public static DBObject getDummyObject() {
		MongoColumnMetaData dummy = new MongoColumnMetaData("", "", "", "", 1, 1);
		return dummy.getColumn();
	}
	
	private static Map<Integer, String> keys = null;

	public static Map<Integer, String> getKeys() {
		if (null == keys) {
			keys = new HashMap<Integer, String>();
			keys.put(1, TABLE_CAT);
			keys.put(2, TABLE_SCHEM);
			keys.put(3, TABLE_NAME);
			keys.put(4, COLUMN_NAME);
			keys.put(5, DATA_TYPE);
			keys.put(6, TYPE_NAME);
			keys.put(7, COLUMN_SIZE);
			keys.put(8, BUFFER_LENGTH);
			keys.put(9, DECIMAL_DIGITS);
			keys.put(10, NUM_PREC_RADIX);
			keys.put(11, NULLABLE);
			keys.put(12, REMARKS);
			keys.put(13, COLUMN_DEF);
			keys.put(14, SQL_DATA_TYPE);
			keys.put(15, SQL_DATETIME_SUB);
			keys.put(16, CHAR_OCTET_LENGTH);
			keys.put(17, ORDINAL_POSITION);
			keys.put(18, IS_NULLABLE);
			keys.put(19, SCOPE_CATLOG);
			keys.put(20, SCOPE_SCHEMA);
			keys.put(21, SCOPE_TABLE);
			keys.put(22, SOURCE_DATA_TYPE);
			keys.put(23, IS_AUTOINCREMENT);
		}
		return keys;
	}

}
