package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDatabaseMetaDataTest extends com.mongodb.util.TestCase {

	@Test
	public void testDbMetaData() throws Exception {

		DBAddress addr = new DBAddress(Base._CONNECTION_URL);
		// _mongo = new Mongo(addr);
		DB db = Mongo.connect(addr);
		db.dropDatabase();
		db = Mongo.connect(addr);
		DBCollection coll = db.getCollection("test");
		DBObject obj = new BasicDBObject();
		for (int i = 1; i < 10; i++) {
			obj.put("col" + i, i);
		}
		BasicDBObject info = new BasicDBObject();

		info.put("x", 203);
		info.put("y", 102);

		obj.put("info", info);
		coll.insert(obj);
		coll = null;
		db = null;

		Connection c = null;
		MongoDriver.install();
		c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL);
		assertNotNull(c);
		MongoDatabaseMetaData meta = new MongoDatabaseMetaData(
				(MongoConnection) c);
		assertEquals(2, meta.getDatabaseMajorVersion());
		assertEquals(0, meta.getDatabaseMinorVersion());
		ResultSet catalogs = meta.getCatalogs();
		assertNotNull(catalogs);
		//System.out.println("Catalogs: ");
		//TODO check catalogs while (catalogs.next()) {
		//	System.out.println("Catalog: " + catalogs.getString(1));
		//}
		ResultSet tables = meta.getTables(null, null, null, null);
		assertNotNull(tables);
		//System.out.println("Tables: ");
		//TODO check tables while (tables.next()) {
		//	System.out.println("Table: " + tables.getString("TABLE_NAME"));
		//}
		ResultSet attrs = meta.getColumns(null, null, "test", null);
		assertNotNull(attrs);
		//System.out.println("Attrs: ");
		//TODO check columns while (attrs.next()) {
		//	System.out.println("Attr: " + attrs.getString("COLUMN_NAME")
		//			+ " : " + attrs.getString("DATA_TYPE"));
		//}
	}
}
