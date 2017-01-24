// DriverTest.java

package com.mongodb.jdbc;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.Assert.assertNull;

public class DriverTest extends Base {

    @Test
    public void test1() throws Exception {

        Connection c = null;
        try {
            c = DriverManager.getConnection("mongodb://localhost/test");
        } catch (Exception e) {
        }

        assertNull(c);

        MongoDriver.install();
        c = DriverManager.getConnection("mongodb://localhost/test");
    }

}
