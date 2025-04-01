package com.plantssoil.mq.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Serialize Object to JSON string and unserialize JSON string to Object<br/>
 * Use singleton pattern to have single instance of Serializer &
 * ObjectMapper<br/>
 * 
 * @author danialdy
 * @Date 24 Nov 2024 9:10:30 pm
 */
public class ObjectJsonSerializer {
	private ObjectMapper om;
	private static volatile ObjectJsonSerializer instance;

	private ObjectJsonSerializer() {
		om = new ObjectMapper();
	}

	/**
	 * Get the singleton instance of the Serializer
	 * 
	 * @return Serializer instance
	 */
	public static ObjectJsonSerializer getInstance() {
		if (instance == null) {
			synchronized (ObjectJsonSerializer.class) {
				if (instance == null) {
					instance = new ObjectJsonSerializer();
				}
			}
		}
		return instance;
	}

	/**
	 * serialize Object to JSON string
	 * 
	 * @param <T>    the object type need to serialize
	 * @param object the object to serialize
	 * @return serialized JSON String
	 * @throws Exception
	 */
	public <T> String serialize(T object) {
		try {
			return om.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * unserialize JSON string to Object
	 * 
	 * @param <T>        the object type need to unserialize
	 * @param serialized serialized JSON String
	 * @param clazz      the object class type
	 * @return object unserialized
	 */
	public <T> T unserialize(String serialized, Class<T> clazz) {
		try {
			return om.readValue(serialized, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
