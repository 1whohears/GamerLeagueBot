package com.onewho.gamerbot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class QueueData implements Storable {

    private final int id;
    @NotNull private final Map<Long,QueueMember> members = new HashMap<>();
    @NotNull private final Map<String,Set<Long>> preferredTeams = new HashMap<>();

    @NotNull private String startTime;
    private boolean resolved;
    private boolean isDirty = true;
    private QueueState queueState = QueueState.NONE;
    private String recentJoinTime = "";
    private String pregameStartTime = "";
    private long messageId = -1;

    private int minPlayers; // TODO minPlayers | default value and override command
    private int teamSize; // TODO teamSize | default value and override command
    private boolean allowLargerTeams; // TODO allowLargerTeams | default value and override command
    private boolean allowOddNum; // TODO allowOddNum | default value and override command
    private int timeoutTime; // TODO timeoutTime | default value and override command
    private int subRequestTime; // TODO subRequestTime | default value and override command
    private int pregameTime; // TODO pregameTime | default value and override command
    private boolean resetTimeoutOnJoin; // TODO resetTimeoutOnJoin | default value and override command
    private boolean ifEnoughPlayersAutoStart; // TODO ifEnoughPlayersAutoStart | default value and override command
    private boolean allowJoinViaDiscord; // TODO allowJoinViaDiscord | default value and override command

    protected QueueData(int id, @NotNull String startTime) {
        this.id = id;
        this.startTime = startTime;
    }

    protected QueueData(@NotNull JsonObject data) {
        id = ParseData.getInt(data, "id", -1);
        startTime = ParseData.getString(data, "startTime", "");
        minPlayers = ParseData.getInt(data, "minPlayers", 2);
        teamSize = ParseData.getInt(data, "teamSize", 2);
        allowLargerTeams = ParseData.getBoolean(data, "allowLargerTeams", false);
        allowOddNum = ParseData.getBoolean(data, "allowOddNum", false);
        timeoutTime = ParseData.getInt(data, "timeoutTime", 2);
        subRequestTime = ParseData.getInt(data, "subRequestTime", 2);
        pregameTime = ParseData.getInt(data, "pregameTime", 2);
        resetTimeoutOnJoin = ParseData.getBoolean(data, "resetTimeoutOnJoin", true);
        ifEnoughPlayersAutoStart = ParseData.getBoolean(data, "ifEnoughPlayersAutoStart", true);
        allowJoinViaDiscord = ParseData.getBoolean(data, "allowJoinViaDiscord", true);
        resolved = ParseData.getBoolean(data, "resolved", false);
        recentJoinTime = ParseData.getString(data, "recentJoinTime", "");
        pregameStartTime = ParseData.getString(data, "pregameStartTime", "");
        messageId = ParseData.getLong(data, "messageId", messageId);
        readMembers(data);
    }

    private void updateQueueState() {
        if (resolved) {
            queueState = QueueState.CLOSED;
            return;
        }
        if (queueState == QueueState.FINAL_PREGAME_TICK) {
            queueState = QueueState.CLOSED;
            isDirty = true;
            return;
        }
        if (isPreGame()) {
            if (UtilCalendar.isAfterSeconds(getPregameStartTime(), pregameTime)) {
                queueState = QueueState.FINAL_PREGAME_TICK;
                isDirty = true;
                return;
            } else if (UtilCalendar.isAfterSeconds(getPregameStartTime(), subRequestTime)) {
                queueState = QueueState.PREGAME_SUBS;
                isDirty = true;
                return;
            } else {
                queueState = QueueState.PREGAME;
                return;
            }
        }
        if (queueState == QueueState.FINAL_ENROLL_TICK) {
            queueState = QueueState.PREGAME;
            isDirty = true;
            return;
        }
        if (members.isEmpty() || !isResetTimeoutOnJoin()) {
            if (UtilCalendar.isAfterSeconds(getStartTime(), timeoutTime)) {
                queueState = QueueState.FINAL_ENROLL_TICK;
                isDirty = true;
                return;
            } else {
                queueState = QueueState.ENROLL;
                return;
            }
        }
        if (UtilCalendar.isAfterSeconds(recentJoinTime, timeoutTime)) {
            queueState = QueueState.FINAL_ENROLL_TICK;
            isDirty = true;
        } else {
            queueState = QueueState.ENROLL;
        }
    }

    public void update(Guild guild, LeagueData league, Consumer<String> debug) {
        updateQueueState();
		if (isClosed()) return;
		if (getQueueState() == QueueState.FINAL_ENROLL_TICK) {
            if (members.size() >= minPlayers && isEnoughPlayersAutoStart()) {
                startPreGame(debug);
            }
		}
        if (!isDirty) return;
		List<QueueMember> sorted = new ArrayList<>(members.values());
        sortQueueMembers(sorted);
		List<QueueMember> filteredQueueMembers = new ArrayList<>(sorted);
        filterQueueMembers(filteredQueueMembers);
        if (getQueueState() == QueueState.FINAL_PREGAME_TICK) {
            if (filteredQueueMembers.size() >= minPlayers && isEnoughPlayersAutoStart()) {
                createSet(guild, league, debug);
            }
        }
        String queueState, nextQueueState;
        if (getQueueState() == QueueState.ENROLL || getQueueState() == QueueState.FINAL_ENROLL_TICK) {
            queueState = "ENROLL";
            nextQueueState = "PRE-GAME";
        } else if (getQueueState() == QueueState.PREGAME) {
            queueState = "PRE-GAME";
            nextQueueState = "PRE-GAME SUBS";
        } else if (getQueueState() == QueueState.PREGAME_SUBS || getQueueState() == QueueState.FINAL_PREGAME_TICK) {
            queueState = "PRE-GAME SUBS";
            nextQueueState = "START";
        } else if (getQueueState() == QueueState.CLOSED) {
            queueState = nextQueueState = "CLOSED";
        } else {
            queueState = nextQueueState = "NONE";
        }
        String nextStateTime = switch (getQueueState()) {
            case ENROLL, FINAL_ENROLL_TICK -> members.isEmpty() || !isResetTimeoutOnJoin()
                    ? UtilCalendar.addSeconds(getStartTime(), timeoutTime)
                    : UtilCalendar.addSeconds(recentJoinTime, timeoutTime);
            case PREGAME -> UtilCalendar.addSeconds(getPregameStartTime(), subRequestTime);
            case PREGAME_SUBS, FINAL_PREGAME_TICK -> UtilCalendar.addSeconds(getPregameStartTime(), pregameTime);
            case CLOSED, NONE -> "";
        };
        nextStateTime = UtilCalendar.toDiscordTime(nextStateTime)+" "+UtilCalendar.toDiscordRelativeTime(nextStateTime);
        MessageCreateBuilder mcb = new MessageCreateBuilder();
        mcb.addContent("__**ID:"+getId()+" | "+queueState+" -> "+nextQueueState+" "+nextStateTime+"**__");
		int maxPlayers = allowLargerTeams ? 200 : teamSize * 2;
		Set<QueueStatus> displayedStatuses = new HashSet<>();
		for (int i = 0; i < sorted.size(); ++i) {
			QueueMember member = sorted.get(i);
			String time;
            String name = getName(member, guild);
			if (isPreGame()) {
                if (member.isCheckedIn()) {
                    if (i < maxPlayers) {
                        member.setQueueStatus(QueueStatus.CHECKED_IN);
                    } else {
                        member.setQueueStatus(QueueStatus.CHECKED_IN_SUB);
                    }
                    if (!isAllowOddNum() && (i == filteredQueueMembers.size()-1) && i % 2 != 0) {
                        member.setQueueStatus(QueueStatus.CHECKED_IN_SUB);
                    }
                    time = member.getCheckInTime();
                } else {
                    if (i < maxPlayers) {
                        member.setQueueStatus(QueueStatus.NOT_CHECKED_IN);
                    } else {
                        member.setQueueStatus(QueueStatus.NOT_CHECKED_IN_SUB);
                    }
                    time = member.getJoinTime();
                    name = "<@"+member.getId()+"> ";
                }
			} else {
                if (i < maxPlayers) {
                    member.setQueueStatus(QueueStatus.PRIORITY);
                } else {
                    member.setQueueStatus(QueueStatus.OVERFLOW);
                }
                if (!isAllowOddNum() && (i == sorted.size()-1) && i % 2 != 0) {
                    member.setQueueStatus(QueueStatus.OVERFLOW);
                }
                time = member.getJoinTime();
			}
			QueueStatus status = member.getQueueStatus();
			if (!displayedStatuses.contains(status)) {
				mcb.addContent(member.getQueueStatusEmoji()+" = **"+status.name()+"**");
				displayedStatuses.add(status);
			}
            time = UtilCalendar.toDiscordTime(time);
			mcb.addContent(member.getQueueStatusEmoji()+" "+name+" "+time);
		}
		TextChannel queueChannel = guild.getChannelById(TextChannel.class, league.getChannelId("queues"));
        if (queueChannel == null) {
            debug.accept(Important.getError()+" Queue **"+id+"** Can't display!"
                    +" __The queues channel is gone!__");
            return;
        }
		displayQueue(queueChannel, mcb.build());
        isDirty = false;
    }

	private String getName(QueueMember member, Guild guild) {
		Member m = guild.getMemberById(member.getId());
		if (m != null) return m.getEffectiveName();
    	else return member.getId()+"";
	}

    public void resetTimeOut(Consumer<String> debug) {
        if (resolved) {
            debug.accept("Cannot reset timeout for queue "+getId()+" because a set has already been generated.");
            return;
        }
        startTime = UtilCalendar.getCurrentDateTimeString();
        pregameStartTime = "";
        queueState = QueueState.ENROLL;
        isDirty = true;
        debug.accept("Queue "+getId()+" has been reset to the start of the Enroll Phase.");
    }

    public void startPreGame(Consumer<String> debug) {
        if (isClosed()) {
            debug.accept("Cannot start the pregame of queue "+getId()+" because it is closed.");
            return;
        }
        pregameStartTime = UtilCalendar.getCurrentDateTimeString();
        queueState = QueueState.PREGAME;
        isDirty = true;
        debug.accept("Pre-Game for Queue "+getId()+" has started! All players must check in to their queue!");
    }

    private void displayQueue(TextChannel channel, MessageCreateData mcd) {
		if (messageId == -1) messageId = channel.sendMessage(mcd).complete().getIdLong();
		else {
			MessageEditData med = new MessageEditBuilder().applyCreateData(mcd).build();
			try {
				channel.editMessageById(messageId, med).complete();
			} catch (ErrorResponseException e) {
				switch (e.getErrorResponse()) {
				case UNKNOWN_MESSAGE:
					messageId = channel.sendMessage(mcd).complete().getIdLong();
					return;
				default:
					return;
				}
			} 
		}
    }

    public void createSet(Guild guild, LeagueData league, Consumer<String> debug) {
        if (isClosed()) {
            debug.accept(Important.getError()+" Queue "+id+" cannot make pairs because it is Closed!");
            return;
        }
        TextChannel pairsChannel = guild.getChannelById(TextChannel.class, league.getChannelId("pairings"));
        if (pairsChannel == null) {
            debug.accept(Important.getError()+" Queue **"+id+"** Can't generate pairings!"
                    +" __The pairings channel is gone!__");
            return;
        }
        int numPlayers = members.size();
        if (numPlayers < getMinPlayers()) {
            debug.accept(Important.getError()+" Queue **"+id+"** Can't generate pairings!"
                    +" __Not enough players joined the queue!__" +
                    " **Need "+(getMinPlayers() - numPlayers)+" More!**");
            return;
        }
        List<Contestant> contestants = getFilteredContestants(league);
        if (contestants.size() < getMinPlayers()) {
            debug.accept(Important.getError()+" Queue **"+id+"** Can't generate a pair!"
                    +" __Not enough players are checked in!__" +
                    " **Need "+(getMinPlayers() - contestants.size())+" More!**");
            return;
        }

        UtilUsers.Result result = UtilUsers.balanceTeams(contestants, preferredTeams.values());
        TeamData team1 = UtilUsers.getCreateTeam(guild, league, result.team1());
        TeamData team2 = UtilUsers.getCreateTeam(guild, league, result.team2());
        SetData set = league.createTeamSet(team1.getName(), team2.getName());
        if (set == null) {
            debug.accept(Important.getError()+" Queue **"+id+"** Failed to gen pairings!"
                            +" __Attempted to make "+team1.getName()+" and "+team2.getName()+" fight each other!__");
            return;
        }
        set.displaySet(pairsChannel);
        debug.accept("Successfully Created Set "+set.getId()+" for Queue "+getId());
        resolved = true;
        isDirty = true;
        queueState = QueueState.CLOSED;
        GlobalData.saveData();
    }

    protected void filterContestants(List<Contestant> contestants) {
        contestants.removeIf(contestant -> {
            QueueMember member = getQueueMemberData(contestant.getUserId());
            return member == null || !member.isCheckedIn();
        });
        if (!isAllowLargerTeams()) {
            while (contestants.size() > getTeamSize() * 2) {
                contestants.remove(contestants.size()-1);
            }
        }
        if (!isAllowOddNum() && contestants.size() % 2 != 0) {
            contestants.remove(contestants.size()-1);
        }
    }

    public void filterQueueMembers(List<QueueMember> queueMembers) {
        queueMembers.removeIf(member -> !member.isCheckedIn());
        if (!isAllowLargerTeams()) {
            while (queueMembers.size() > getTeamSize() * 2) {
                queueMembers.remove(queueMembers.size()-1);
            }
        }
        if (!isAllowOddNum() && queueMembers.size() % 2 != 0) {
            queueMembers.remove(queueMembers.size()-1);
        }
    }

    public List<Contestant> getSortedMembers(LeagueData league) {
        List<QueueMember> queueMembers = new ArrayList<>(members.values());
        sortQueueMembers(queueMembers);
        List<Contestant> contestants = new ArrayList<>();
        for (QueueMember member : queueMembers)
            contestants.add(league.getContestantById(member.getId()));
        return contestants;
    }

    public List<Contestant> getFilteredContestants(LeagueData league) {
        List<Contestant> contestants = getSortedMembers(league);
        filterContestants(contestants);
        return contestants;
    }

    public static void sortQueueMembers(List<QueueMember> qm) {
        for (int i = 0; i < qm.size(); ++i) {
            int maxIndex = i;
            for (int j = i+1; j < qm.size(); ++j) {
                QueueMember test = qm.get(j);
                QueueMember max = qm.get(maxIndex);
                boolean testChkdIn = test.isCheckedIn();
                boolean maxChkdIn = max.isCheckedIn();
                if (testChkdIn && !maxChkdIn
                        || testChkdIn && UtilCalendar.isOlderTime(test.getCheckInTime(), max.getCheckInTime())
                        || !testChkdIn && !maxChkdIn && UtilCalendar.isOlderTime(test.getJoinTime(), max.getJoinTime())
                ) {
                    maxIndex = j;
                }
            }
            QueueMember temp = qm.get(maxIndex);
            qm.set(maxIndex, qm.get(i));
            qm.set(i, temp);
        }
    }

    public QueueResult addIndividual(@NotNull UserData user) {
        if (cantJoin())
            return QueueResult.CLOSED;
        long id = user.getId();
        if (members.containsKey(id))
            return QueueResult.ALREADY_JOINED;
        addMember(user);
        return QueueResult.SUCCESS;
    }

    public QueueResult addPreferredTeam(@NotNull String name, @NotNull UserData... users) {
        if (users.length > getTeamSize())
            return QueueResult.WRONG_TEAM_SIZE;
        if (cantJoin())
            return QueueResult.CLOSED;
        Set<Long> teamMembers = new HashSet<>();
        boolean teamAlreadyExisted = false;
        for (UserData user : users) {
            long id = user.getId();
            if (findClearTeam(id))
                teamAlreadyExisted = true;
            teamMembers.add(id);
            addMember(user);
        }
        preferredTeams.put(name, teamMembers);
        if (teamAlreadyExisted)
            return QueueResult.CHANGED_TEAM;
        return QueueResult.SUCCESS;
    }

    private void addMember(UserData user) {
        String currentTime = UtilCalendar.getCurrentDateTimeString();
        members.put(user.getId(), new QueueMember(user.getId(), currentTime));
        recentJoinTime = currentTime;
        isDirty = true;
    }

    private boolean findClearTeam(Long id) {
        AtomicBoolean found = new AtomicBoolean(false);
        preferredTeams.entrySet().removeIf(entry -> {
            boolean b = entry.getValue().contains(id);
            if (b) found.set(true);
            return b;
        });
        return found.get();
    }

    public boolean removeFromQueue(long id) {
        QueueMember member = members.remove(id);
        findClearTeam(id);
        isDirty = true;
        return member != null;
    }

    public QueueResult checkIn(long id) {
        if (isClosed()) return QueueResult.CLOSED;
        if (!isPreGame()) return QueueResult.NOT_PRE_GAME;
        QueueMember member = getQueueMemberData(id);
        if (member == null) return QueueResult.NOT_IN_QUEUE;
        member.setCheckedIn(true);
        isDirty = true;
        return QueueResult.SUCCESS;
    }

    public QueueResult checkOut(long id) {
        if (isClosed()) return QueueResult.CLOSED;
        if (!isPreGame()) return QueueResult.NOT_PRE_GAME;
        QueueMember member = getQueueMemberData(id);
        if (member == null) return QueueResult.NOT_IN_QUEUE;
        member.setCheckedIn(false);
        isDirty = true;
        return QueueResult.SUCCESS;
    }

    @Override
    public JsonObject getJson() {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("startTime", startTime);
        data.addProperty("minPlayers", minPlayers);
        data.addProperty("teamSize", teamSize);
        data.addProperty("allowLargerTeams", allowLargerTeams);
        data.addProperty("allowOddNum", allowOddNum);
        data.addProperty("timeoutTime", timeoutTime);
        data.addProperty("subRequestTime", subRequestTime);
        data.addProperty("pregameTime", pregameTime);
        data.addProperty("resetTimeoutOnJoin", resetTimeoutOnJoin);
        data.addProperty("ifEnoughPlayersAutoStart", ifEnoughPlayersAutoStart);
        data.addProperty("allowJoinViaDiscord", allowJoinViaDiscord);
        data.addProperty("resolved", resolved);
        data.addProperty("recentJoinTime", recentJoinTime);
        data.addProperty("pregameStartTime", pregameStartTime);
        data.addProperty("messageId", messageId);
        data.addProperty("queueState", getQueueState().name());
        JsonArray members = new JsonArray();
        this.members.forEach((id, qm) -> members.add(qm.getData()));
        data.add("members", members);
        JsonArray teams = new JsonArray();
        preferredTeams.forEach((name, ids) -> {
            JsonObject team = new JsonObject();
            team.addProperty("name", name);
            addLongSet(team, "members", ids);
            teams.add(team);
        });
        data.add("preferredTeams", teams);
        return data;
    }

    private void readMembers(JsonObject data) {
        this.members.clear();
        this.preferredTeams.clear();
        JsonArray members = ParseData.getJsonArray(data, "members");
        for (int i = 0; i < members.size(); ++i) {
            QueueMember qm = new QueueMember(members.get(i).getAsJsonObject());
            this.members.put(qm.getId(), qm);
        }
        JsonArray teams = ParseData.getJsonArray(data, "preferredTeams");
        for (int i = 0; i < teams.size(); ++i) {
            JsonObject team = teams.get(i).getAsJsonObject();
            String name = team.get("name").getAsString();
            JsonArray mja = ParseData.getJsonArray(data, "members");
            Set<Long> ml = new HashSet<>();
            for (int j = 0; j < mja.size(); ++j)
                ml.add(mja.get(i).getAsLong());
            this.preferredTeams.put(name, ml);
        }
    }

    public static void addLongSet(JsonObject data, String name, Set<Long> numbers) {
        JsonArray array = new JsonArray();
        for (Long num : numbers) array.add(num);
        data.add(name, array);
    }

    @Override
    public JsonObject getBackupJson() {
        return getJson();
    }

    @Override
    public void readBackup(JsonObject data) {
        readMembers(data);
    }

    public int getId() {
        return id;
    }

    @NotNull
    public String getStartTime() {
        return startTime;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        if (teamSize < 1) teamSize = 1;
        if (teamSize != this.teamSize) preferredTeams.clear();
        this.teamSize = teamSize;
        isDirty = true;
    }

    public boolean isClosed() {
        return getQueueState() == QueueState.CLOSED;
    }

    public boolean cantJoin() {
        return isClosed() || getQueueState() == QueueState.FINAL_PREGAME_TICK;
    }

    public QueueState getQueueState() {
        return queueState;
    }

    @Nullable
    public QueueMember getQueueMemberData(long id) {
        return members.get(id);
    }

    public static class QueueMember {
        private final long id;
        private final String joinTime;
        private String checkInTime = "";
		private QueueStatus queueStatus = QueueStatus.NONE;
        public QueueMember(long id, String joinTime) {
            this.id = id;
            this.joinTime = joinTime;
        }
        public QueueMember(JsonObject data) {
            this.id = ParseData.getLong(data, "id", 0);
            this.joinTime = ParseData.getString(data, "joinTime", "");
            this.checkInTime = ParseData.getString(data, "checkInTime", "");
        }
        public JsonObject getData() {
            JsonObject data = new JsonObject();
            data.addProperty("id", id);
            data.addProperty("joinTime", joinTime);
            data.addProperty("checkInTime", checkInTime);
			data.addProperty("queueStatus", queueStatus.name());
            return data;
        }
        public long getId() {
            return id;
        }
        public String getJoinTime() {
            return joinTime;
        }
        public boolean isCheckedIn() {
            return !checkInTime.isEmpty();
        }
        public void setCheckedIn(boolean checkedIn) {
            if (checkedIn) checkInTime = UtilCalendar.getCurrentDateTimeString();
            else checkInTime = "";
        }
        public String getCheckInTime() {
            return checkInTime;
        }
		@Override
		public boolean equals(Object other) {
			if (other instanceof QueueMember qm) return this.id == qm.id;
			return false;
		}
		public QueueStatus getQueueStatus() {
			return queueStatus;
		}
		protected void setQueueStatus(QueueStatus state) {
			queueStatus = state;
		}
		public String getQueueStatusEmoji() {
            return switch (queueStatus) {
                case CHECKED_IN -> ":white_check_mark:";
                case CHECKED_IN_SUB -> ":ballot_box_with_check:";
                case NOT_CHECKED_IN -> ":negative_squared_cross_mark:";
                case NOT_CHECKED_IN_SUB -> ":x:";
                case PRIORITY -> ":arrow_up:";
                case OVERFLOW -> ":arrow_down:";
                case NONE -> ":question:";
            };
		}
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
        isDirty = true;
    }

    public boolean isAllowLargerTeams() {
        return allowLargerTeams;
    }

    public void setAllowLargerTeams(boolean allowLargerTeams) {
        this.allowLargerTeams = allowLargerTeams;
        isDirty = true;
    }

    public boolean isAllowOddNum() {
        return allowOddNum;
    }

    public void setAllowOddNum(boolean allowOddNum) {
        this.allowOddNum = allowOddNum;
        isDirty = true;
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    public void setTimeoutTime(int timeoutTime) {
        this.timeoutTime = timeoutTime;
        isDirty = true;
    }

    public int getSubRequestTime() {
        return subRequestTime;
    }

    public void setSubRequestTime(int subRequestTime) {
        this.subRequestTime = subRequestTime;
        isDirty = true;
    }

    public int getPregameTime() {
        return pregameTime;
    }

    public void setPregameTime(int pregameTime) {
        this.pregameTime = pregameTime;
        isDirty = true;
    }

    public boolean isResetTimeoutOnJoin() {
        return resetTimeoutOnJoin;
    }

    public void setResetTimeoutOnJoin(boolean resetTimeoutOnJoin) {
        this.resetTimeoutOnJoin = resetTimeoutOnJoin;
        isDirty = true;
    }

    public boolean isEnoughPlayersAutoStart() {
        return ifEnoughPlayersAutoStart;
    }

    public void setEnoughPlayersAutoStart(boolean ifEnoughPlayersAutoStart) {
        this.ifEnoughPlayersAutoStart = ifEnoughPlayersAutoStart;
        isDirty = true;
    }

    public boolean isAllowJoinViaDiscord() {
        return allowJoinViaDiscord;
    }

    public void setAllowJoinViaDiscord(boolean allowJoinViaDiscord) {
        this.allowJoinViaDiscord = allowJoinViaDiscord;
        isDirty = true;
    }

    public String getPregameStartTime() {
        return pregameStartTime;
    }

	public boolean isPreGame() {
		return !getPregameStartTime().isEmpty();
	}
}
