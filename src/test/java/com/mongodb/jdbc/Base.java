package com.mongodb.jdbc;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.MongoClient;

public abstract class Base {

    private static final String PREFIX = "mongodb://";

    final DB _db;

    public Base() {
        String url = "mongodb://127.0.0.1:27017/test";
        url = url.substring(PREFIX.length());
        try {
            DBAddress addr = new DBAddress(url);
            _db = new MongoClient(addr).getDB(addr.getDBName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

