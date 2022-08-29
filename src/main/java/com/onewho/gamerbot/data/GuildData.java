package com.onewho.gamerbot.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GuildData {
	
	private long id;
	private int maxSetsPerWeek;
	private int weeksBeforeAutoInactive;
	
	private List<UserData> users = new ArrayList<UserData>();
	private List<SetData> sets = new ArrayList<SetData>();
	
	private long leagueRoleId;
	private long leagueCategoryId;
	private long joinLeagueOptionId;
	private long setsaweekOptionId;
	private JsonObject channelIds;
	
	public GuildData(JsonObject data) {
		id = data.get("id").getAsLong();
		maxSetsPerWeek = data.get("max sets a week").getAsInt();
		weeksBeforeAutoInactive = data.get("weeks before auto inactive").getAsInt();
		users.clear();
		JsonArray us = data.get("users").getAsJsonArray();
		for (int i = 0; i < us.size(); ++i) users.add(new UserData(us.get(i).getAsJsonObject()));
		sets.clear();
		JsonArray ss = data.get("sets").getAsJsonArray();
		for (int i = 0; i < ss.size(); ++i) sets.add(new SetData(ss.get(i).getAsJsonObject()));
		this.leagueRoleId = data.get("league role id").getAsLong();
		this.leagueCategoryId = data.get("league category id").getAsLong();
		this.joinLeagueOptionId = data.get("join league option id").getAsLong();
		this.setsaweekOptionId = data.get("setsaweek option id").getAsLong();
		this.channelIds = data.get("channel ids").getAsJsonObject();
	}
	
	public GuildData(long id) {
		this.id = id;
		this.maxSetsPerWeek = 3;
		this.weeksBeforeAutoInactive = -1;
		this.leagueRoleId = -1;
		this.leagueCategoryId = -1;
		this.joinLeagueOptionId = -1;
		this.setsaweekOptionId = -1;
		this.channelIds = new JsonObject();
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("max sets a week", maxSetsPerWeek);
		data.addProperty("weeks before auto inactive", weeksBeforeAutoInactive);
		data.add("users", getUsersJson());
		data.add("sets", getSetsJson());
		data.addProperty("league role id", leagueRoleId);
		data.addProperty("league category id", leagueCategoryId);
		data.addProperty("join league option id", 0);
		data.addProperty("setsaweek option id", 0);
		data.add("channel ids", new JsonObject());
		return data;
	}
	
	private JsonArray getUsersJson() {
		JsonArray us = new JsonArray();
		for (UserData u : users) us.add(u.getJson());
		return us;
	}
	
	private JsonArray getSetsJson() {
		JsonArray ss = new JsonArray();
		for (SetData s : sets) ss.add(s.getJson());
		return ss;
	}
	
	public long getId() {
		return id;
	}
	
	public int getMaxSetsPerWeek() {
		return maxSetsPerWeek;
	}
	
	public void setMaxSetsPerWeek(int max) {
		maxSetsPerWeek = max;
	}
	
	public UserData getUserDataById(long id) {
		for (int i = 0; i < users.size(); ++i) if (users.get(i).getId() == id) return users.get(i);
		UserData data = new UserData(id);
		users.add(data);
		return data;
	}
	
	public SetData getSetDataById(int id) {
		for (int i = 0; i < sets.size(); ++i) if (sets.get(i).getId() == id) return sets.get(i);
		return null;
	}
	
	public int getWeeksBeforeAutoInactive() {
		return weeksBeforeAutoInactive;
	}
	
	public void setWeeksBeforeAutoInactive(int weeks) {
		if (weeks < -1) weeks = -1;
		weeksBeforeAutoInactive = weeks;
	}

	public long getLeagueRoleId() {
		return leagueRoleId;
	}

	public void setLeagueRoleId(long leagueRoleId) {
		this.leagueRoleId = leagueRoleId;
	}

	public long getLeagueCategoryId() {
		return leagueCategoryId;
	}

	public void setLeagueCategoryId(long leagueCategoryId) {
		this.leagueCategoryId = leagueCategoryId;
	}

	public long getJoinLeagueOptionId() {
		return joinLeagueOptionId;
	}

	public void setJoinLeagueOptionId(long joinLeagueOptionId) {
		this.joinLeagueOptionId = joinLeagueOptionId;
	}

	public long getSetsaweekOptionId() {
		return setsaweekOptionId;
	}

	public void setSetsaweekOptionId(long setsaweekOptionId) {
		this.setsaweekOptionId = setsaweekOptionId;
	}
	
	public long getChannelId(String name) {
		if (channelIds.get(name) == null) return -1;
		return channelIds.get(name).getAsLong();
	}
	
	public void setChannelId(String name, long id) {
		channelIds.addProperty(name, id);
	}
	
}
