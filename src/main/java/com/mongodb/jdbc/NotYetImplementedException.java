package com.mongodb.jdbc;

@SuppressWarnings("serial")
public class NotYetImplementedException extends IllegalStateException {
	public NotYetImplementedException(String message) {
		super(message);
		System.out
				.println("UnsupportedOperationException.UnsupportedOperationException()");
	}
}
