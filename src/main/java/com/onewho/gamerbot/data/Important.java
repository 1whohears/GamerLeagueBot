package com.onewho.gamerbot.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Important {

	public static String getError() {
		return errors[(int)(Math.random()*errors.length)];
	}
	
	public static String getError(String msg) {
		return getError()+" "+msg;
	}
	
	private static String[] errors = { "ERROR!" };
	
	public static void load() throws JsonSyntaxException, JsonIOException, IOException {
		JsonObject jsonO = GlobalData.getGson().fromJson(Files.newBufferedReader(Paths.get("important.json")), JsonObject.class);
		if (jsonO.get("insults") == null) return;
		JsonArray json = jsonO.get("insults").getAsJsonArray();
		errors = new String[json.size()];
		for (int i = 0; i < json.size(); ++i) errors[i] = json.get(i).getAsString();
	}
	
}
