package com.onewho.gamerbot.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LeagueData {
	
	private static Gson gson = null;
	private static JsonObject json = new JsonObject();
	private static String dataFileName = "data.json";
	
	public static JsonObject readJsonData() throws IOException {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			gson = builder.create();
		}
		if (!Files.exists(Paths.get(dataFileName))) {
			json.add("guilds", new JsonArray());
			saveData();
		} else {
			Reader reader = Files.newBufferedReader(Paths.get(dataFileName));
			json = gson.fromJson(reader, JsonObject.class);
			reader.close();
		}
		return json;
	}
	
	public static void saveData() {
		String data = gson.toJson(json);
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(dataFileName));
			writer.write(data);
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JsonObject getGuildDataById(long id) {
		JsonArray guilds = json.get("guilds").getAsJsonArray();
		for (int i = 0; i < guilds.size(); ++i) 
			if (guilds.get(i).getAsJsonObject().get("id").getAsLong() == id) 
				return guilds.get(i).getAsJsonObject();
		JsonObject guild = new JsonObject();
		guild.addProperty("id", id);
		guild.add("users", new JsonArray());
		guild.add("sets", new JsonArray());
		guilds.add(guild);
		saveData();
		return guild;
	}
	
	public static JsonObject getUserDataById(long id, JsonObject guildData) {
		if (guildData == null) return null;
		JsonArray users = guildData.get("users").getAsJsonArray();
		for (int i = 0; i < users.size(); ++i) 
			if (users.get(i).getAsJsonObject().get("id").getAsLong() == id) 
				return users.get(i).getAsJsonObject();
		JsonObject user = new JsonObject();
		user.addProperty("id", id);
		user.addProperty("active", false);
		user.addProperty("sets per week", 0);
		users.add(user);
		saveData();
		return user;
	}
	
	public static JsonObject getSetDataById(int id, JsonObject guildData) {
		if (guildData == null) return null;
		JsonArray sets = guildData.get("sets").getAsJsonArray();
		for (int i = 0; i < sets.size(); ++i)
			if (sets.get(i).getAsJsonObject().get("id").getAsInt() == id)
				return sets.get(i).getAsJsonObject();
		return null;
	}
	
}
