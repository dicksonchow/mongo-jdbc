package com.mongodb.jdbc;

import java.sql.ResultSet;
import java.sql.Statement;

import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class InsertTest extends QueryTest {

	@Test
	public void testInsert() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleInsert = "INSERT INTO test (col1, col2, col3, col4, col5, col6, col7, col8, col9) VALUES (1, 2, 3, 4, 5, 6, 7, 8, 9)";
		assertFalse(s.execute(sqlSimpleInsert));
		ResultSet rs = s.getGeneratedKeys();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertNotNull(rs.getString("_id"));
		// System.out.println("InsertTest.testInsert() new id = " +
		// rs.getString("_id"));
		assertFalse(rs.next());
		// MongoResultSet res = new MongoResultSet(coll.find(new
		// BasicDBObject("_id", rs.getString("_id"))));
		// System.out.println("InsertTest.testInsert() _id type = " +
		// rs.getObject("_id").getClass().getName());
		DBObject newObj = findId(rs.getObject("_id"));
		assertNotNull(newObj);
	}

	@Test
	public void testInsertObject() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleInsert = "INSERT INTO test (info, col1, col2, col3, col4, col5, col6, col7, col8, col9) VALUES ('{\"x\":1,\"y\":2}', 1, 2, 3, 4, 5, 6, 7, 8, 9)";
		assertFalse(s.execute(sqlSimpleInsert));
		ResultSet rs = s.getGeneratedKeys();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertNotNull(rs.getString("_id"));
		// System.out.println("InsertTest.testInsert() new id = " +
		// rs.getString("_id"));
		assertFalse(rs.next());
		// MongoResultSet res = new MongoResultSet(coll.find(new
		// BasicDBObject("_id", rs.getString("_id"))));
		// System.out.println("InsertTest.testInsert() _id type = " +
		// rs.getObject("_id").getClass().getName());
		DBObject newObj = findId(rs.getObject("_id"));
		assertNotNull(newObj);
		Object infoObj = newObj.get("info");
		//System.out.println("InsertTest.testInsertObject() " + infoObj.getClass().getName() + " = " + infoObj);
		assertEquals("com.mongodb.BasicDBObject", infoObj.getClass().getName());
		BasicDBObject info = (BasicDBObject) infoObj;
		assertEquals(1, info.get("x"));
		assertEquals(2, info.get("y"));

	}

}
