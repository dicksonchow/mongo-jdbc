package com.mongodb.jdbc;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;

public class MongoDatabaseTypeInfo {

	//private static Map<String, ResultSet> typeInfos = new HashMap<String, ResultSet>();

	public static ResultSet getTypeInfo(DB db) {
		//if (!typeInfos.containsKey(db.getName())) {
			//Set<String> names = db.getCollectionNames();
			Set<Map<String, Object>> values = new HashSet<Map<String, Object>>();
			values.add(createInteger());
			values.add(createString());
			values.add(createObject());
			DBObject cols = new BasicDBObject();
			cols.put("TYPE_NAME", "");
			cols.put("DATA_TYPE", new Integer(1)); //int
			cols.put("PRECISION", new Integer(1)); //int
			cols.put("LITERAL_PREFIX", "");
			cols.put("LITERAL_SUFFIX", "");
			cols.put("CREATE_PARAMS", "");
			cols.put("NULLABLE", new Integer(1));
			cols.put("CASE_SENSITIVE", true);
			cols.put("SEARCHABLE", new Integer(1));
			cols.put("UNSIGNED_ATTRIBUTE", true);
			cols.put("FIXED_PREC_SCALE", true);
			cols.put("AUTO_INCREMENT", true);
			cols.put("LOCAL_TYPE_NAME", "");
			cols.put("MINIMUM_SCALE", new Integer(1));
			cols.put("MAXIMUM_SCALE", new Integer(1));
			cols.put("SQL_DATA_TYPE", new Integer(1)); //int
			cols.put("SQL_DATETIME_SUB", new Integer(1)); //int
			cols.put("NUM_PREC_RADIX", new Integer(1)); //int
			//typeInfos.put(db.getName(), new SimpleResultSet(db, cols, values));
		//}
		//return typeInfos.get(db.getName());
			return new SimpleResultSet(db, cols, values);
	}
	// TYPE_NAME String => Type name
	// DATA_TYPE int => SQL data type from java.sql.Types
	// PRECISION int => maximum precision
	// LITERAL_PREFIX String => prefix used to quote a literal (may be null)
	// LITERAL_SUFFIX String => suffix used to quote a literal (may be null)
	// CREATE_PARAMS String => parameters used in creating the type (may be null)
	// NULLABLE short => can you use NULL for this type.
	//   typeNoNulls - does not allow NULL values
	//   typeNullable - allows NULL values
	//   typeNullableUnknown - nullability unknown
	// CASE_SENSITIVE boolean=> is it case sensitive.
	// SEARCHABLE short => can you use "WHERE" based on this type:
	//   typePredNone - No support
	//   typePredChar - Only supported with WHERE .. LIKE
	//   typePredBasic - Supported except for WHERE .. LIKE
	//   typeSearchable - Supported for all WHERE ..
	// UNSIGNED_ATTRIBUTE boolean => is it unsigned.
	// FIXED_PREC_SCALE boolean => can it be a money value.
	// AUTO_INCREMENT boolean => can it be used for an auto-increment value.
	// LOCAL_TYPE_NAME String => localized version of type name (may be null)
	// MINIMUM_SCALE short => minimum scale supported
	// MAXIMUM_SCALE short => maximum scale supported
	// SQL_DATA_TYPE int => unused
	// SQL_DATETIME_SUB int => unused
	// NUM_PREC_RADIX int => usually 2 or 10
	private static Map<String, Object> createObject() {
		Map<String, Object> value = new HashMap<String, Object>();
		value.put("TYPE_NAME", "Object");
		value.put("DATA_TYPE", java.sql.Types.JAVA_OBJECT); //int
		value.put("PRECISION", new Integer(0)); //int
		value.put("LITERAL_PREFIX", null);
		value.put("LITERAL_SUFFIX", null);
		value.put("CREATE_PARAMS", null);
		value.put("NULLABLE", java.sql.DatabaseMetaData.typeNullable);
		value.put("CASE_SENSITIVE", true);
		value.put("SEARCHABLE", java.sql.DatabaseMetaData.typeSearchable);
		value.put("UNSIGNED_ATTRIBUTE", false);
		value.put("FIXED_PREC_SCALE", false);
		value.put("AUTO_INCREMENT", false);
		value.put("LOCAL_TYPE_NAME", "DBObject");
		value.put("MINIMUM_SCALE", 0);
		value.put("MAXIMUM_SCALE", 0);
		value.put("SQL_DATA_TYPE", null); //int
		value.put("SQL_DATETIME_SUB", null); //int
		value.put("NUM_PREC_RADIX", 10); //int
		return value;
	}
	private static Map<String, Object> createString() {
		Map<String, Object> value = new HashMap<String, Object>();
		value.put("TYPE_NAME", "String");
		value.put("DATA_TYPE", java.sql.Types.CHAR); //int
		value.put("PRECISION", new Integer(0)); //int
		value.put("LITERAL_PREFIX", null);
		value.put("LITERAL_SUFFIX", null);
		value.put("CREATE_PARAMS", null);
		value.put("NULLABLE", java.sql.DatabaseMetaData.typeNullable);
		value.put("CASE_SENSITIVE", true);
		value.put("SEARCHABLE", java.sql.DatabaseMetaData.typeSearchable);
		value.put("UNSIGNED_ATTRIBUTE", false);
		value.put("FIXED_PREC_SCALE", false);
		value.put("AUTO_INCREMENT", false);
		value.put("LOCAL_TYPE_NAME", null);
		value.put("MINIMUM_SCALE", 0);
		value.put("MAXIMUM_SCALE", 0);
		value.put("SQL_DATA_TYPE", null); //int
		value.put("SQL_DATETIME_SUB", null); //int
		value.put("NUM_PREC_RADIX", 10); //int
		return value;
	}
	private static Map<String, Object> createInteger() {
		Map<String, Object> value = new HashMap<String, Object>();
		value.put("TYPE_NAME", "Integer");
		value.put("DATA_TYPE", java.sql.Types.INTEGER); //int
		value.put("PRECISION", new Integer(0)); //int
		value.put("LITERAL_PREFIX", null);
		value.put("LITERAL_SUFFIX", null);
		value.put("CREATE_PARAMS", null);
		value.put("NULLABLE", java.sql.DatabaseMetaData.typeNullable);
		value.put("CASE_SENSITIVE", true);
		value.put("SEARCHABLE", java.sql.DatabaseMetaData.typeSearchable);
		value.put("UNSIGNED_ATTRIBUTE", false);
		value.put("FIXED_PREC_SCALE", false);
		value.put("AUTO_INCREMENT", true);
		value.put("LOCAL_TYPE_NAME", null);
		value.put("MINIMUM_SCALE", 0);
		value.put("MAXIMUM_SCALE", 0);
		value.put("SQL_DATA_TYPE", null); //int
		value.put("SQL_DATETIME_SUB", null); //int
		value.put("NUM_PREC_RADIX", 10); //int
		return value;
	}
}
