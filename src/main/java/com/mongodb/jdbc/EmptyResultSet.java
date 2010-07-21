package com.mongodb.jdbc;

import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;

public class EmptyResultSet extends MongoResultSet {

	public EmptyResultSet(DB db) {
		// TODO remove this method
		super(db, null, new MongoResultSetMetaData(db, new BasicDBObject(), new HashMap<Integer, String>(), null));
	}

	public EmptyResultSet(DB db, MongoResultSetMetaData metaData) {
		super(db, null, metaData);
	}

}
