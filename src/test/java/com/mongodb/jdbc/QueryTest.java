package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class QueryTest extends com.mongodb.util.TestCase {

	protected Connection c = null;

	private DBObject createObject(int offset) {
		DBObject obj = new BasicDBObject();
		for (int i = 1; i < 10; i++) {
			obj.put("int" + i, offset + i);
			obj.put("str" + i, "" + (offset + i));
		}
		BasicDBObject info = new BasicDBObject();

		info.put("x", offset + 0.1);
		info.put("y", offset + 0.1);

		obj.put("info", info);
		return obj;
	}

	protected void setupDb() throws Exception {
		DBAddress addr = new DBAddress(Base._CONNECTION_URL);
		DB db = Mongo.connect(addr);
		db.dropDatabase();
		db = Mongo.connect(addr);
		DBCollection coll = db.getCollection("test");
		for (int i = 0; i < 10; i++) {
			DBObject obj = createObject(i * 10);
			coll.insert(obj);
		}
		coll = null;
		MongoDriver.install();
		c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL);
	}

	protected DBObject findId(Object id) throws Exception {
		return findOneObject(new BasicDBObject("_id", id));
	}

	protected DBObject findOneObject(DBObject filter) throws Exception {
		DBAddress addr = new DBAddress(Base._CONNECTION_URL);
		DB db = Mongo.connect(addr);
		DBCollection coll = db.getCollection("test");
		DBCursor cursor = coll.find(filter);
		assertTrue(cursor.hasNext());
		DBObject res = cursor.next();
		assertFalse(cursor.hasNext());
		return res;
	}

}
