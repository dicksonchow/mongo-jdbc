package com.mongodb.jdbc;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.Mongo;

public abstract class Base extends com.mongodb.util.TestCase {
	public static final String TESTIP = "188.40.69.209";
	public static final String JDBC_BAD_CONNECTION_URL = "mongo://localhost/jdbctest";
	public static final String JDBC_CONNECTION_URL = "mongodb://" + TESTIP
			+ ":27017/jdbctest";
	public static final String TEST_CONNECTION_URL = "mongodb://" + TESTIP
	+ ":27017/test";
	public static final String _CONNECTION_URL = TESTIP + ":27017/jdbctest";
	public static final String _TEST_CONNECTION_URL = TESTIP + ":27017/test";

	final DB _db;

	public Base() {
		try {
			DBAddress addr = new DBAddress(_CONNECTION_URL);
			_db = Mongo.connect(addr);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
