package com.mongodb.jdbc;

@SuppressWarnings("serial")
public class MongoUnsupportedOperationException extends UnsupportedOperationException {
	public MongoUnsupportedOperationException() {
		super();
		System.out.println("MongoUnsupportedOperationException.MongoUnsupportedOperationException()");
		this.printStackTrace();
		//System.exit(0);
	}
	public MongoUnsupportedOperationException(String message) {
		super(message);
		System.out.println("MongoUnsupportedOperationException.MongoUnsupportedOperationException() " + message);
		this.printStackTrace();
		//System.exit(0);
	}
}
