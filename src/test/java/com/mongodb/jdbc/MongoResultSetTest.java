// MongoResultSetTest.java

package com.mongodb.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoResultSetTest extends Base {

	@Test
	public void testbasic1() {
		DBCollection c = _db.getCollection("rs.basic1");
		c.drop();
		DBObject obj = BasicDBObjectBuilder.start("x", 1).add("y", "foo").get();
		c.insert(obj);
		c.insert(BasicDBObjectBuilder.start("x", 2).add("y", "bar").get());
		Map<Integer, String> fields = new HashMap<Integer, String>();
		fields.put(1, "x");
		fields.put(2, "y");
		MongoResultSetMetaData meta = new MongoResultSetMetaData(_db, obj, fields, "rs.basic1");
		MongoResultSet res = new MongoResultSet(_db, c.find().sort(
				new BasicDBObject("x", 1)), meta);
		assertTrue(res.next());
		assertEquals(1, res.getInt("x"));
		assertEquals("foo", res.getString("y"));
		assertTrue(res.next());
		assertEquals(2, res.getInt("x"));
		assertEquals("bar", res.getString("y"));
		assertFalse(res.next());
	}

	@Test
	public void testorder1() {
		DBCollection c = _db.getCollection("rs.basic1");
		c.drop();
		DBObject obj = BasicDBObjectBuilder.start("x", 1).add("y", "foo").get();
		c.insert(obj);
		c.insert(BasicDBObjectBuilder.start("x", 2).add("y", "bar").get());

		Map<Integer, String> fields = new HashMap<Integer, String>();
		fields.put(1, "x");
		fields.put(2, "y");
		MongoResultSetMetaData meta = new MongoResultSetMetaData(_db, obj, fields, "rs.basic1");
		MongoResultSet res = new MongoResultSet(_db, c.find(new BasicDBObject(),
				BasicDBObjectBuilder.start("x", 1).add("y", 1).get()).sort(
				new BasicDBObject("x", 1)), meta);
		assertTrue(res.next());
		assertEquals(1, res.getInt(1));
		assertEquals("foo", res.getString(2));
		assertTrue(res.next());
		assertEquals(2, res.getInt(1));
		assertEquals("bar", res.getString(2));
		assertFalse(res.next());

		fields = new HashMap<Integer, String>();
		fields.put(1, "y");
		fields.put(2, "x");
		meta = new MongoResultSetMetaData(_db, obj, fields, "rs.basic1");
		res = new MongoResultSet(_db, c.find(new BasicDBObject(),
				BasicDBObjectBuilder.start("y", 1).add("x", 1).get()).sort(
				new BasicDBObject("x", 1)), meta);
		assertTrue(res.next());
		assertEquals(1, res.getInt(2));
		assertEquals("foo", res.getString(1));
		assertTrue(res.next());
		assertEquals(2, res.getInt(2));
		assertEquals("bar", res.getString(1));
		assertFalse(res.next());
	}

}
