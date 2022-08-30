package com.onewho.gamerbot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ParseData {
	
	public static long getLong(JsonObject data, String key, long defaultValue) {
		if (data.get(key) == null) return defaultValue;
		return data.get(key).getAsLong();
	}
	
	public static int getInt(JsonObject data, String key, int defaultValue) {
		if (data.get(key) == null) return defaultValue;
		return data.get(key).getAsInt();
	}
	
	public static boolean getBoolean(JsonObject data, String key, boolean defaultValue) {
		if (data.get(key) == null) return defaultValue;
		return data.get(key).getAsBoolean();
	}
	
	public static String getString(JsonObject data, String key, String defaultValue) {
		if (data.get(key) == null) return defaultValue;
		return data.get(key).getAsString();
	}
	
	public static JsonObject getJsonObject(JsonObject data, String key) {
		if (data.get(key) == null) return new JsonObject();
		return data.get(key).getAsJsonObject();
	}
	
	public static JsonArray getJsonArray(JsonObject data, String key) {
		if (data.get(key) == null) return new JsonArray();
		return data.get(key).getAsJsonArray();
	}
}
