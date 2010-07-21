package com.mongodb.jdbc;

import java.util.List;

public class MongoConnectionInfo {

	private String productName = "couchdb";

	private String url;
	private String database;
	private String username;
	private String password;

	private String version;
	private String majorVersion;
	private int majorVersionInt;
	private String minorVersion;
	private int minorVersionInt;
	private String buildVersion;
	private List<String> databases;

	public String getProductName() {
		return productName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
		String[] parts = version.split("\\.");
		if (parts.length > 0) {
			setMajorVersion(parts[0]);
		}
		if (parts.length > 1) {
			setMinorVersion(parts[1]);
		}
		if (parts.length > 2) {
			setBuildVersion(parts[2]);
		}
	}

	public String getMajorVersion() {
		return majorVersion;
	}

	public int getMajorVersionInt() {
		return majorVersionInt;
	}

	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
		try {
			this.majorVersionInt = Integer.parseInt(majorVersion);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	public int getMinorVersionInt() {
		return minorVersionInt;
	}

	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
		try {
			this.minorVersionInt = Integer.parseInt(minorVersion);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public List<String> getDatabases() {
		return databases;
	}

	public void setDatabases(List<String> databases) {
		this.databases = databases;
	}

}
