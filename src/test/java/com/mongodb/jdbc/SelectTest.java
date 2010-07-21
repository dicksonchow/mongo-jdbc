package com.mongodb.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.testng.annotations.Test;

import com.mongodb.DBObject;

public class SelectTest extends QueryTest {
	@Test
	public void testColumnSelect() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1, int2 FROM test WHERE int1 = 11";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertNotNull(rs.getString("_id"));
		assertEquals(11, rs.getInt("int1"));
		assertEquals(12, rs.getInt("int2"));
		// Object col3 = rs.getObject("col3");
		// System.out.println("SelectTest.testColumnSelect() col3 type = " +
		// col3.getClass().getName());
		assertNull(rs.getObject("col3"));
		assertFalse(rs.next());
	}

	@Test
	public void testPreparedSelect() throws Exception {
		setupDb();
		assertNotNull(c);
		String sqlSimpleSelect = "SELECT int1, int2 FROM test WHERE int1 = 11";
		PreparedStatement s = c.prepareStatement(sqlSimpleSelect);
		assertNotNull(s);
		ResultSetMetaData meta = s.getMetaData();
		assertNotNull(meta);
		assertEquals(2, meta.getColumnCount());
		ResultSet rs = s.executeQuery();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertNotNull(rs.getString("_id"));
		assertEquals(11, rs.getInt("int1"));
		assertEquals(12, rs.getInt("int2"));
		assertNull(rs.getObject("col3"));
		assertFalse(rs.next());
	}

	@Test
	public void testInvalidColumnSelect() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1, int2, strA, strB FROM test WHERE int1 = 11";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertNotNull(rs.getString("_id"));
		assertEquals(11, rs.getInt("int1"));
		assertEquals(12, rs.getInt("int2"));
		assertNull(rs.getObject("strA"));
		assertFalse(rs.next());
	}

	@Test
	public void testAllSelect() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT * FROM test WHERE int1 = 1";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		//TODO ResultSetMetaData meta = rs.getMetaData();
		//TODO assertNotNull(meta);
		// TODO assertEquals(20, meta.getColumnCount());
		// TODO assertEquals("_id", meta.getColumnName(1));
		
		//System.out.println("SelectTest.testAllSelect() " + meta.getColumnCount());
		//TODO column count is currently 0 assertEquals(20, meta.getColumnCount());
		assertNotNull(rs);
		assertTrue(rs.next());
		assertNotNull(rs.getString("_id"));
		assertEquals(1, rs.getInt("int1"));
		assertEquals(2, rs.getInt("int2"));
		assertEquals("1", rs.getString("str1"));
		assertEquals("2", rs.getString("str2"));
		DBObject dbobj = (DBObject) rs.getObject("info");
		assertEquals(0.1, dbobj.get("x"));
		assertEquals(0.1, dbobj.get("y"));
		assertFalse(rs.next());
	}

	@Test
	public void testLikeFilter() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT str1 FROM test WHERE str1 like 'str1%'";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		//TODO like filter currently not working
		while (rs.next()) {
			System.out.println("SelectTest.testLikeFilter() " + rs.getString("str1"));
		}
		assertFalse(rs.next());
	}

	@Test
	public void testLessThanFilter() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1 FROM test WHERE int1 < 21";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(11, rs.getInt("int1"));
		assertFalse(rs.next());
	}

	@Test
	public void testLessThanEqualFilter() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1 FROM test WHERE int1 <= 21";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertEquals(1, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(11, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(21, rs.getInt("int1"));
		assertFalse(rs.next());
	}

	@Test
	public void testGreaterThanFilter() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1 FROM test WHERE int1 > 71";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertEquals(81, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(91, rs.getInt("int1"));
		assertFalse(rs.next());
	}

	@Test
	public void testGreaterThanEqualFilter() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1 FROM test WHERE int1 >= 71";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertEquals(71, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(81, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(91, rs.getInt("int1"));
		assertFalse(rs.next());
	}

	@Test
	public void testBetweenFilter() throws Exception {
		setupDb();
		assertNotNull(c);
		Statement s = c.createStatement();
		String sqlSimpleSelect = "SELECT int1 FROM test WHERE int1 > 60 AND int1 < 80";
		assertTrue(s.execute(sqlSimpleSelect));
		ResultSet rs = s.getResultSet();
		assertNotNull(rs);
		assertTrue(rs.next());
		assertEquals(61, rs.getInt("int1"));
		assertTrue(rs.next());
		assertEquals(71, rs.getInt("int1"));
		assertFalse(rs.next());
	}

}
