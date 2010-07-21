package com.mongodb.jdbc;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class Stuff {
	public static final String _AIXBOMS_CONNECTION_URL = "www.mcrobert.eu:27017/aixboms";
	public static final String _TEST_CONNECTION_URL = "www.mcrobert.eu:27017/test";
	public static final String _JDBCTEST_CONNECTION_URL = "www.mcrobert.eu:27017/jdbctest";
	public static final String _LOCATION_CONNECTION_URL = "www.mcrobert.eu:27017/location";

	public static void main(String[] args) {
		//if (createUsers()) {
		//	return;
		//}
		if (testSelect()) {
			return;
		}
		
		
		DBAddress addr;
		try {
			addr = new DBAddress(_AIXBOMS_CONNECTION_URL);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		// _mongo = new Mongo(addr);
		DB db = Mongo.connect(addr);
		DBCollection coll1 = db.getCollection("test");
		DBObject newobj = new BasicDBObject();
		newobj.put("a", "b");
		coll1.insert(newobj);
		for (String string : db.getCollectionNames()) {
			System.out.println("Stuff.main() " + string);
		}
		//BasicDBObject def = new BasicDBObject();
		db.addUser("abc", "123".toCharArray());
		
		DBCollection coll = db.getCollection("jdbc.tabledefs");
		DBObject index = new BasicDBObject();
		index.put("TABLE_NAME", "");
		coll.createIndex(index);
		DBObject filter = new BasicDBObject();
		filter.put("TABLE_NAME", "COCO_COMPONENT");
		DBObject orderBy = new BasicDBObject();
		orderBy.put("COLUMN_NAME", "");
		DBCursor cursor = coll.find(filter).sort(orderBy);
		System.out.println("Columns: ");
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			System.out.println("A: " + obj);
		}
		//BasicDBObject def = new BasicDBObject();
		//def.put("abc", "123");
		//coll.insert(def);
		//coll.drop();
		// db.addUser("abc", "123".toCharArray());
		if (cursor.hasNext()) {
			test1();
		}
	}
	private static boolean testSelect() {
		DBAddress addr;
		try {
			addr = new DBAddress(_TEST_CONNECTION_URL);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return true;
		}
		DB db = Mongo.connect(addr);
		DBCollection coll1 = db.getCollection("test");
		DBObject newobj = new BasicDBObject();
		//newobj.put("int1", 1);
		DBObject found = coll1.findOne(newobj);
		System.out.println("Stuff.testSelect() found: " + found);
		DBObject keys = BasicDBObjectBuilder.start().add("obj1", 1).add("str1", 1).add("int1", "1").get();
		DBCursor cursor = coll1.find(newobj, keys);
		while (cursor.hasNext()) {
			System.out.println("Stuff.testSelect() next: " + cursor.next());
		}
		return true;
	}
	private static boolean createUsers() {
		createUser(_TEST_CONNECTION_URL);
		createUser(_JDBCTEST_CONNECTION_URL);
		createUser(_LOCATION_CONNECTION_URL);
		return true;
	}
	private static boolean createUser(String url) {
		// TODO Auto-generated method stub
		DBAddress addr;
		try {
			addr = new DBAddress(url);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return true;
		}
		DB db = Mongo.connect(addr);
		db.addUser("abc", "123".toCharArray());
		return true;
	}
	private static void test1() {
		MongoDriver.install();
		try {
			Connection c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL);
			Statement s = c.createStatement();
			String sqlSimpleSelect = "SELECT int1, int2 FROM test WHERE int1 = 11";
			if (s.execute(sqlSimpleSelect)) {
				ResultSet rs = s.getResultSet();
				while (rs.next()) {
					System.out.println("Normal: " + rs.getInt(1));
				}
			}
			PreparedStatement ps = c.prepareStatement(sqlSimpleSelect);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					System.out.println("Prepared: " + rs.getInt(1));
				}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
