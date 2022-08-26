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
		guilds.add(guild);
		saveData();
		return guild;
	}
	
}
