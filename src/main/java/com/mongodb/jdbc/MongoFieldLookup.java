package com.mongodb.jdbc;

import java.util.HashMap;
import java.util.Map;

public class MongoFieldLookup {

	final Map<Integer, String> ids = new HashMap<Integer, String>();
	final Map<String, Integer> strings = new HashMap<String, Integer>();
	private boolean initialized = false;

	public MongoFieldLookup(Iterable<String> o) {
		init(o);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void init(Iterable<String> o) {
		if (initialized) {
			return;
		}
		if (o == null) {
			return;
		}
		initialized = true;
		for (String key : o) {
			get(key);
		}
	}

	public int get(String s) {
		Integer i = strings.get(s);
		if (i == null) {
			i = strings.size() + 1;
			store(i, s);
		}
		return i;
	}

	public String get(int i) {
		String s = ids.get(i);
		if (s != null) {
			return s;
		}
		throw new IllegalArgumentException(i + " is not a valid column id");
	}

	private void store(Integer i, String s) {
		ids.put(i, s);
		strings.put(s, i);
	}

}