package com.example.crc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private JsonUtil() {
	}

	public static String toJson(Object value) {
		try {
			return OBJECT_MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException exception) {
			throw new IllegalArgumentException("Failed to serialize object to JSON", exception);
		}
	}

	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return OBJECT_MAPPER.readValue(json, type);
		} catch (JsonProcessingException exception) {
			throw new IllegalArgumentException("Failed to deserialize JSON", exception);
		}
	}
}
