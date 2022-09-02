package com.onewho.gamerbot.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.BotMain;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class GuildData {
	
	private long id = -1;
	
	List<LeagueData> leagues = new ArrayList<LeagueData>();
	
	/**
	 * construct this GuildData instance using data from disk
	 * @param data
	 */
	protected GuildData(JsonObject data) {
		id = ParseData.getLong(data, "id", id);
		leagues.clear();
		JsonArray ls = ParseData.getJsonArray(data, "leagues");
		for (int i = 0; i < ls.size(); ++i) leagues.add(new LeagueData(ls.get(i).getAsJsonObject()));
	}
	
	/**
	 * construct data for a new guild
	 * @param id guild id
	 */
	protected GuildData(long id) {
		this.id = id;
	}
	
	/**
	 * @return this guild's league data to be written to disk
	 */
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
	
	/**
	 * @return this guild's id
	 */
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
	
	/**
	 * create a new league for this guild
	 * @param guild
	 * @param debugChannel channel debug messages to be sent to
	 * @param name name of the new league
	 * @return new league data object
	 */
	public LeagueData createLeague(Guild guild, MessageChannelUnion debugChannel, String name) {
		LeagueData league = new LeagueData(name);
		leagues.add(league);
		league.setupDiscordStuff(guild, debugChannel);
		return league;
	}
	
	/**
	 * not working yet
	 */
	public void archiveLeague() {
		// TODO archive league command
	}
	
	/**
	 * setup the channels for all the leagues in this guild
	 * @param guild
	 * @param debugChannel
	 */
	public void setupLeagues(Guild guild, MessageChannelUnion debugChannel) {
		for (LeagueData l : leagues) l.setupDiscordStuff(guild, debugChannel);
	}
	
	protected void genScheduledPairsForAllLeagues() {
		for (LeagueData l : leagues) l.genScheduledPairs(BotMain.jda.getGuildById(id));
	}
	
	protected void updateRanksForAllLeagues() {
		for (LeagueData l : leagues) l.updateRanks(BotMain.jda.getGuildById(id));
	}
	
}
