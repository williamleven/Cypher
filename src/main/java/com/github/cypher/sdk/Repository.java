package com.github.cypher.sdk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Repository<T> {
	private final Map<String, T> storage = new ConcurrentHashMap<>();
	private final Factory<? extends T> factory;

	interface Factory<K>{
		K get(String id);
	}

	Repository(Factory<? extends T> factory){
		this.factory = factory;
	}

	/**
	 * This method returns an existing object if possible,
	 * otherwise it creates and stores a new one.
	 * @param id The unique ID of the object
	 * @return A object
	 */
	public T get(String id) {
		if(storage.containsKey(id)) {
			return storage.get(id);
		}
		T object = factory.get(id);
		storage.put(id, object);
		return object;
	}
}
