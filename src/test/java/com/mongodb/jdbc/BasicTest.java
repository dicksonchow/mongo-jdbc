// BasicTest.java

package com.mongodb.jdbc;

import java.sql.Connection;

import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class BasicTest extends Base {

	public BasicTest() {
	}

	@Test
	public void test1() throws Exception {
		String name = "simple.test1";
		DBCollection c = _db.getCollection(name);
		c.drop();

		for (int i = 1; i <= 3; i++) {
			c.insert(BasicDBObjectBuilder.start("a", i).add("b", i).add("x", i)
					.get());
		}

		DBObject empty = new BasicDBObject();
		DBObject ab = BasicDBObjectBuilder.start("a", 1).add("b", 1).get();
		assertEquals(c.find().toArray(), new Executor(null, _db, "select * from "
				+ name).query().toArray());
		assertEquals(c.find(empty, ab).toArray(), new Executor(null, _db,
				"select a,b from " + name).query().toArray());
		assertEquals(c.find(new BasicDBObject("x", 3), ab).toArray(),
				new Executor(null, _db, "select a,b from " + name + " where x=3")
						.query().toArray());

	}
}
