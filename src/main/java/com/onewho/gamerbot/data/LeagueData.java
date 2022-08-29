package com.onewho.gamerbot.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class LeagueData {
	
	private static Gson gson = null;
	private static String dataFileName = "data.json";
	
	private static List<GuildData> guilds = new ArrayList<GuildData>();
	
	public static JsonObject readJsonData() throws IOException {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			gson = builder.create();
		}
		JsonObject json = new JsonObject();
		if (!Files.exists(Paths.get(dataFileName))) {
			json.add("guilds", new JsonArray());
			saveData();
		} else {
			Reader reader = Files.newBufferedReader(Paths.get(dataFileName));
			json = gson.fromJson(reader, JsonObject.class);
			reader.close();
		}
		guilds.clear();
		JsonArray gs = json.get("guilds").getAsJsonArray();
		for (int i = 0; i < gs.size(); ++i) guilds.add(new GuildData(gs.get(i).getAsJsonObject()));
		return json;
	}
	
	public static void saveData() {
		JsonObject json = new JsonObject();
		JsonArray gs = new JsonArray();
		for (int i = 0; i < guilds.size(); ++i) gs.add(guilds.get(i).getJson());
		json.add("guilds", gs);
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
	
	public static GuildData getGuildDataById(long id) {
		for (int i = 0; i < guilds.size(); ++i) if (guilds.get(i).getId() == id) return guilds.get(i);
		GuildData data = new GuildData(id);
		guilds.add(data);
		return data;
	}
	
}
