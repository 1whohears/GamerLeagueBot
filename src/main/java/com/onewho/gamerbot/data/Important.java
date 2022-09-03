package com.onewho.gamerbot.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Important {

	public static String getInsult() {
		return insults[(int)(Math.random()*insults.length)];
	}
	
	private static String[] insults = { "ERROR!" };
	
	public static void load() throws JsonSyntaxException, JsonIOException, IOException {
		JsonObject jsonO = GlobalData.getGson().fromJson(Files.newBufferedReader(Paths.get("token.json")), JsonObject.class);
		if (jsonO.get("insults") == null) return;
		JsonArray json = jsonO.get("insults").getAsJsonArray();
		insults = new String[json.size()];
		for (int i = 0; i < json.size(); ++i) insults[i] = json.get(i).getAsString();
	}
	
}
