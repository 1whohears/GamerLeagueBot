package com.onewho.gamerbot.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.command.Backup;
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
	
	public boolean autoGenPairs = false;
	public boolean autoUpdateRanks = false;
	
	private List<UserData> users = new ArrayList<UserData>();
	private List<SetData> sets = new ArrayList<SetData>();
	
	private long leagueRoleId = -1;
	private long leagueCategoryId = -1;
	private long joinLeagueOptionId = -1;
	private long setsaweekOptionId = -1;
	private JsonObject channelIds = new JsonObject();
	
	/**
	 * @param data league data written from disk
	 */
	protected LeagueData(JsonObject data) {
		name = ParseData.getString(data, "name", name);
		
		maxSetsPerWeek = ParseData.getInt(data, "max sets a week", maxSetsPerWeek);
		weeksBeforeAutoInactive = ParseData.getInt(data, "weeks before auto inactive", weeksBeforeAutoInactive);
		weeksBeforeSetExpires = ParseData.getInt(data, "weeks before set expires", weeksBeforeSetExpires);
		weeksBeforeSetRepeat = ParseData.getInt(data, "weeks before set repeat", weeksBeforeSetRepeat);
		defaultScore = ParseData.getInt(data, "default score", defaultScore);
		K = ParseData.getDouble(data, "K", K);
		
		autoGenPairs = ParseData.getBoolean(data, "auto gen pairs", autoGenPairs);
		autoUpdateRanks = ParseData.getBoolean(data, "auto update ranks", autoUpdateRanks);
		
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
	
	/**
	 * construct a new league data object
	 * @param name name of this new league
	 */
	protected LeagueData(String name) {
		this.name = name;
	}
	
	/**
	 * @return league data to be written to disk
	 */
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("name", name);
		data.addProperty("max sets a week", maxSetsPerWeek);
		data.addProperty("weeks before auto inactive", weeksBeforeAutoInactive);
		data.addProperty("weeks before set expires", weeksBeforeSetExpires);
		data.addProperty("weeks before set repeat", weeksBeforeSetRepeat);
		data.addProperty("default score", defaultScore);
		data.addProperty("K", K);
		data.addProperty("auto gen pairs", autoGenPairs);
		data.addProperty("auto update ranks", autoUpdateRanks);
		data.add("users", getUsersJson());
		data.add("sets", getSetsJson());
		data.addProperty("league role id", leagueRoleId);
		data.addProperty("league category id", leagueCategoryId);
		data.addProperty("join league option id", joinLeagueOptionId);
		data.addProperty("setsaweek option id", setsaweekOptionId);
		data.add("channel ids", channelIds);
		return data;
	}
	
	/**
	 * change user and set data to the contents of this backup data
	 * @param backup
	 * @throws IllegalStateException
	 * @throws ClassCastException
	 */
	public void readBackup(JsonObject backup) throws IllegalStateException, ClassCastException {
		JsonArray us = ParseData.getJsonArray(backup, "users");
		for (int i = 0; i < us.size(); ++i) {
			long id = us.get(i).getAsJsonObject().get("id").getAsLong();
			UserData user = getUserDataById(id);
			if (user == null) createUser(id);
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
	
	/**
	 * @return data needed to recover the current set and user data
	 */
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
	
	/**
	 * @return the name of this league
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name change the name of this league
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the max number of sets a user will be automatically assigned every week
	 */
	public int getMaxSetsPerWeek() {
		return maxSetsPerWeek;
	}
	
	/**
	 * @param max change the max number of sets a user can be assigned every week
	 */
	public void setMaxSetsPerWeek(int max) {
		if (max < 0) max = 0;
		maxSetsPerWeek = max;
	}
	
	/**
	 * @param id User Id
	 * @return a user's league data or null if that user isn't in this league
	 */
	public UserData getUserDataById(long id) {
		for (int i = 0; i < users.size(); ++i) if (users.get(i).getId() == id) return users.get(i);
		return null;
	}
	
	/**
	 * add a user to this league by their id
	 * @param id
	 * @return the new user data
	 */
	public UserData createUser(long id) {
		UserData data = new UserData(id);
		data.setScore(defaultScore);
		users.add(data);
		return data;
	}
	
	/**
	 * @param id
	 * @return get set data by id or null if that set doesn't exist
	 */
	@Nullable
	public SetData getSetDataById(int id) {
		for (int i = 0; i < sets.size(); ++i) if (sets.get(i).getId() == id) return sets.get(i);
		return null;
	}
	
	/**
	 * @return weeks of not completing sets before a user is automatically set to inactive
	 */
	public int getWeeksBeforeAutoInactive() {
		return weeksBeforeAutoInactive;
	}
	
	/**
	 * weeks of not completing sets before a user is automatically set to inactive
	 * @param weeks set to -1 to disable this feature
	 */
	public void setWeeksBeforeAutoInactive(int weeks) {
		if (weeks < -1) weeks = -1;
		weeksBeforeAutoInactive = weeks;
	}
	
	/**
	 * @return the id of the role given to players in this league
	 */
	public long getLeagueRoleId() {
		return leagueRoleId;
	}
	
	/**
	 * @param leagueRoleId the id of the role given to players in this league
	 */
	public void setLeagueRoleId(long leagueRoleId) {
		this.leagueRoleId = leagueRoleId;
	}
	
	/**
	 * @return the id of the category this leagues channels are under
	 */
	public long getLeagueCategoryId() {
		return leagueCategoryId;
	}
	
	/**
	 * @param leagueCategoryId the id of the category this leagues channels are under
	 */
	public void setLeagueCategoryId(long leagueCategoryId) {
		this.leagueCategoryId = leagueCategoryId;
	}
	
	/**
	 * @return the id of the message with the join/quit league buttons
	 */
	public long getJoinLeagueOptionId() {
		return joinLeagueOptionId;
	}
	
	/**
	 * @param joinLeagueOptionId the id of the message with the join/quit league buttons
	 */
	public void setJoinLeagueOptionId(long joinLeagueOptionId) {
		this.joinLeagueOptionId = joinLeagueOptionId;
	}
	
	/**
	 * @return the id of the message with the sets a week option buttons 
	 */
	public long getSetsaweekOptionId() {
		return setsaweekOptionId;
	}
	
	/**
	 * @param setsaweekOptionId the id of the message with the sets a week option buttons 
	 */
	public void setSetsaweekOptionId(long setsaweekOptionId) {
		this.setsaweekOptionId = setsaweekOptionId;
	}
	
	/**
	 * @param name
	 * @return the saved id of a league channel by name. -1 if a channel with that name isn't saved
	 */
	public long getChannelId(String name) {
		if (channelIds.get(name) == null) return -1;
		return channelIds.get(name).getAsLong();
	}
	
	/**
	 * @param name the name of the league channel
	 * @param id the new id of a channel you want to be used by the league
	 */
	public void setChannelId(String name, long id) {
		channelIds.addProperty(name, id);
	}
	
	/**
	 * @param channel
	 * @return if this league uses this channel
	 */
	public boolean hasChannel(Channel channel) {
		if (channelIds.get(channel.getName()) == null) return false;
		return channelIds.get(channel.getName()).getAsLong() == channel.getIdLong();
	}
	
	/**
	 * @return the score new users are given
	 */
	public int getDefaultScore() {
		return defaultScore;
	}
	
	/**
	 * @param defaultScore the score new users are given
	 */
	public void setDefaultScore(int defaultScore) {
		this.defaultScore = defaultScore;
	}
	
	/**
	 * @return weeks before 2 players are allowed to be automatically paired together
	 */
	public int getWeeksBeforeSetRepeat() {
		return weeksBeforeSetRepeat;
	}
	
	/**
	 * set weeks before 2 players are allowed to be automatically paired together
	 * @param weeksBeforeSetRepeat -1 to disable this feature
	 */
	public void setWeeksBeforeSetRepeat(int weeksBeforeSetRepeat) {
		if (weeksBeforeSetRepeat < -1) weeksBeforeSetRepeat = -1;
		this.weeksBeforeSetRepeat = weeksBeforeSetRepeat;
	}
	
	/**
	 * @return weeks before an incomplete set is removed
	 */
	public int getWeeksBeforeSetExpires() {
		return weeksBeforeSetExpires;
	}
	
	/**
	 * set weeks before an incomplete set is removed
	 * @param weeks -1 to disable this feature
	 */
	public void setWeeksBeforeSetExpires(int weeks) {
		if (weeks < -1) weeks = -1;
		this.weeksBeforeSetExpires = weeks;
	}
	
	/**
	 * @return the elo K constant
	 */
	public double getK() {
		return K;
	}
	
	/**
	 * @param k new elo k constant
	 */
	public void setK(double k) {
		K = k;
	}
	
	/**
	 * @return list of all users league data 
	 */
	public List<UserData> getAllUsers() {
		return users;
	}
	
	/**
	 * remove incomplete sets based on weeks before auto expire
	 * @param pairsChannel the pairs channel of this league
	 */
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
	
	/**
	 * remove this set
	 * @param id the set id
	 * @param pairsChannel the pairs channel of this league
	 */
	public void removeSet(int id, TextChannel pairsChannel) {
		SetData set = this.getSetDataById(id);
		if (set == null) return;
		set.removeSetDisplay(pairsChannel);
		sets.remove(set);
	}
	
	/**
	 * @return a list of active/available user data
	 */
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
	
	/**
	 * @param id league user id
	 * @return list of this users sets that were assigned this week or hasn't completed yet 
	 */
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
	
	/**
	 * @param id1 user 1 id
	 * @param id2 user 2 id
	 * @return list of sets that these users played each other
	 */
	public List<SetData> getSetsBetweenUsers(long id1, long id2) {
		List<SetData> userSets = new ArrayList<SetData>();
		for (SetData set : sets) if (set.hasPlayer(id1) && set.hasPlayer(id2)) userSets.add(set);
		return userSets;
	}
	
	/**
	 * @param id1 user 1 id
	 * @param id2 user 2 id
	 * @return the most recent set these users have played
	 */
	@Nullable
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
	
	/**
	 * @param id1 user 1 that is in this league's id
	 * @param id2 user 2 that is in this league's id
	 * @return a new set between these 2 users
	 */
	@Nullable
	public SetData createSet(long id1, long id2) {
		if (id1 == id2) return null;
		if (getUserDataById(id1) == null) return null;
		if (getUserDataById(id2) == null) return null;
		SetData set = new SetData(getNewSetId(), id1, id2, UtilCalendar.getCurrentDateString());
		sets.add(set);
		return set;
	}
	
	private int getNewSetId() {
		int maxId = -1;
		for (SetData set : sets) if (set.getId() > maxId) maxId = set.getId();
		return maxId+1;
	}
	
	/**
	 * @param ud sort this list of users by their score descending
	 */
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
	
	/**
	 * @param date dd-mm-yyyy format
	 * @return list of sets that happened the week of date
	 */
	public List<SetData> getSetsAtWeekOfDate(String date) {
		List<SetData> saw = new ArrayList<SetData>();
		for (SetData set : sets) if (UtilCalendar.getWeekDiff(date, set.getCreatedDate()) == 0) saw.add(set);
		return saw;
	}
	
	/**
	 * display this league's sets that happened the week of this date
	 * @param date dd-mm-yyyy format
	 * @param channel the pairs channel to display this leagues sets in
	 */
	public void displaySetsByDate(String date, TextChannel channel) {
		for (SetData set : sets) if (UtilCalendar.getWeekDiff(date, set.getCreatedDate()) == 0) set.displaySet(channel);
	}
	
	/**
	 * update the scores of players based on completed sets.
	 * once a set is process it's score's can't be changed 
	 * @return the number of sets processed
	 */
	public int processSets() {
		int num = 0;
		for (SetData set : sets) if (set.isComplete() && !set.isProcessed()) {
			set.processSet(this);
			++num;
		}
		return num;
	}
	
	/**
	 * setup this league's role/channels so info can be displayed to the user
	 * @param guild this league's guild
	 * @param debugChannel channel debug info should be sent to
	 */
	public void setupDiscordStuff(Guild guild, MessageChannelUnion debugChannel) {
		//setup roles
		Role gamerRole = guild.getRoleById(getLeagueRoleId());
		if (gamerRole == null) {
			gamerRole = guild.createRole().complete();
			setLeagueRoleId(gamerRole.getIdLong());
			gamerRole.getManager()
				.setName("GAMERS")
				.setColor(getRandomColor())
				.queue();
		}
		//setup category
		Category gamerCat = guild.getCategoryById(getLeagueCategoryId());
		if (gamerCat == null) {
			gamerCat = guild.createCategory(name).complete();
			setLeagueCategoryId(gamerCat.getIdLong());
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
		TextChannel channel = guild.getTextChannelById(getChannelId(name));
		if (channel == null) {
			channel = cat.createTextChannel(name).complete();
			setChannelId(name, channel.getIdLong());
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
	
	/**
	 * automatically generate pairings for this week
	 * @param guild this league's guild
	 * @param debugChannel channel for debug messages
	 */
	public void genWeeklyPairs(Guild guild, MessageChannelUnion debugChannel) {
		debugChannel.sendMessage("Generating Pairs...").queue();
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, this.getChannelId("pairings"));
		this.removeOldSets(pairsChannel);
		List<UserData> activeUsers = this.getAvailableSortedUsers();
		boolean createdSet = true;
		while (createdSet) {
			createdSet = false;
			System.out.println("BIG LOOP");
			for (UserData udata : activeUsers) {
				System.out.println("user "+udata.getId());
				List<SetData> incompleteSets = this.getIncompleteOrCurrentSetsByPlayer(udata.getId());
				System.out.println("incomplete sets "+incompleteSets.size());
				if (incompleteSets.size() >= udata.getSetsPerWeek()) continue;
				int[] ksort = LeagueData.getClosestUserIndexsByScore(udata, activeUsers);
				for (int i = 0; i < ksort.length; ++i) {
					UserData userk = activeUsers.get(ksort[i]);
					System.out.println("userk "+userk.getId());
					List<SetData> incompleteSetsK = this.getIncompleteOrCurrentSetsByPlayer(userk.getId());
					System.out.println("incomplete sets k "+incompleteSetsK.size());
					if (incompleteSetsK.size() >= userk.getSetsPerWeek()) continue;
					SetData recentSet = this.getNewestSetBetweenUsers(udata.getId(), userk.getId());
					System.out.println("recent set "+recentSet);
					if (recentSet != null) {
						int diff = UtilCalendar.getWeekDiff(
								UtilCalendar.getDate(recentSet.getCreatedDate()), UtilCalendar.getCurrentDate());
						if (diff <= this.getWeeksBeforeSetRepeat()) continue;
					}
					long id1 = udata.getId(), id2 = activeUsers.get(ksort[i]).getId();
					if (id1 == id2) continue;
					this.createSet(id1, id2);
					createdSet = true;
					break;
				}
			}
		}
		this.displaySetsByDate(UtilCalendar.getCurrentDateString(), pairsChannel);
		debugChannel.sendMessage("Finished Generating Pairings!").queue();
	}
	
	public void updateRanks(Guild guild, MessageChannelUnion debugChannel) {
		Backup.createBackup(guild, "pre_updateranks_backup", debugChannel);
		int num = processSets();
		//display
		if (num == 0) {
			debugChannel.sendMessage("There were no sets ready to be processed!").queue();
			return;
		}
		debugChannel.sendMessage("Processed "+num+" sets! Ranks and backups are being updated!").queue();
		TextChannel ranksChannel = guild.getChannelById(TextChannel.class, getChannelId("ranks"));
		List<UserData> users = getAllUsers();
		LeagueData.sortByScoreDescend(users);
		MessageCreateBuilder mcb = new MessageCreateBuilder();
		mcb.addContent("__**"+UtilCalendar.getCurrentDateString()+" RANKS**__");
		int r = 0, r2 = 0, prevScore = Integer.MAX_VALUE;
		for (UserData user : users) {
			++r2;
			if (user.getScore() < prevScore) r = r2;
			mcb.addContent("\n**"+r+")** "+getMention(user.getId())+" **"+user.getScore()+"**");
			prevScore = user.getScore();
		}
		MessageCreateData mcd = mcb.build();
		ranksChannel.sendMessage(mcd).queue();
	}
	
	private String getMention(long id) {
		return "<@"+id+">";
	}
	
	protected void genScheduledPairs(Guild guild) {
		if (autoGenPairs) {
			MessageChannelUnion channel = guild.getChannelById(MessageChannelUnion.class, this.getChannelId("bot-commands"));
			genWeeklyPairs(guild, channel);
		}
	}
	
	protected void updateRanks(Guild guild) {
		if (autoGenPairs) {
			MessageChannelUnion channel = guild.getChannelById(MessageChannelUnion.class, this.getChannelId("bot-commands"));
			updateRanks(guild, channel);
		}
	}
	
}
