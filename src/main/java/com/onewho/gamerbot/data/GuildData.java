package com.onewho.gamerbot.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildData {
	
	private long id = -1;
	
	List<LeagueData> leagues = new ArrayList<LeagueData>();
	
	public GuildData(JsonObject data) {
		id = ParseData.getLong(data, "id", id);
		leagues.clear();
		JsonArray ls = ParseData.getJsonArray(data, "leagues");
		for (int i = 0; i < ls.size(); ++i) leagues.add(new LeagueData(ls.get(i).getAsJsonObject()));
	}
	
	public GuildData(long id) {
		this.id = id;
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.add("leagues", getLeaguesJson());
		return data;
	}
	
	private JsonArray getLeaguesJson() {
		JsonArray json = new JsonArray();
		for (LeagueData l : leagues) json.add(l.getJson());
		return json;
	}
	
	public long getId() {
		return id;
	}
	
	/**
	 * Meant to get the league data based on which bot-command channel you are typing in
	 */
	public LeagueData getLeagueByChannel(Channel channel) {
		for (LeagueData l : leagues) if (l.hasChannel(channel)) return l;
		return null;
	}
	
	public LeagueData createLeague(Guild guild, TextChannel debugChannel, String name) {
		LeagueData league = new LeagueData(name);
		leagues.add(league);
		league.setupDiscordStuff(guild, debugChannel);
		return league;
	}
	
	public void archiveLeague() {
		
	}
	
}
