package com.onewho.gamerbot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilKClosest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class LeagueData implements Storable {
	
	private String name = "Gamer League";
	private String seasonStart = "";
	private String seasonEnd = "";
	private int seasonId = 1;
	
	private int maxSetsPerWeek = 3;
	private int weeksBeforeAutoInactive = -1;
	private int weeksBeforeSetExpires = -1;
	private int weeksUntilSetRepeat = 1;
	private int challengesPerWeek = 0;
	private int dayOfWeek = 1;
	private int defaultScore = 1000;
	private double K = 20d;
	private int penaltyScore = 20;
	
	public boolean autoGenPairs = false;
	public boolean autoUpdateRanks = false;
	
	private final List<UserData> users = new ArrayList<>();
	private final List<SetData> sets = new ArrayList<>();
    private final List<TeamData> teams = new ArrayList<>();
    private final List<QueueData> queues = new ArrayList<>();
	
	private long leagueRoleId = -1;
	private long toRoleId = -1;
	private long leagueCategoryId = -1;
	private long joinLeagueOptionId = -1;
	private long setsaweekOptionId = -1;
	private JsonObject channelIds = new JsonObject();

    private int defaultQueueMinPlayers = 2;
    private int defaultQueueTeamSize = 2;
    private int defaultQueueTimeoutTime = 900;
    private int defaultQueueSubRequestTime = 300;
    private int defaultQueuePregameTime = 600;
    private boolean defaultQueueAllowLargerTeams = true;
    private boolean defaultQueueAllowOddNum = true;
    private boolean defaultQueueResetTimeoutOnJoin = true;
    private boolean defaultQueueEnoughPlayersAutoStart = true;
    private boolean defaultQueueAllowJoinViaDiscord = true;
	private boolean defaultCloseIfEmpty = false;
	
	/**
	 * @param data league data written from disk
	 */
	protected LeagueData(JsonObject data) {
		name = ParseData.getString(data, "name", name);
		seasonStart = ParseData.getString(data, "season start", UtilCalendar.getCurrentDateString());
        if (seasonStart.isEmpty()) seasonStart = UtilCalendar.getCurrentDateString();
		seasonEnd = ParseData.getString(data, "season end", seasonEnd);
		seasonId = ParseData.getInt(data, "season id", seasonId);
		
		maxSetsPerWeek = ParseData.getInt(data, "max sets a week", maxSetsPerWeek);
		weeksBeforeAutoInactive = ParseData.getInt(data, "weeks before auto inactive", weeksBeforeAutoInactive);
		weeksBeforeSetExpires = ParseData.getInt(data, "weeks before set expires", weeksBeforeSetExpires);
		weeksUntilSetRepeat = ParseData.getInt(data, "weeks until set repeat", weeksUntilSetRepeat);
		challengesPerWeek = ParseData.getInt(data, "challenges per week", challengesPerWeek);
		defaultScore = ParseData.getInt(data, "default score", defaultScore);
		K = ParseData.getDouble(data, "K", K);
		penaltyScore = ParseData.getInt(data, "penaltyScore", penaltyScore);
		
		autoGenPairs = ParseData.getBoolean(data, "auto gen pairs", false);
		autoUpdateRanks = ParseData.getBoolean(data, "auto update ranks", false);

		JsonArray us = ParseData.getJsonArray(data, "users");
		for (int i = 0; i < us.size(); ++i) users.add(new UserData(us.get(i).getAsJsonObject()));
        teams.clear();
        JsonArray ts = ParseData.getJsonArray(data, "teams");
        for (int i = 0; i < ts.size(); ++i) teams.add(new TeamData(this, ts.get(i).getAsJsonObject()));
		sets.clear();
		JsonArray ss = ParseData.getJsonArray(data, "sets");
		for (int i = 0; i < ss.size(); ++i) sets.add(new SetData(this, ss.get(i).getAsJsonObject()));
        queues.clear();
        JsonArray qs = ParseData.getJsonArray(data, "queues");
        for (int i = 0; i < qs.size(); ++i) queues.add(new QueueData(qs.get(i).getAsJsonObject()));
		
		leagueRoleId = ParseData.getLong(data, "league role id", leagueRoleId);
		toRoleId = ParseData.getLong(data, "to role id", toRoleId);
		leagueCategoryId = ParseData.getLong(data, "league category id", leagueCategoryId);
		joinLeagueOptionId = ParseData.getLong(data, "join league option id", joinLeagueOptionId);
		setsaweekOptionId = ParseData.getLong(data, "setsaweek option id", setsaweekOptionId);
		channelIds = ParseData.getJsonObject(data, "channel ids");

        defaultQueueMinPlayers = ParseData.getInt(data, "defaultQueueMinPlayers", defaultQueueMinPlayers);
        defaultQueueTeamSize = ParseData.getInt(data, "defaultQueueTeamSize", defaultQueueTeamSize);
        defaultQueueTimeoutTime = ParseData.getInt(data, "defaultQueueTimeoutTime", defaultQueueTimeoutTime);
        defaultQueueSubRequestTime = ParseData.getInt(data, "defaultQueueSubRequestTime", defaultQueueSubRequestTime);
        defaultQueuePregameTime = ParseData.getInt(data, "defaultQueuePregameTime", defaultQueuePregameTime);
        defaultQueueAllowLargerTeams = ParseData.getBoolean(data, "defaultQueueAllowLargerTeams", defaultQueueAllowLargerTeams);
        defaultQueueAllowOddNum = ParseData.getBoolean(data, "defaultQueueAllowOddNum", defaultQueueAllowOddNum);
        defaultQueueResetTimeoutOnJoin = ParseData.getBoolean(data, "defaultQueueResetTimeoutOnJoin", defaultQueueResetTimeoutOnJoin);
        defaultQueueEnoughPlayersAutoStart = ParseData.getBoolean(data, "defaultQueueEnoughPlayersAutoStart", defaultQueueEnoughPlayersAutoStart);
        defaultQueueAllowJoinViaDiscord = ParseData.getBoolean(data, "defaultQueueAllowJoinViaDiscord", defaultQueueAllowJoinViaDiscord);
		defaultCloseIfEmpty = ParseData.getBoolean(data, "defaultCloseIfEmpty", defaultCloseIfEmpty);
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
		data.addProperty("season start", seasonStart);
		data.addProperty("season end", seasonEnd);
		data.addProperty("season id", seasonId);
		data.addProperty("max sets a week", maxSetsPerWeek);
		data.addProperty("weeks before auto inactive", weeksBeforeAutoInactive);
		data.addProperty("weeks before set expires", weeksBeforeSetExpires);
		data.addProperty("weeks until set repeat", weeksUntilSetRepeat);
		data.addProperty("challenges per week", challengesPerWeek);
		data.addProperty("default score", defaultScore);
		data.addProperty("K", K);
		data.addProperty("penaltyScore", penaltyScore);
		data.addProperty("auto gen pairs", autoGenPairs);
		data.addProperty("auto update ranks", autoUpdateRanks);
		data.add("users", getUsersJson());
		data.add("sets", getSetsJson());
        data.add("teams", getTeamsJson());
        data.add("queues", getQueuesJson());
		data.addProperty("league role id", leagueRoleId);
		data.addProperty("to role id", toRoleId);
		data.addProperty("league category id", leagueCategoryId);
		data.addProperty("join league option id", joinLeagueOptionId);
		data.addProperty("setsaweek option id", setsaweekOptionId);
		data.add("channel ids", channelIds);
		data.addProperty("defaultQueueMinPlayers", defaultQueueMinPlayers);
		data.addProperty("defaultQueueTeamSize", defaultQueueTeamSize);
		data.addProperty("defaultQueueTimeoutTime", defaultQueueTimeoutTime);
		data.addProperty("defaultQueueSubRequestTime", defaultQueueSubRequestTime);
		data.addProperty("defaultQueuePregameTime", defaultQueuePregameTime);
		data.addProperty("defaultQueueAllowLargerTeams", defaultQueueAllowLargerTeams);
		data.addProperty("defaultQueueAllowOddNum", defaultQueueAllowOddNum);
		data.addProperty("defaultQueueResetTimeoutOnJoin", defaultQueueResetTimeoutOnJoin);
		data.addProperty("defaultQueueEnoughPlayersAutoStart", defaultQueueEnoughPlayersAutoStart);
		data.addProperty("defaultQueueAllowJoinViaDiscord", defaultQueueAllowJoinViaDiscord);
		data.addProperty("defaultCloseIfEmpty", defaultCloseIfEmpty);
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
			if (user == null) user = createUser(id);
			user.readBackup(us.get(i).getAsJsonObject());
		}
        teams.clear();
        JsonArray ts = ParseData.getJsonArray(backup, "teams");
        for (int i = 0; i < ts.size(); ++i) teams.add(new TeamData(this, ts.get(i).getAsJsonObject()));
		sets.clear();
		JsonArray ss = ParseData.getJsonArray(backup, "sets");
		for (int i = 0; i < ss.size(); ++i) sets.add(new SetData(this, ss.get(i).getAsJsonObject()));
        queues.clear();
        JsonArray qs = ParseData.getJsonArray(backup, "queues");
        for (int i = 0; i < qs.size(); ++i) queues.add(new QueueData(qs.get(i).getAsJsonObject()));
	}
	
	private JsonArray getUsersJson() {
		JsonArray us = new JsonArray();
		for (Contestant u : users) us.add(u.getJson());
		return us;
	}
	
	private JsonArray getSetsJson() {
		JsonArray ss = new JsonArray();
		for (SetData s : sets) ss.add(s.getJson());
		return ss;
	}

    private JsonArray getTeamsJson() {
        JsonArray ts = new JsonArray();
        for (TeamData t : teams) ts.add(t.getJson());
        return ts;
    }

    private JsonArray getQueuesJson() {
        JsonArray qs = new JsonArray();
        for (QueueData q : queues) qs.add(q.getJson());
        return qs;
    }
	
	/**
	 * @return data needed to recover the current set and user data
	 */
	public JsonObject getBackupJson() {
		JsonObject backup = new JsonObject();
		backup.add("users", getUsersBackupJson());
		backup.add("sets", getSetsJson());
        backup.add("teams", getTeamsJson());
        backup.add("queues", getQueuesJson());
		return backup;
	}
	
	private JsonObject getOldSeasonJson() {
		JsonObject backup = new JsonObject();
		backup.addProperty("season id", seasonId);
		backup.addProperty("season start", seasonStart);
		backup.addProperty("season end", seasonEnd);
		backup.add("users", getUsersBackupJson());
		backup.add("sets", getSetsOldSeasonJson());
        backup.add("teams", getTeamsJson());
        backup.add("queues", getQueuesJson());
		return backup;
	}
	
	private JsonArray getUsersBackupJson() {
		JsonArray us = new JsonArray();
		for (Contestant u : users) us.add(u.getBackupJson());
		return us;
	}
	
	private JsonArray getSetsOldSeasonJson() {
		JsonArray ss = new JsonArray();
		for (SetData s : sets) if (s.isProcessed()) ss.add(s.getJson());
		return ss;
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
	 * @return the start date of the current season
	 */
	public String getSeasonStart() {
		return seasonStart;
	}
	
	/**
	 * @return the end date of the current season
	 */
	public String getSeasonEnd() {
		return seasonEnd;
	}
	
	/**
	 * @return the id of the current season
	 */
	public int getSeasonId() {
		return seasonId;
	}
	
	/**
	 * @param end set the end date of the current season. set as null if season never ends
	 * @return if it was a valid date
	 */
	public boolean setSeasonEnd(String end) {
		if (end == null || end.isEmpty()) {
			seasonEnd = "";
			return true;
		}
		if (UtilCalendar.parseTime(end) == null) return false;
		seasonEnd = end;
		return true;
	}
	
	/**
	 * @param start set the start date of the current season
	 * @return if it was a valid date
	 */
	public boolean setSeasonStart(String start) {
		if (start == null || start.isEmpty()) {
			return false;
		}
		if (UtilCalendar.parseTime(start) == null) return false;
		seasonStart = start;
		return true;
	}
	
	public boolean willSeasonEnd() {
		return !getSeasonEnd().isEmpty();
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
	public int setMaxSetsPerWeek(int max) {
		if (max < 0) max = 0;
		maxSetsPerWeek = max;
		return maxSetsPerWeek;
	}
	
	/**
	 * @return the number of challenges a user can be in per week. -1 means infinite
	 */
	public int getChallengesPerWeek() {
		return challengesPerWeek;
	}
	
	/**
	 * @param ch the number of challenges a user can be in per week. -1 means infinite
	 */
	public int setChallengesPerWeek(int ch) {
		if (ch < -1) ch = -1;
		challengesPerWeek = ch;
		return challengesPerWeek;
	}
    
    public void updateAllQueues(Guild guild) {
        MessageChannelUnion debug = getMessageChannelUnion(guild, "bot-commands");
        if (debug == null) return;
        queues.forEach(queue -> queue.update(guild, this, msg -> debug.sendMessage(msg).queue()));
    }

    @Nullable
    public QueueData getQueueById(int id) {
        for (QueueData queue : queues)
            if (queue.getId() == id)
                return queue;
        return null;
    }

    public QueueData createQueue() {
        QueueData queue = new QueueData(getNewQueueId(), UtilCalendar.getCurrentDateTimeString());
        resetQueueDefaults(queue);
        queues.add(queue);
        return queue;
    }

    public void resetQueueDefaults(@NotNull QueueData queue) {
        queue.setMinPlayers(defaultQueueMinPlayers);
        queue.setTeamSize(defaultQueueTeamSize);
        queue.setAllowLargerTeams(defaultQueueAllowLargerTeams);
        queue.setAllowOddNum(defaultQueueAllowOddNum);
        queue.setTimeoutTime(defaultQueueTimeoutTime);
        queue.setSubRequestTime(defaultQueueSubRequestTime);
        queue.setPregameTime(defaultQueuePregameTime);
        queue.setResetTimeoutOnJoin(defaultQueueResetTimeoutOnJoin);
        queue.setEnoughPlayersAutoStart(defaultQueueEnoughPlayersAutoStart);
        queue.setAllowJoinViaDiscord(defaultQueueAllowJoinViaDiscord);
		queue.setCloseIfEmpty(defaultCloseIfEmpty);
    }

    private int getNewQueueId() {
        int maxId = -1;
        for (QueueData queue : queues) if (queue.getId() > maxId) maxId = queue.getId();
        return maxId+1;
    }
	
	/**
	 * @param id User Id
	 * @return a user's league data or null if that user isn't in this league
	 */
    @Nullable
	public UserData getUserDataById(long id) {
        for (UserData user : users)
            if (user.getId() == id)
                return user;
		return null;
	}

    @Nullable
    public Contestant getContestantById(long id) {
        for (Contestant user : users)
            if (user.getId() == id)
                return user;
        for (Contestant team : teams)
            if (team.getId() == id)
                return team;
        return null;
    }

    @Nullable
    public UserData getUserByExtraData(String key, String value) {
        for (UserData user : users)
            if (user.getExtraData().has(key) && user.getExtraData().get(key).getAsString().equals(value))
                return user;
        return null;
    }

    @Nullable
    public TeamData getTeamByName(String name) {
        for (TeamData team : teams)
            if (team.getName().equals(name))
                return team;
        return null;
    }

    @Nullable
    public TeamData createTeam(String name, UserData... users) {
        if (getTeamByName(name) != null) return null;
        TeamData team = new TeamData(name, users);
        teams.add(team);
        return team;
    }

    public static String createTeamName(Guild guild, UserData... users) {
        String name = "";
        for (UserData user : users) {
            Member member = guild.getMemberById(user.getId());
            if (member != null) {
                String memName = member.getEffectiveName();
                if (memName.length() > 4) memName = memName.substring(0, 4);
                name += memName;
            }
        }
        return name;
    }
	
	/**
	 * add a user to this league by their id
	 * @param id
	 * @return the new user data
	 */
	protected UserData createUser(long id) {
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
        for (SetData set : sets) if (set.getId() == id) return set;
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
	public int setWeeksBeforeAutoInactive(int weeks) {
		if (weeks < -1) weeks = -1;
		weeksBeforeAutoInactive = weeks;
		return weeksBeforeAutoInactive;
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
	 * @return the id of the role given to TOs of this league by admin
	 */
	public long getToRoleId() {
		return toRoleId;
	}
	
	/**
	 * @param toRoleId the id of the role given to TOs of this league by admin
	 */
	public void setToRoleId(long toRoleId) {
		this.toRoleId = toRoleId;
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
	public int setDefaultScore(int defaultScore) {
		this.defaultScore = defaultScore;
		return this.defaultScore;
	}
	
	/**
	 * @return weeks before 2 players are allowed to be automatically paired together
	 */
	public int getWeeksUntilSetRepeat() {
		return weeksUntilSetRepeat;
	}
	
	/**
	 * set weeks until 2 players are allowed to be automatically paired together
	 * @param weeksUntilSetRepeat 1 = instance of a pairing a week. 2 = 1 instance every 2 weeks.
	 */
	public int setWeeksUntilSetRepeat(int weeksUntilSetRepeat) {
		if (weeksUntilSetRepeat < 1) weeksUntilSetRepeat = 1;
		this.weeksUntilSetRepeat = weeksUntilSetRepeat;
		return this.weeksUntilSetRepeat;
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
	public int setWeeksBeforeSetExpires(int weeks) {
		if (weeks < -1) weeks = -1;
		this.weeksBeforeSetExpires = weeks;
		return this.weeksBeforeSetExpires;
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
	
	public int getNumberOfCachedSets() {
		return sets.size();
	}
	
	/**
	 * resets all user scores to the default score and makes all users inactive
	 */
	private void resetAllUsers() {
		for (Contestant ud : users) {
			ud.setScore(getDefaultScore());
		}
	}

	/**
	 * @return list of all users league data 
	 */
	public List<UserData> getActiveUsers() {
		List<UserData> active = new ArrayList<UserData>();
		for (UserData user : users) if (user.isActive()) active.add(user);
		return active;
	}
	
	/**
	 * @return all users that have completed a set this season
	 */
	public List<UserData> getActiveUsersThisSeason() {
		List<UserData> active = new ArrayList<>();
		for (UserData user : users)
			if (user.isActive()/* && UtilCalendar.isNewer(user.getLastActive(), getSeasonStart())*/)
				active.add(user);
		return active;
	}
	
	/**
	 * remove incomplete sets based on weeks before auto expire
	 * @param pairsChannel the pairs channel of this league
	 */
	private void removeOldSets(TextChannel pairsChannel) {
		//System.out.println("REMOVING OLD SETS");
        for (SetData set : sets) {
            if (set.isComplete()) continue;
            int weekDiff = UtilCalendar.getWeekDiffByWeekDayFromNow(
                    set.getCreatedDate(), dayOfWeek);
            //System.out.println("SET "+sets.get(i)+" weekDiff = "+weekDiff+" > "+weeksBeforeSetExpires);
            if (weeksBeforeSetExpires == -1 || weekDiff <= weeksBeforeSetExpires) continue;
            removeSet(set.getId(), pairsChannel);
            //System.out.println("removed");
        }
	}
	
	private int removeUnprocessedSets(Guild guild) {
		TextChannel pairsChannel = getTextChannel(guild, "pairings");
		if (pairsChannel == null) return 0;
		int success = 0;
		for (int i = 0; i < sets.size(); ++i) {
			SetData set = sets.get(i);
			if (!set.isProcessed() && removeSet(set.getId(), pairsChannel)) {
				++success;
				--i;
			}
		}
		return success;
	}
	
	/**
	 * @param guild the guild this league is in
	 * @param debugChannel a channel in this league
	 * @param ids list of set ids to remove
	 * @return number of sets successfully removed
	 */
	public int removeSets(Guild guild, MessageChannelUnion debugChannel, int[] ids) {
		backup(guild, debugChannel, "pre_removesets_backup");
		TextChannel pairsChannel = getTextChannel(guild, "pairings");
		if (pairsChannel == null) return 0;
		int success = 0;
        for (int id : ids) if (removeSet(id, pairsChannel)) ++success;
		return success;
	}
	
	private boolean removeSet(int id, TextChannel pairsChannel) {
		SetData set = getSetDataById(id);
		if (set == null) return false;
		if (!set.removeSetDisplay(pairsChannel)) return false;
		return sets.remove(set);
	}
	
	/**
	 * @return a list of active/available user data
	 */
	public List<UserData> getAvailableSortedUsers(Guild guild) {
		//System.out.println("GETTING AVAILABLE USERS");
		List<UserData> available = new ArrayList<UserData>();
        for (UserData user : users) {
            if (!user.isActive()) continue;
            if (user.getSetsPerWeek() < 1) continue;
            int weekDiff = UtilCalendar.getWeekDiffByWeekDayFromNow(user.getLastActive(), dayOfWeek);
            //System.out.println("last active week diff = "+weekDiff+" <= "+weeksBeforeAutoInactive);
            if (weeksBeforeAutoInactive != -1 && weekDiff > weeksBeforeAutoInactive) {
                user.setActive(false);
                user.setSetsPerWeek(0);
                continue;
            }
            if (guild.getMemberById(user.getId()) == null) {
                user.setActive(false);
                user.setSetsPerWeek(0);
            }
            //System.out.println("added user "+users.get(i));
            available.add(user);
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
					&& (UtilCalendar.getWeekDiffByWeekDayFromNow(
						set.getCreatedDate(), dayOfWeek) == 0
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
		if (userSets.isEmpty()) return null;
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
        UserData ud1 = getUserDataById(id1);
        UserData ud2 = getUserDataById(id2);
		if (ud1 == null) return null;
		if (ud2 == null) return null;
		SetData set = new SetData(getNewSetId(), ud1, ud2, UtilCalendar.getCurrentDateTimeString());
		sets.add(set);
		return set;
	}

    @Nullable
    public SetData createTeamSet(@NotNull String team1Name, @NotNull String team2Name) {
        if (team1Name.equals(team2Name)) return null;
        TeamData team1 = getTeamByName(team1Name);
        TeamData team2 = getTeamByName(team2Name);
        if (team1 == null) return null;
        if (team2 == null) return null;
        if (team1.hasOverlappingMembers(team2)) return null;
        SetData set = new SetData(getNewSetId(), team1, team2, UtilCalendar.getCurrentDateTimeString());
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
	public static <C extends Contestant> void sortByScoreDescend(List<C> ud) {
		for (int i = 0; i < ud.size(); ++i) {
			int maxIndex = i;
			for (int j = i+1; j < ud.size(); ++j) {
				if (ud.get(j).getScore() > ud.get(maxIndex).getScore()) {
					maxIndex = j;
				}
			}
            C temp = ud.get(maxIndex);
			ud.set(maxIndex, ud.get(i));
			ud.set(i, temp);
		}
	}
	
	public static int[] getClosestUserIndexsByScore(UserData user, List<UserData> sortedUsers) {
		int[] scores = new int[sortedUsers.size()];
		for (int i = 0; i < scores.length; ++i) scores[i] = sortedUsers.get(i).getScore();
		return UtilKClosest.getKClosestIndexArray(scores, sortedUsers.indexOf(user), scores.length-1);
	}
	
	/**
	 * @param date dd-mm-yyyy format
	 * @return list of sets that happened the week of date
	 */
	public List<SetData> getSetsAtWeekOfDate(String date) {
		List<SetData> saw = new ArrayList<SetData>();
		for (SetData set : sets) if (UtilCalendar.getWeekDiffByWeekDay(date, set.getCreatedDate(), dayOfWeek) == 0) saw.add(set);
		return saw;
	}
	
	public List<SetData> getCurrentChallengesByPlayer(long id) {
		List<SetData> c = getSetsAtWeekOfDate(UtilCalendar.getCurrentDateString());
		for (int i = 0; i < c.size(); ++i) {
			if (!c.get(i).isChallenge() || !c.get(i).hasPlayer(id)) c.remove(i--);
		}
		return c;
	}
	
	/**
	 * display this league's sets that happened the week of this date
	 * @param date dd-mm-yyyy format
	 * @param channel the pairs channel to display this leagues sets in
	 */
	public void displaySetsByDate(String date, TextChannel channel) {
		for (SetData set : sets) if (UtilCalendar.getWeekDiffByWeekDay(date, set.getCreatedDate(), dayOfWeek) == 0) set.displaySet(channel);
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
	
	// TODO these should throw exceptions to send error messages idiot
	
	public String addUser(Guild guild, long id, boolean ignoreLock) {
		UserData userData = getUserDataById(id);
		if (userData == null) userData = createUser(id);
		if (!ignoreLock && userData.isLocked()) 
			return "You are not allowed to join this league because a TO locked you out!";
		Member m = guild.getMemberById(id);
		if (m == null) 
			return "User with ID "+id+" isn't in this server or doesn't exist!";
		guild.addRoleToMember(m, guild.getRoleById(getLeagueRoleId())).queue();
		userData.setActive(true);
		userData.setLastActive(UtilCalendar.getCurrentDateString());
		GlobalData.markReadyToSave();
		return "You have joined the Gamer League! Please select how many sets you want to do per week!"
				+ " Use $help in #bot-commands for more info!";
	}
	
	public boolean removeUser(Guild guild, MessageChannelUnion debugChannel, long id) {
		UserData userData = getUserDataById(id);
		if (userData == null) {
			debugChannel.sendMessage("That player isn't in this league!").queue();
			return false;
		}
		guild.removeRoleFromMember(guild.getMemberById(id), guild.getRoleById(getLeagueRoleId())).queue();
		userData.setActive(false);
		userData.setSetsPerWeek(0);
		debugChannel.sendMessage("Removed "+getMention(id)+" from this league!").queue();
		return true;
	}
	
	public boolean postUserData(Guild guild, MessageChannelUnion debugChannel, long id) {
		UserData userData = getUserDataById(id);
		if (userData == null) {
			debugChannel.sendMessage("That player isn't in this league!").queue();
			return false;
		}
		String print = "__**"+getMention(id)+" Data**__"
				+ "\n**Active**: " + userData.isActive()
				+ "\n**Last Active**: " + userData.getLastActive()
				+ "\n**Sets/Week**: " + userData.getSetsPerWeek()
				+ "\n**Score**: " + userData.getScore()
				+ "\n**Locked**: " + userData.isLocked();
		debugChannel.sendMessage(print).queue();
		return true;
	}
	
	public boolean lockUser(Guild guild, MessageChannelUnion debugChannel, long id, boolean lock) {
		UserData userData = getUserDataById(id);
		if (userData == null) {
			debugChannel.sendMessage("That player isn't in this league!").queue();
			return false;
		}
		if (lock) {
			userData.lockUser();
			debugChannel.sendMessage("That player's options have been locked!").queue();
		} else {
			userData.unlockUser();
			debugChannel.sendMessage("That player's options have been unlocked!").queue();
		}
		return true;
	}
	
	public boolean userSetsPerWeek(Guild guild, MessageChannelUnion debugChannel, long id, int setsPerWeek) {
		UserData userData = getUserDataById(id);
		if (userData == null) {
			debugChannel.sendMessage("That player isn't in this league!").queue();
			return false;
		}
		if (setsPerWeek < 0) setsPerWeek = 0;
		userData.setSetsPerWeek(setsPerWeek);
		debugChannel.sendMessage("That player has been set to "+setsPerWeek+" Sets Per Week!").queue();
		return true;
	}

    public boolean userOverrideScore(Guild guild, MessageChannelUnion debugChannel, long id, int score) {
        UserData userData = getUserDataById(id);
        if (userData == null) {
            debugChannel.sendMessage("That player isn't in this league!").queue();
            return false;
        }
        userData.setScore(score);
        debugChannel.sendMessage("That player's score has been overridden to "+score).queue();
        return true;
    }
	
	/**
	 * setup this league's role/channels so info can be displayed to the user
	 * @param guild this league's guild
	 * @param debugChannel channel debug info should be sent to
	 */
	public void setupDiscordStuff(Guild guild, MessageChannelUnion debugChannel) {
		try { 
			setupRoles(guild, debugChannel); 
			Category leagueCategory = setupCategory(guild, debugChannel);
			setupChannels(guild, debugChannel, leagueCategory);
		} catch (InsufficientPermissionException e) {
			insufficientPermissionError(guild, debugChannel, e);
			System.out.println("PERMISSION ERROR: League discord setup for "+getName()+" failed"
					+ " because this permission is missing! "+e.getPermission());
			System.out.println(e.getMessage());
			e.printStackTrace();
			return;
		}
		GlobalData.markReadyToSave();
		debugChannel.sendMessage("Bot Channel Setup for League "+name+" Complete!").queue();		
	}
	
	private void insufficientPermissionError(Guild guild, MessageChannelUnion debugChannel, InsufficientPermissionException e) {
		debugChannel.sendMessage(Important.getError()+" Discord stuff setup for "+getName()
				+ " failed because of a permission error! Please verify that "
				+ getRoleMention(guild.getBotRole().getIdLong())+" the following permission! "
				+ "`"+e.getPermission().getName()+"`")
			.queue();
		debugChannel.sendMessage("One possible fix is synching all the league channels with the"
				+ " category and then running this command again.")
			.queue();
	}
	
	private void setupChannels(Guild guild, MessageChannelUnion debugChannel, Category leagueCategory) throws InsufficientPermissionException {
		TextChannel rulesChannel = setupChannel("rules", leagueCategory, guild);
		TextChannel chatChannel = setupChannel("chat", leagueCategory, guild);
		TextChannel commandsChannel = setupChannel("bot-commands", leagueCategory, guild);
		TextChannel optionsChannel = setupChannel("options", leagueCategory, guild);
		setupChannel("set-history", leagueCategory, guild);
		setupChannel("ranks", leagueCategory, guild);
		setupChannel("pairings", leagueCategory, guild);
		setupChannel("queues", leagueCategory, guild);
		// SPECIAL PERMS
		Collection<Permission> viewPerm = new ArrayList<Permission>();
		viewPerm.add(Permission.VIEW_CHANNEL);
		Collection<Permission> chatPerm = new ArrayList<Permission>();
		chatPerm.add(Permission.MESSAGE_SEND);
		chatPerm.add(Permission.MESSAGE_ADD_REACTION);
		// RULES
		rulesChannel.getManager()
			.putRolePermissionOverride(guild.getPublicRole().getIdLong(), viewPerm, chatPerm)
			.complete();
		// OPTIONS
		optionsChannel.getManager()
			.putRolePermissionOverride(guild.getPublicRole().getIdLong(), viewPerm, chatPerm)
			.complete();
		// CHAT
		chatPerm.addAll(viewPerm);
		chatChannel.getManager()
			.putRolePermissionOverride(getLeagueRoleId(), chatPerm, null)
			.complete();
		// COMMANDS
		commandsChannel.getManager()
			.putRolePermissionOverride(getLeagueRoleId(), chatPerm, null)
			.complete();
		// SETUP SPECIAL CHANNELS
		setupOptions(optionsChannel);
	}
	
	private TextChannel setupChannel(String name, Category cat, Guild guild) throws InsufficientPermissionException {
		TextChannel channel = guild.getTextChannelById(getChannelId(name));
		if (channel == null) {
			channel = cat.createTextChannel(name).complete();
			setChannelId(name, channel.getIdLong());
		}
		channel.getManager().sync(cat.getPermissionContainer()).complete();
		return channel;
	}
	
	private Category setupCategory(Guild guild, MessageChannelUnion debugChannel) throws InsufficientPermissionException{
		Category leagueCategory = guild.getCategoryById(getLeagueCategoryId());
		if (leagueCategory == null) {
			leagueCategory = guild.createCategory(name).complete();
			setLeagueCategoryId(leagueCategory.getIdLong());
		} else leagueCategory.getManager().setName(name).queue();
		// BOT PERMS 
		Collection<Permission> botPermsAllow = new ArrayList<Permission>();
		botPermsAllow.add(Permission.VIEW_CHANNEL);
		botPermsAllow.add(Permission.MESSAGE_SEND);
		botPermsAllow.add(Permission.MESSAGE_ADD_REACTION);
		// PUBLIC PERMS
		Collection<Permission> publicPermsAllow = new ArrayList<Permission>();
		publicPermsAllow.add(Permission.MESSAGE_HISTORY);
		Collection<Permission> publicPermsDeny = new ArrayList<Permission>();
		publicPermsDeny.add(Permission.VIEW_CHANNEL);
		publicPermsDeny.add(Permission.MESSAGE_SEND);
		publicPermsDeny.add(Permission.MESSAGE_ADD_REACTION);
		// LEAGUE PERMS
		Collection<Permission> leaguePermsAllow = new ArrayList<Permission>();
		leaguePermsAllow.add(Permission.VIEW_CHANNEL);
		// TO PERMS
		Collection<Permission> toPermsAllow = new ArrayList<Permission>();
		toPermsAllow.add(Permission.VIEW_CHANNEL);
		toPermsAllow.add(Permission.MESSAGE_SEND);
		toPermsAllow.add(Permission.MESSAGE_ATTACH_FILES);
		toPermsAllow.add(Permission.MESSAGE_MANAGE);
		// SET PERMS (ORDER MATTERS)
		leagueCategory.getManager()
			.putRolePermissionOverride(guild.getBotRole().getIdLong(), botPermsAllow, null)
			.putRolePermissionOverride(guild.getPublicRole().getIdLong(), publicPermsAllow, publicPermsDeny)
			.putRolePermissionOverride(getLeagueRoleId(), leaguePermsAllow, null)
			.putRolePermissionOverride(getToRoleId(), toPermsAllow, null)
			.complete();
		return leagueCategory;
	}
	
	private void setupRoles(Guild guild, MessageChannelUnion debugChannel) throws InsufficientPermissionException {
		Color color = getRandomColor();
		Role toRole = guild.getRoleById(getToRoleId());
		if (toRole == null) {
			toRole = guild.createRole().setColor(color).complete();
			setToRoleId(toRole.getIdLong());
		}
		toRole.getManager().setName("TO "+name).queue();
		Role gamerRole = guild.getRoleById(getLeagueRoleId());
		if (gamerRole == null) {
			gamerRole = guild.createRole().setColor(color).complete();
			setLeagueRoleId(gamerRole.getIdLong());
		}
		gamerRole.getManager().setName(name).queue();
	}
	
	private Color getRandomColor() {
		Random random = new Random();
		float hue = random.nextFloat();
		float saturation = 0.9f;
		float luminance = 1.0f;
		return Color.getHSBColor(hue, saturation, luminance);
	}
	
	/**
	 * Used to change re-configure the buttons in the options channel.
	 * Mostly used after the sets-per-week config is changed.
	 * @param guild this league's guild
	 */
	public void updateOptions(Guild guild) {
		TextChannel options = guild.getTextChannelById(getChannelId("options"));
		if (options == null) return;
		setupOptions(options);
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
		jleb.setTitle("Join "+getName()+"?");
		jleb.setColor(getRandomColor());
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
		sweb.setColor(getRandomColor());
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
		System.out.println("GENERATING WEEKLY PAIRS: "+getName());
		debugChannel.sendMessage("Generating Pairs...").queue();
		TextChannel pairsChannel = getTextChannel(guild, "pairings");
		if (pairsChannel == null) {
			debugChannel.sendMessage(Important.getError()+
					" Can't generate pairings because the pairings channel is gone!").queue();
			return;
		}
		removeOldSets(pairsChannel);
		List<UserData> activeUsers = getAvailableSortedUsers(guild);
		List<SetData> newSets = new ArrayList<SetData>();
		boolean createdSet = true;
		while (createdSet) {
			createdSet = false;
			//System.out.println("=====");
			//System.out.println("BIG LOOP");
			for (UserData udata : activeUsers) {
				//System.out.println("USER LOOP");
				//System.out.println("user "+udata);
				if (hasEnoughSets(udata)) continue;
				int[] ksort = LeagueData.getClosestUserIndexsByScore(udata, activeUsers);
				//UtilDebug.printIntArray("K LOOP index sort", ksort);
                for (int j : ksort) {
                    UserData userk = activeUsers.get(j);
                    long id1 = udata.getId(), id2 = userk.getId();
                    if (id1 == id2) continue;
                    //System.out.println("user k "+userk);
                    if (hasEnoughSets(userk)) continue;
                    if (willSetRepeat(udata, userk)) continue;
                    SetData newSet = createSet(id1, id2);
                    if (newSet != null) {
                        //System.out.println("created set "+newSet);
                        newSets.add(newSet);
                        createdSet = true;
                        break;
                    }
                }
			}
		}
		for (SetData set : newSets) set.displaySet(pairsChannel);
		if (!newSets.isEmpty()) debugChannel.sendMessage("Generated "+newSets.size()+" new Pairings!").queue();
		else debugChannel.sendMessage("No new pairings were generated!").queue();
	}
	
	public boolean hasEnoughSets(UserData udata) {
		List<SetData> incompleteSets = getIncompleteOrCurrentSetsByPlayer(udata.getId());
		//System.out.println("incomplete sets "+incompleteSets.size());
		return incompleteSets.size() >= udata.getSetsPerWeek();
	}
	
	public boolean willSetRepeat(UserData u1, UserData u2) {
		SetData recentSet = getNewestSetBetweenUsers(u1.getId(), u2.getId());
		if (recentSet != null) {
			int diff = UtilCalendar.getWeekDiffByWeekDayFromNow(
					recentSet.getCreatedDate(), dayOfWeek);
			//System.out.println("recent set week diff "+diff);
            return diff < getWeeksUntilSetRepeat();
		}
		return false;
	}
	
	public boolean canChallenge(long id1, long id2) {
		int max = getChallengesPerWeek();
		if (max == -1) return true;
		else if (max == 0) return false;
		if (getCurrentChallengesByPlayer(id1).size() >= max) return false;
        return getCurrentChallengesByPlayer(id2).size() < max;
    }
	
	public boolean createChallenge(Guild guild, MessageChannelUnion debugChannel, long id1, long id2) {
		if (!canChallenge(id1, id2)) {
			debugChannel.sendMessage(Important.getError()+" Either you or the opponent ran out of challenges for the week!").queue();
			return false;
		}
		SetData set = createSet(id1, id2);
		if (set == null) {
			debugChannel.sendMessage(Important.getError()+" This challenge could not be created!").queue();
			return false;
		}
		set.setChallenge();
		debugChannel.sendMessage("Successfully created challlenge! "+set.getId()).queue();
		TextChannel pairsChannel = getTextChannel(guild, "pairings");
		set.displaySet(pairsChannel);
		return true;
	}

    public void updateRanks(Guild guild, MessageChannelUnion debugChannel, boolean finalized) {
        updateRanks(guild, debugChannel, finalized, false);
    }

	public void updateRanks(Guild guild, MessageChannelUnion debugChannel, boolean finalized, boolean force) {
		if (!finalized) backup(guild, debugChannel, "pre_updateranks_backup");
		int num = processSets();
		// TODO elo decay?
		//display
		if (num == 0 && !finalized && !force) {
			debugChannel.sendMessage("There were no sets ready to be processed!").queue();
			return;
		}
		TextChannel ranksChannel = getTextChannel(guild, "ranks");
		List<UserData> users = getActiveUsersThisSeason();
		LeagueData.sortByScoreDescend(users);
        debugChannel.sendMessage("Processed "+num+" sets! Ranks for "+users.size()
                +" users and backups are being updated!").queue();
        MessageCreateData mcd = createMessage(finalized, users);
		if (ranksChannel != null) ranksChannel.sendMessage(mcd).queue();
	}

    private @NotNull MessageCreateData createMessage(boolean finalized, List<UserData> users) {
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        String date = UtilCalendar.toDiscordTime(UtilCalendar.getCurrentDateTimeString());
        if (finalized) mcb.addContent("__**END OF SEASON "+getSeasonId()+" RANKS "+date+"**__");
        else mcb.addContent("__**Current Season "+getSeasonId()+" Ranks "+date+"**__");
        int r = 0, r2 = 0, prevScore = Integer.MAX_VALUE;
        for (UserData user : users) {
            ++r2;
            if (user.getScore() < prevScore) r = r2;
            if (finalized) {
                mcb.addContent("\n");
                if (r == 1) mcb.addContent(":video_game:");
                else if (r == 2) mcb.addContent(":second_place:");
                else if (r == 3) mcb.addContent(":third_place:");
                else mcb.addContent("**"+r+")**");
                mcb.addContent(" ");
            }
            else mcb.addContent("\n**"+r+")** ");
            mcb.addContent(getMention(user.getId())+" **"+user.getScore()+"**");
            prevScore = user.getScore();
        }
        return mcb.build();
    }

    public void updateRanks(Guild guild, MessageChannelUnion debugChannel) {
		updateRanks(guild, debugChannel, false);
	}
	
	private String getMention(long id) {
		return "<@"+id+">";
	}
	
	private String getRoleMention(long id) {
		return "<@&"+id+">";
	}
	
	protected void genScheduledPairs(Guild guild) {
		if (autoGenPairs) {
			MessageChannelUnion channel = getMessageChannelUnion(guild, "bot-commands");
			if (channel != null) genWeeklyPairs(guild, channel);
		}
	}
	
	protected void updateRanks(Guild guild) {
		if (autoUpdateRanks) {
			MessageChannelUnion channel = getMessageChannelUnion(guild, "bot-commands");
            if (channel == null) return; 
			if (shouldStartNewSeason()) startNewSeason(guild, channel);
			else updateRanks(guild, channel);
		}
	}

	public boolean cancelSet(int setId, Collection<UserData> penaltyUsers, Guild guild, Consumer<String> debug) {
		SetData set = getSetDataById(setId);
		if (set == null) {
			debug.accept("There is no set with ID "+setId);
			return false;
		}
		for (UserData user : penaltyUsers) {
			user.changeScore(-getPenaltyScore());
			user.lockUser();
		}
		TextChannel pairsChannel = getTextChannel(guild, "pairings");
		set.cancelSet(pairsChannel);
		debug.accept("Set "+setId+" was canceled! "+penaltyUsers.size()
				+" players received an elo penalty of "+getPenaltyScore());
		return true;
	}
	
	/**
	 * backup this league
	 * @param guild
	 * @param debugChannel
	 * @return if backup worked
	 */
	public boolean backup(Guild guild, MessageChannelUnion debugChannel, String backupName) {
		return uploadJson(guild, debugChannel, getBackupJson(),
				guild.getName()+"_"+getName()+"_"+backupName+"_"+UtilCalendar.getCurrentDateTimeString());
	}
	
	/**
	 * @param guild
	 * @param debugChannel
	 * @param backupName
	 * @param backupChannel
	 * @return if backup worked
	 */
	public boolean backup(Guild guild, MessageChannelUnion debugChannel, String backupName, TextChannel backupChannel) {
		return uploadJson(guild, debugChannel, backupChannel, getBackupJson(),
				guild.getName()+"_"+getName()+"_"+backupName+"_"+UtilCalendar.getCurrentDateTimeString());
	}
	
	public boolean uploadJson(Guild guild, MessageChannelUnion debugChannel, JsonObject json, String fileName) {
		TextChannel historyChannel = getTextChannel(guild, "set-history");
		if (historyChannel == null) {
			debugChannel.sendMessage(Important.getError()+" Can't backup because the backup channel is gone!").queue();
			return false;
		}
		return uploadJson(guild, debugChannel, historyChannel, json, fileName);
	}
	
	public boolean uploadJson(Guild guild, MessageChannelUnion debugChannel, TextChannel backupChannel, JsonObject json, String fileName) {
		String data = GlobalData.getGson().toJson(json);
		FileUpload fu = FileUpload.fromData(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), fileName+".json");
		backupChannel.sendFiles(fu).queue();
		try { fu.close(); } 
		catch (IOException e) { 
			debugChannel.sendMessage(Important.getError()+" An error occured while uploading: "+e.getMessage()).queue();
			e.printStackTrace(); 
			return false; 
		}
		return true;
	}
	
	/**
	 * start a new season
	 * @param guild
	 * @param debugChannel
	 * @return if starting new season worked
	 */
	public boolean startNewSeason(Guild guild, MessageChannelUnion debugChannel) {
		// upload backups and announce final stats
		backup(guild, debugChannel, "pre_newseason_backup"); 
		updateRanks(guild, debugChannel, true);
		if (!willSeasonEnd()) setSeasonEnd(UtilCalendar.getCurrentDateString());
		uploadJson(guild, debugChannel, getOldSeasonJson(),
				guild.getName()+"_"+getName()+"_SEASON_"+getSeasonId()+"_DATA_"+UtilCalendar.getCurrentDateTimeString());
		debugChannel.sendMessage("This Season's data has been uploaded to the #set-history channel."
				+ " Please save it on a hard drive and bury it incase of apocolypse.").queue();
		// start new season
		++seasonId;
		seasonStart = UtilCalendar.getCurrentDateString();
		setSeasonEnd(null);
		resetAllUsers();
		removeUnprocessedSets(guild);
		TextChannel pairsChannel = getTextChannel(guild, "pairings");
		if (pairsChannel != null) {
			pairsChannel.sendMessage("__**SEASON "+getSeasonId()+" HAS BEGUN!**__").queue();
		}
		sets.clear();
        queues.clear();
		GlobalData.markReadyToSave();
		return true;
	}
	
	public boolean shouldStartNewSeason() {
		if (!willSeasonEnd()) return false;
        OffsetDateTime now = UtilCalendar.getCurrentDate();
        OffsetDateTime end = UtilCalendar.parseTime(getSeasonEnd());
		return !UtilCalendar.isOlder(now, end);
	}

    public int getDefaultQueueMinPlayers() {
        return defaultQueueMinPlayers;
    }

    public void setDefaultQueueMinPlayers(int defaultQueueMinPlayers) {
        this.defaultQueueMinPlayers = defaultQueueMinPlayers;
    }

    public int getDefaultQueueTeamSize() {
        return defaultQueueTeamSize;
    }

    public void setDefaultQueueTeamSize(int defaultQueueTeamSize) {
        this.defaultQueueTeamSize = defaultQueueTeamSize;
    }

    public int getDefaultQueueTimeoutTime() {
        return defaultQueueTimeoutTime;
    }

    public void setDefaultQueueTimeoutTime(int defaultQueueTimeoutTime) {
        this.defaultQueueTimeoutTime = defaultQueueTimeoutTime;
    }

    public int getDefaultQueueSubRequestTime() {
        return defaultQueueSubRequestTime;
    }

    public void setDefaultQueueSubRequestTime(int defaultQueueSubRequestTime) {
        this.defaultQueueSubRequestTime = defaultQueueSubRequestTime;
    }

    public int getDefaultQueuePregameTime() {
        return defaultQueuePregameTime;
    }

    public void setDefaultQueuePregameTime(int defaultQueuePregameTime) {
        this.defaultQueuePregameTime = defaultQueuePregameTime;
    }

    public boolean isDefaultQueueAllowLargerTeams() {
        return defaultQueueAllowLargerTeams;
    }

    public void setDefaultQueueAllowLargerTeams(boolean defaultQueueAllowLargerTeams) {
        this.defaultQueueAllowLargerTeams = defaultQueueAllowLargerTeams;
    }

    public boolean isDefaultQueueAllowOddNum() {
        return defaultQueueAllowOddNum;
    }

    public void setDefaultQueueAllowOddNum(boolean defaultQueueAllowOddNum) {
        this.defaultQueueAllowOddNum = defaultQueueAllowOddNum;
    }

    public boolean isDefaultQueueResetTimeoutOnJoin() {
        return defaultQueueResetTimeoutOnJoin;
    }

    public void setDefaultQueueResetTimeoutOnJoin(boolean defaultQueueResetTimeoutOnJoin) {
        this.defaultQueueResetTimeoutOnJoin = defaultQueueResetTimeoutOnJoin;
    }

    public boolean isDefaultQueueEnoughPlayersAutoStart() {
        return defaultQueueEnoughPlayersAutoStart;
    }

    public void setDefaultQueueEnoughPlayersAutoStart(boolean defaultQueueEnoughPlayersAutoStart) {
        this.defaultQueueEnoughPlayersAutoStart = defaultQueueEnoughPlayersAutoStart;
    }

    public boolean isDefaultQueueAllowJoinViaDiscord() {
        return defaultQueueAllowJoinViaDiscord;
    }

    public void setDefaultQueueAllowJoinViaDiscord(boolean defaultQueueAllowJoinViaDiscord) {
        this.defaultQueueAllowJoinViaDiscord = defaultQueueAllowJoinViaDiscord;
    }

	public boolean isDefaultCloseIfEmpty() {
		return defaultCloseIfEmpty;
	}

	public void setDefaultCloseIfEmpty(boolean closeIfEmpty) {
		this.defaultCloseIfEmpty = closeIfEmpty;
	}

	public int getPenaltyScore() {
		return penaltyScore;
	}

	public int setPenaltyScore(int penaltyScore) {
		if (penaltyScore < 0) penaltyScore = 0;
		this.penaltyScore = penaltyScore;
		return this.penaltyScore;
	}

    @Nullable
    public MessageChannelUnion getMessageChannelUnion(Guild guild, String channelName) {
        return guild.getChannelById(MessageChannelUnion.class, getChannelId(channelName));
    }

    @Nullable
    public TextChannel getTextChannel(Guild guild, String channelName) {
        return guild.getChannelById(TextChannel.class, getChannelId(channelName));
    }
}
