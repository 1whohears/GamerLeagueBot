package com.onewho.gamerbot.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilKClosest;

import net.dv8tion.jda.api.entities.TextChannel;

public class GuildData {
	
	private long id = -1;
	private int maxSetsPerWeek = 3;
	private int weeksBeforeAutoInactive = -1;
	private int weeksBeforeSetExpires = -1;
	private int weeksBeforeSetRepeat = -1;
	private int defaultScore = 1000;
	private double K = 20d;
	
	private List<UserData> users = new ArrayList<UserData>();
	private List<SetData> sets = new ArrayList<SetData>();
	
	private long leagueRoleId = -1;
	private long leagueCategoryId = -1;
	private long joinLeagueOptionId = -1;
	private long setsaweekOptionId = -1;
	private JsonObject channelIds = new JsonObject();
	
	public GuildData(JsonObject data) {
		id = ParseData.getLong(data, "id", id);
		maxSetsPerWeek = ParseData.getInt(data, "max sets a week", maxSetsPerWeek);
		weeksBeforeAutoInactive = ParseData.getInt(data, "weeks before auto inactive", weeksBeforeAutoInactive);
		weeksBeforeSetExpires = ParseData.getInt(data, "weeks before set expires", weeksBeforeSetExpires);
		weeksBeforeSetRepeat = ParseData.getInt(data, "weeks before set repeat", weeksBeforeSetRepeat);
		defaultScore = ParseData.getInt(data, "default score", defaultScore);
		K = ParseData.getDouble(data, "K", K);
		
		users.clear();
		JsonArray us = ParseData.getJsonArray(data, "users");
		for (int i = 0; i < us.size(); ++i) users.add(new UserData(us.get(i).getAsJsonObject()));
		sets.clear();
		JsonArray ss = ParseData.getJsonArray(data, "sets");
		for (int i = 0; i < ss.size(); ++i) sets.add(new SetData(ss.get(i).getAsJsonObject()));
		
		leagueRoleId = ParseData.getLong(data, "league role id", leagueRoleId);
		leagueCategoryId = ParseData.getLong(data, "league category id", leagueCategoryId);
		joinLeagueOptionId = ParseData.getLong(data, "join league option id", joinLeagueOptionId);
		setsaweekOptionId = ParseData.getLong(data, "setsaweek option id", setsaweekOptionId);
		channelIds = ParseData.getJsonObject(data, "channel ids");
	}
	
	public GuildData(long id) {
		this.id = id;
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("max sets a week", maxSetsPerWeek);
		data.addProperty("weeks before auto inactive", weeksBeforeAutoInactive);
		data.addProperty("weeks before set expires", weeksBeforeSetExpires);
		data.addProperty("weeks before set repeat", weeksBeforeSetRepeat);
		data.addProperty("default score", defaultScore);
		data.addProperty("K", K);
		data.add("users", getUsersJson());
		data.add("sets", getSetsJson());
		data.addProperty("league role id", leagueRoleId);
		data.addProperty("league category id", leagueCategoryId);
		data.addProperty("join league option id", joinLeagueOptionId);
		data.addProperty("setsaweek option id", setsaweekOptionId);
		data.add("channel ids", channelIds);
		return data;
	}
	
	public void readBackup(JsonObject backup) {
		users.clear();
		JsonArray us = ParseData.getJsonArray(backup, "users");
		for (int i = 0; i < us.size(); ++i) users.add(new UserData(us.get(i).getAsJsonObject()));
		sets.clear();
		JsonArray ss = ParseData.getJsonArray(backup, "sets");
		for (int i = 0; i < ss.size(); ++i) sets.add(new SetData(ss.get(i).getAsJsonObject()));
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
		data.setScore(defaultScore);
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
	
	public int getDefaultScore() {
		return defaultScore;
	}

	public void setDefaultScore(int defaultScore) {
		this.defaultScore = defaultScore;
	}
	
	public int getWeeksBeforeSetRepeat() {
		return weeksBeforeSetRepeat;
	}

	public void setWeeksBeforeSetRepeat(int weeksBeforeSetRepeat) {
		this.weeksBeforeSetRepeat = weeksBeforeSetRepeat;
	}
	
	public int getWeeksBeforeSetExpires() {
		return weeksBeforeSetExpires;
	}
	
	public void setWeeksBeforeSetExpires(int weeks) {
		this.weeksBeforeSetExpires = weeks;
	}
	
	public double getK() {
		return K;
	}

	public void setK(double k) {
		K = k;
	}
	
	public List<UserData> getAllUsers() {
		return users;
	}
	
	public void removeOldSets() {
		for (int i = 0; i < sets.size(); ++i) {
			if (sets.get(i).isComplete()) continue;
			int weekDiff = UtilCalendar.getWeekDiff(
					UtilCalendar.getDate(sets.get(i).getCreatedDate()), 
					UtilCalendar.getCurrentDate()); 
			System.out.println("SET "+sets.get(i)+" weekDiff = "+weekDiff+" "+weeksBeforeSetExpires);
			if (weeksBeforeSetExpires == -1 || weekDiff <= weeksBeforeSetExpires) continue;
			System.out.println("removed");
			sets.remove(i--);
		}
	}
	
	public List<UserData> getAvailableSortedUsers() {
		List<UserData> available = new ArrayList<UserData>();
		System.out.println("getting available users");
		for (int i = 0; i < users.size(); ++i) {
			System.out.println(users.get(i));
			if (!users.get(i).getActive()) continue;
			if (users.get(i).getSetsPerWeek() < 1) continue;
			int weekDiff = UtilCalendar.getWeekDiff(
					UtilCalendar.getDate(users.get(i).getLastActive()), 
					UtilCalendar.getCurrentDate());
			System.out.println("week diff = "+weekDiff);
			if (weeksBeforeAutoInactive != -1 && weekDiff > weeksBeforeAutoInactive) {
				users.get(i).setActive(false);
				continue;
			}
			System.out.println("added");
			available.add(users.get(i));
		}
		sortByScoreDescend(available);
		return available;
	}
	
	public List<SetData> getIncompleteOrCurrentSetsByPlayer(long id) {
		List<SetData> userSets = new ArrayList<SetData>();
		for (SetData set : sets) if (set.hasPlayer(id) 
					&& (UtilCalendar.getWeekDiff(
						UtilCalendar.getDate(set.getCreatedDate()), 
						UtilCalendar.getCurrentDate()) == 0
					|| (!set.isComplete() && !set.isUnconfirmed()))) 
				userSets.add(set);
		return userSets;
	}
	
	public List<SetData> getSetsBetweenUsers(long id1, long id2) {
		List<SetData> userSets = new ArrayList<SetData>();
		for (SetData set : sets) if (set.hasPlayer(id1) && set.hasPlayer(id2)) userSets.add(set);
		return userSets;
	}
	
	public SetData getNewestSetBetweenUsers(long id1, long id2) {
		List<SetData> userSets = getSetsBetweenUsers(id1, id2);
		if (userSets.size() == 0) return null;
		int newestIndex = 0;
		for (int i = 1; i < userSets.size(); ++i) {
			String d1 = userSets.get(i).getCreatedDate();
			String d2 = userSets.get(newestIndex).getCreatedDate();
			if (UtilCalendar.isNewer(d1, d2)) newestIndex = i;
		}
		return userSets.get(newestIndex);
	}
	
	public SetData createSet(long id1, long id2) {
		if (id1 == id2) return null;
		SetData set = new SetData(getNewSetId(), id1, id2, UtilCalendar.getCurrentDateString());
		sets.add(set);
		return set;
	}
	
	private int getNewSetId() {
		int maxId = -1;
		for (SetData set : sets) if (set.getId() > maxId) maxId = set.getId();
		return maxId+1;
	}
	
	public static void sortByScoreDescend(List<UserData> ud) {
		for (int i = 0; i < ud.size(); ++i) {
			int maxIndex = i;
			for (int j = i+1; j < ud.size(); ++j) 
				if (ud.get(j).getScore() > ud.get(maxIndex).getScore()) maxIndex = j;
			UserData temp = ud.get(maxIndex);
			ud.set(maxIndex, ud.get(i));
			ud.set(i, temp);
		}
	}
	
	public static int[] getClosestUserIndexsByScore(UserData user, List<UserData> sortedUsers) {
		int[] scores = new int[sortedUsers.size()];
		for (int i = 0; i < scores.length; ++i) scores[i] = sortedUsers.get(i).getScore();
		return UtilKClosest.getKclosestIndex(scores, user.getScore(), sortedUsers.size(), sortedUsers.size());
	}
	
	public List<SetData> getSetsAtWeekOfDate(String date) {
		List<SetData> saw = new ArrayList<SetData>();
		for (SetData set : sets) if (UtilCalendar.getWeekDiff(date, set.getCreatedDate()) == 0) saw.add(set);
		return saw;
	}
	
	public void displaySetsByDate(String date, TextChannel channel) {
		for (SetData set : sets) if (UtilCalendar.getWeekDiff(date, set.getCreatedDate()) == 0) set.displaySet(channel);
	}
	
	public int processSets() {
		int num = 0;
		for (SetData set : sets) if (set.isComplete() && !set.isProcessed()) {
			set.processSet(this);
			++num;
		}
		return num;
	}
	
}
