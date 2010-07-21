package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.testng.annotations.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class TableTest extends com.mongodb.util.TestCase {

	protected Connection c = null;

	protected void setupDb() throws Exception {
		MongoDriver.install();
		//c = DriverManager.getConnection(Base.JDBC_CONNECTION_URL);
		c = DriverManager.getConnection(Base.TEST_CONNECTION_URL);
	}

	@Test
	public void testCreateTable() throws Exception {
		setupDb();
		MongoTableMetaData table = new MongoTableMetaData(null, null, "test");
		table.addIntColumn("int1");
		table.addStringColumn("str1");
		table.addObjectColumn("obj1");
		MongoDatabaseMetaData mgmeta = (MongoDatabaseMetaData) c.getMetaData();
		mgmeta.createTable(table);
		
		DatabaseMetaData meta = c.getMetaData();
		String[] types = new String[1];
		types[0] = "TABLE";
		ResultSet trs = meta.getTables(null, null, null, types);
		assertNotNull(trs);
		assertTrue(trs.next());
		//System.out.println("TableTest.testCreateTable() " + trs.getString(MongoColumnMetaData.TABLE_NAME));
		assertEquals("test", trs.getString(MongoColumnMetaData.TABLE_NAME));
		//assertTrue(trs.next());
		//System.out.println("TableTest.testCreateTable() " + trs.getString(MongoColumnMetaData.TABLE_NAME));
		//assertEquals("system.indexes", trs.getString(MongoColumnMetaData.TABLE_NAME));
		assertFalse(trs.next());
		
		ResultSet rs = meta.getColumns(null, null, "test", null);
		assertNotNull(rs);
		assertTrue(rs.next());
		assertEquals("int1", rs.getString(MongoColumnMetaData.COLUMN_NAME));
		assertEquals(java.sql.Types.INTEGER, rs.getInt(MongoColumnMetaData.DATA_TYPE));
		checkColumn(rs);
		assertTrue(rs.next());
		assertEquals("str1", rs.getString(MongoColumnMetaData.COLUMN_NAME));
		assertEquals(java.sql.Types.CHAR, rs.getInt(MongoColumnMetaData.DATA_TYPE));
		assertTrue(rs.next());
		assertEquals("obj1", rs.getString(MongoColumnMetaData.COLUMN_NAME));
		assertEquals(java.sql.Types.JAVA_OBJECT, rs.getInt(MongoColumnMetaData.DATA_TYPE));
		assertFalse(rs.next());
		printJdbcInfo();
		createData();
		Statement s = c.createStatement();
		ResultSet qrs = s.executeQuery("SELECT int1, obj1, str1 FROM test");
		assertNotNull(qrs);
		assertTrue(qrs.next());
		ResultSetMetaData qrsMeta = qrs.getMetaData();
		assertNotNull(qrsMeta);
		//System.out.println("TableTest.testCreateTable() " + qrsMeta.getColumnType(1));
		assertEquals(java.sql.Types.INTEGER, qrsMeta.getColumnType(1));
		//System.out.println("TableTest.testCreateTable() " + qrsMeta.getColumnType(2));
		assertEquals(java.sql.Types.JAVA_OBJECT, qrsMeta.getColumnType(2));
		//System.out.println("TableTest.testCreateTable() " + qrsMeta.getColumnType(3));
		assertEquals(java.sql.Types.CHAR, qrsMeta.getColumnType(3));
		checkWildcardSelect();
	}
	private void checkWildcardSelect() throws Exception{
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM test");
		assertNotNull(rs);
		assertTrue(rs.next());
		ResultSetMetaData meta = rs.getMetaData();
		assertNotNull(meta);
		// TODO assertEquals(4, meta.getColumnCount());
		//System.out.println("TableTest.testCreateTable() " + qrsMeta.getColumnType(1));
		//assertEquals(java.sql.Types.INTEGER, qrsMeta.getColumnType(1));
		//System.out.println("TableTest.testCreateTable() " + qrsMeta.getColumnType(2));
		//assertEquals(java.sql.Types.JAVA_OBJECT, qrsMeta.getColumnType(2));
		//System.out.println("TableTest.testCreateTable() " + qrsMeta.getColumnType(3));
		//assertEquals(java.sql.Types.CHAR, qrsMeta.getColumnType(3));
	}
	private void checkColumn(ResultSet rdr) {
		try {
			System.out.println("TableTest.checkColumn() = " + rdr.getString(1));
			assertEquals(null, rdr.getString(1)); // TABLE_CAT
			assertEquals(null, rdr.getString(2)); // TABLE_SCHEM
			assertEquals("test", rdr.getString(3)); // TABLE_NAME
			assertEquals("int1", rdr.getString(4)); // COLUMN_NAME
			assertEquals(java.sql.Types.INTEGER, new Long(rdr.getLong(5)).intValue()); // DATA_TYPE
			assertEquals("Integer", rdr.getString(6)); // TYPE_NAME
			assertEquals(10, new Long(rdr.getLong(7)).intValue()); // COLUMN_SIZE
			assertEquals(0, new Long(rdr.getLong(9)).intValue()); // DECIMAL_DIGITS
			assertEquals(10, new Long(rdr.getLong(10)).intValue()); // NUM_PREC_RADIX
			//assertEquals(isNullAllowed); // NULLABLE
			assertEquals("Column int1", rdr.getString(12)); // REMARKS
			assertEquals(null, rdr.getString(13)); // COLUMN_DEF
			assertEquals(4096, new Long(rdr.getLong(16)).intValue()); // CHAR_OCTET_LENGTH
			assertEquals(1, new Long(rdr.getLong(17)).intValue()); // ORDINAL_POSITION
			assertEquals("YES", rdr.getString(18)); // IS_NULLABLE
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createData() throws Exception {
		DBAddress addr = new DBAddress(Base._TEST_CONNECTION_URL);
		DB db = Mongo.connect(addr);
		DBCollection coll = db.getCollection("test");
		coll.drop();
		coll = db.getCollection("test");
		for (int i = 1; i < 10; i++) {
			DBObject obj = createObject(i);
			coll.insert(obj);
		}
	}

	private DBObject createObject(int offset) {
		DBObject obj = new BasicDBObject();
		obj.put("int1", offset);
		obj.put("str1", "" + (offset));
		BasicDBObject obj1 = new BasicDBObject();
		obj1.put("x", offset + 0.1);
		obj1.put("y", offset + 0.1);
		obj.put("obj1", obj1);
		return obj;
	}

	public void printJdbcInfo() throws Exception {
		DBAddress addr = new DBAddress(Base._CONNECTION_URL);
		DB db = Mongo.connect(addr);
		db = Mongo.connect(addr);
		DBCollection coll = db.getCollection(MongoDatabaseMetaData.JDBC_TABLEDEFS_COLLECTION);
		System.out.println("JdbcDefTest.testCreateTable() " + coll.getCount());
		DBCursor cursor = coll.find();
		while (cursor.hasNext()) {
			System.out.println("\t" + cursor.next().toString());
		}
	}
}
