// MongoDriver.java

/**
 *      Copyright (C) 2008 10gen Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.mongodb.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Properties;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDriver implements Driver {

	static final String PREFIX = "mongodb://";

	public MongoDriver() {
	}

	public boolean acceptsURL(String url) {
		return url.startsWith(PREFIX);
	}

	public Connection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) {
			return null;
		}
		if (url.startsWith(PREFIX))
			url = url.substring(PREFIX.length());
		if (url.indexOf("/") < 0)
			throw new MongoSQLException("bad url: " + url);
		DB db = null;
		try {
			DBAddress addr = new DBAddress(url);
			db = Mongo.connect(addr);
		} catch (java.net.UnknownHostException uh) {
			throw new MongoSQLException("bad url: " + uh);
		}
		//System.out.println("MongoDriver.connect() check info");
		String user = null;
		char[] password = null;
		if (null != info) {
			//System.out.println("MongoDriver.connect() info");
			if (info.containsKey("user")) {
				user = (String) info.get("user");
				//System.out.println("MongoDriver.connect() user = " + user);
				if (info.containsKey("password")) {
					password = ((String) info.get("password")).toCharArray();
					//System.out.println("MongoDriver.connect() passwd = "
					//		+ info.get("password"));
				}
			}
		}
		try {
			if (user == null) {
				db.requestEnsureConnection();
			} else {
				if (!db.authenticate(user, password)) {
					throw new MongoSQLException("Could not authenticate user '"
							+ user + "'.");
				}
			}
		} catch (MongoException e) {
			e.printStackTrace();
			if (null != e.getCause()) {
				throw new MongoSQLException(e.getCause());

			} else {
				throw new MongoSQLException(e.getMessage(), e);
			}
		}
		MongoConnection conn = new MongoConnection(db);
		conn.getConnectionInfo().setUrl(url);
		conn.getConnectionInfo().setUsername(user);
		if (null != password) {
			conn.getConnectionInfo().setPassword(new String(password));
		}
		return conn;
	}

	public int getMajorVersion() {
		return 0;
	}

	public int getMinorVersion() {
		return 1;
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
		// TODO do we need to do anything with info
		if (Executor.DEBUG) {
			for (Entry<Object, Object> property : info.entrySet()) {
				System.out.println("MongoDriver.getPropertyInfo() "
						+ property.getKey() + " = '" + property.getValue()
						+ "'");
			}
		}
		DriverPropertyInfo[] res = new DriverPropertyInfo[2];
		res[0] = new DriverPropertyInfo("user", null);
		res[1] = new DriverPropertyInfo("password", null);
		return res;
	}

	public boolean jdbcCompliant() {
		return false;
	}

	public static void install() {
		// NO-OP, handled in static
	}

	static {
		try {
			DriverManager.registerDriver(new MongoDriver());
		} catch (SQLException e) {
			throw new MongoRuntimeException(e);
		}
	}
}
