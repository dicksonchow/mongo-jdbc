package com.mongodb.jdbc;

import java.util.ArrayList;
import java.util.List;

public class MongoTableMetaData {
	private final String tableCat;
	private final String tableSchem; 
	private final String tableName;
	
	private List<MongoColumnMetaData> columns;

	public MongoTableMetaData(String tableCat, String tableSchem, String tableName) {
		this.tableCat = tableCat;
		this.tableSchem = tableSchem;
		this.tableName = tableName;
		this.columns = new ArrayList<MongoColumnMetaData>();
	}

	private void addColumn(String columnName, int dataType, int ordinalPosition) {
		MongoColumnMetaData column = new MongoColumnMetaData(getTableCat(), tableSchem, tableName, columnName, dataType, ordinalPosition);
		columns.add(column);
	}
	
	public void addStringColumn(String columnName) {
		addColumn(columnName, java.sql.Types.CHAR, columns.size() + 1);
	}
	
	public void addIntColumn(String columnName) {
		addColumn(columnName, java.sql.Types.INTEGER, columns.size() + 1);
	}
	
	public void addObjectColumn(String columnName) {
		addColumn(columnName, java.sql.Types.JAVA_OBJECT, columns.size() + 1);
	}
	
	public List<MongoColumnMetaData> getColumns() {
		return columns;
	}

	public String getTableCat() {
		return tableCat;
	}

	public String getTableSchem() {
		return tableSchem;
	}

	public String getTableName() {
		return tableName;
	}

}
