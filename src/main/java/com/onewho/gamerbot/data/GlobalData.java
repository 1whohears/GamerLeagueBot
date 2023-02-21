package com.onewho.gamerbot.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GlobalData {
	
	private static Gson gson = null;
	private static String dataFileName = "data.json";
	
	private static HashMap<Long, GuildData> guilds = new HashMap<>();
	
	public static Gson getGson() {
		if (gson == null) {
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			gson = builder.create();
		}
		return gson;
	}
	
	/**
	 * Read all data saved on disk
	 * @return JsonObject with the data for all servers/guilds using this bot
	 * @throws IOException
	 */
	public static JsonObject readJsonData() throws IOException {
		getGson();
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
		for (int i = 0; i < gs.size(); ++i) {
			GuildData gd = new GuildData(gs.get(i).getAsJsonObject());
			guilds.put(gd.getId(), gd);
		}
		return json;
	}
	
	/**
	 * writes data for all discord servers/guilds to disk
	 */
	public static void saveData() {
		JsonObject json = new JsonObject();
		JsonArray gs = new JsonArray();
		guilds.forEach((id, data) -> saveGuild(data, gs));
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
	
	private static void saveGuild(GuildData data, JsonArray gs) {
		gs.add(data.getJson());
	}
	
	/**
	 * @param id guild id
	 * @return the league data for this guild
	 */
	@Nullable
	public static GuildData getGuildDataById(long id) {
		return guilds.get(id);
	}
	
	/**
	 * @param id guild id
	 * @return the league data for this guild
	 */
	public static GuildData createGuildData(long id) {
		GuildData data = getGuildDataById(id);
		if (data != null) return data;
		data = new GuildData(id);
		guilds.put(id, data);
		return data;
	}
	
	public static void genScheduledPairsForAllLeagues() {
		guilds.forEach((id, data) -> data.genScheduledPairsForAllLeagues());
	}
	
	public static void updateRanksForAllLeagues() {
		guilds.forEach((id, data) -> data.updateRanksForAllLeagues());
	}
	
}
