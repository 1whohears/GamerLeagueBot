package com.onewho.gamerbot.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilKClosest;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class LeagueData {
	
	private String name = "Gamer League";
	
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
	
	public LeagueData(JsonObject data) {
		name = ParseData.getString(data, name, name);
		
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
	
	public LeagueData(String name) {
		this.name = name;
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
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
	
	public void readBackup(JsonObject backup) throws IllegalStateException, ClassCastException {
		JsonArray us = ParseData.getJsonArray(backup, "users");
		for (int i = 0; i < us.size(); ++i) {
			long id = us.get(i).getAsJsonObject().get("id").getAsLong();
			UserData user = getUserDataById(id);
			if (user == null) {
				user = new UserData(id);
				users.add(user);
			}
			user.readBackup(us.get(i).getAsJsonObject());
		}
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
	
	public JsonObject getBackupJson() {
		JsonObject backup = new JsonObject();
		backup.add("users", getUsersBackupJson());
		backup.add("sets", getSetsJson());
		return backup;
	}
	
	private JsonArray getUsersBackupJson() {
		JsonArray us = new JsonArray();
		for (UserData u : users) us.add(u.getBackupJson());
		return us;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	public boolean hasChannel(Channel channel) {
		if (channelIds.get(channel.getName()) == null) return false;
		return channelIds.get(name).getAsLong() == channel.getIdLong();
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
	
	public void removeOldSets(TextChannel pairsChannel) {
		for (int i = 0; i < sets.size(); ++i) {
			if (sets.get(i).isComplete()) continue;
			int weekDiff = UtilCalendar.getWeekDiff(
					UtilCalendar.getDate(sets.get(i).getCreatedDate()), 
					UtilCalendar.getCurrentDate()); 
			System.out.println("SET "+sets.get(i)+" weekDiff = "+weekDiff+" "+weeksBeforeSetExpires);
			if (weeksBeforeSetExpires == -1 || weekDiff <= weeksBeforeSetExpires) continue;
			removeSet(sets.get(i).getId(), pairsChannel);
			System.out.println("removed");
		}
	}
	
	public void removeSet(int id, TextChannel pairsChannel) {
		SetData set = this.getSetDataById(id);
		set.removeSetDisplay(pairsChannel);
		sets.remove(set);
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
	
	public void setupDiscordStuff(Guild guild, MessageChannelUnion debugChannel) {
		//setup roles
		Role gamerRole = null;
		if (getLeagueRoleId() == -1) {
			gamerRole = guild.createRole().complete();
			setLeagueRoleId(gamerRole.getIdLong());
		} else {
			gamerRole = guild.getRoleById(getLeagueRoleId());
			if (gamerRole == null) {
				gamerRole = guild.createRole().complete();
				setLeagueRoleId(gamerRole.getIdLong());
			}
		}
		gamerRole.getManager()
			.setName("GAMERS")
			.setColor(getRandomColor())
			.queue();
		//setup category
		Category gamerCat = null;
		if (getLeagueCategoryId() == -1) {
			gamerCat = guild.createCategory(name).complete();
			setLeagueCategoryId(gamerCat.getIdLong());
		} else {
			gamerCat = guild.getCategoryById(getLeagueCategoryId());
			if (gamerCat == null) {
				gamerCat = guild.createCategory(name).complete();
				setLeagueCategoryId(gamerCat.getIdLong());
			}
		}
		Collection<Permission> perm1 = new ArrayList<Permission>();
		Collection<Permission> perm2 = new ArrayList<Permission>();
		perm1.add(Permission.MESSAGE_HISTORY);
		perm2.add(Permission.MESSAGE_SEND);
		perm2.add(Permission.MESSAGE_ADD_REACTION);
		gamerCat.getManager()
			.putRolePermissionOverride(guild.getBotRole().getIdLong(), perm1, null)
			.putRolePermissionOverride(guild.getBotRole().getIdLong(), perm2, null)
			.putRolePermissionOverride(guild.getPublicRole().getIdLong(), perm1, perm2)
			.complete();
		perm1.clear();
		perm2.clear();
		//setup channels
		TextChannel commandsChannel = setupChannel("bot-commands", gamerCat, guild);
		TextChannel optionsChannel = setupChannel("options", gamerCat, guild);
		setupChannel("set-history", gamerCat, guild);
		setupChannel("ranks", gamerCat, guild);
		setupChannel("pairings", gamerCat, guild);
		perm1.add(Permission.MESSAGE_SEND);
		perm1.add(Permission.MESSAGE_ADD_REACTION);
		commandsChannel.getManager()
			.putRolePermissionOverride(gamerRole.getIdLong(), perm1, null)
			.complete();
		perm2.add(Permission.MESSAGE_ADD_REACTION);
		optionsChannel.getManager()
			.putRolePermissionOverride(gamerRole.getIdLong(), perm2, null)
			.complete();
		perm1.clear();
		perm2.clear();
		//setup options channel
		setupOptions(optionsChannel);
		//finish
		GlobalData.saveData();
		System.out.println("setup command complete");
		debugChannel.sendMessage("Bot Channel Setup Complete!").queue();		
	}
	
	private Color getRandomColor() {
		Random random = new Random();
		float hue = random.nextFloat();
		float saturation = 0.9f;
		float luminance = 1.0f;
		return Color.getHSBColor(hue, saturation, luminance);
	}
	
	private TextChannel setupChannel(String name, Category cat, Guild guild) {
		TextChannel channel = null;
		if (getChannelId(name) == -1) {
			channel = cat.createTextChannel(name).complete();
			setChannelId(name, channel.getIdLong());
		} else {
			channel = guild.getTextChannelById(getChannelId(name));
			if (channel == null) {
				channel = cat.createTextChannel(name).complete();
				setChannelId(name, channel.getIdLong());
			}
		}
		channel.getManager().sync(cat.getPermissionContainer()).complete();
		return channel;
	}
	
	private void setupOptions(TextChannel channel) {
		if (getJoinLeagueOptionId() == -1) {
			MessageCreateData jlc = new MessageCreateBuilder()
					.addEmbeds(getJLEmbed())
					.addActionRow(getJLButtons())
					.build();
			Message jlb = channel.sendMessage(jlc).complete();
			setJoinLeagueOptionId(jlb.getIdLong());
		} else {
			MessageEditData jle = new MessageEditBuilder()
					.setEmbeds(getJLEmbed())
					.setActionRow(getJLButtons())
					.build();
			channel.editMessageById(getJoinLeagueOptionId(), jle).complete();
		}
		int max = getMaxSetsPerWeek();
		if (getSetsaweekOptionId() == -1) {
			MessageCreateData swc = new MessageCreateBuilder()
					.addEmbeds(getSWEmbed())
					.addActionRow(getSWButtons(max))
					.build();
			Message swb = channel.sendMessage(swc).complete();
			setSetsaweekOptionId(swb.getIdLong());
		} else {
			MessageEditData swe = new MessageEditBuilder()
					.setEmbeds(getSWEmbed())
					.setActionRow(getSWButtons(max))
					.build();
			channel.editMessageById(getSetsaweekOptionId(), swe).complete();
		}
	}
	
	private MessageEmbed getJLEmbed() {
		EmbedBuilder jleb = new EmbedBuilder();
		jleb.setTitle("Join this Server's Gamer League?");
		jleb.setColor(Color.GREEN);
		jleb.setDescription("You will be pinged often and must complete your assigned matches!");
		return jleb.build();
	}
	
	private List<Button> getJLButtons() {
		Button join = Button.success("join-gamer-league", "Join");
		Button quit = Button.danger("quit-gamer-league", "Quit");
		return Arrays.asList(join, quit);
	}
	
	private MessageEmbed getSWEmbed() {
		EmbedBuilder sweb = new EmbedBuilder();
		sweb.setTitle("Sets Per Week");
		sweb.setColor(Color.BLUE);
		sweb.setDescription("Most amount of sets you can do next week?");
		return sweb.build();
	}
	
	private List<Button> getSWButtons(int max) {
		List<Button> bs = new ArrayList<Button>();
		for (int i = 0; i <= max; ++i) bs.add(Button.primary("setsaweek-"+i, i+""));
		return bs;
	}
	
}
