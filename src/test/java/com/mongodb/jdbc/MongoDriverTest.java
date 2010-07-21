// DriverTest.java

package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.testng.annotations.Test;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.Mongo;

public class MongoDriverTest extends com.mongodb.util.TestCase {

	public static final String PASSWD = "123";
	public static final String USERNAME = "abc";

	// public static final String NOPWDUSERNAME = "nopwd";

	@Test
	public void test1() throws Exception {

		Connection c = null;
		try {
			c = DriverManager.getConnection(Base.JDBC_BAD_CONNECTION_URL);
		} catch (Exception e) {
		}

		assertNull(c);

		MongoDriver.install();
		c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL);
		assertNotNull(c);
	}

	@Test
	public void testUser() throws Exception {

		DBAddress addr = new DBAddress(Base._CONNECTION_URL);
		// _mongo = new Mongo(addr);
		DB db = Mongo.connect(addr);
		db.dropDatabase();
		db = Mongo.connect(addr);
		db.addUser(USERNAME, PASSWD.toCharArray());
		// try {
		// db.addUser(NOPWDUSERNAME, null);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		Connection c = null;
		MongoDriver.install();
		c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL, USERNAME,
				PASSWD);
		assertNotNull(c);

		c = null;
		Properties props = new Properties();
		props.put("user", USERNAME);
		props.put("password", PASSWD);
		c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL, props);
		assertNotNull(c);

		c = null;
		props = new Properties();
		props.put("password", USERNAME);
		props.put("user", PASSWD);
		try {
			c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL, props);
			assertNull(c);
		} catch (SQLException e) {
			assert e.getMessage().equals("Could not authenticate user '123'.");
		}

		// c = null;
		// props.put("user", NOPWDUSERNAME);
		// c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL, props);
		// assertNotNull(c);
	}

}
