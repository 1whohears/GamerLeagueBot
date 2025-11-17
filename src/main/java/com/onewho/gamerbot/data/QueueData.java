package com.onewho.gamerbot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueueData implements Storable {

    private final int id;
    @NotNull private final String startTime;
    @NotNull private String closeTime = "";
    private int teamSize = 2;
    private final Set<Long> members = new HashSet<>();
    private final Map<String, Set<Long>> preferredTeams = new HashMap<>();

    protected QueueData(int id, @NotNull String startTime) {
        this.id = id;
        this.startTime = startTime;
    }

    protected QueueData(@NotNull JsonObject data) {
        id = ParseData.getInt(data, "id", -1);
        startTime = ParseData.getString(data, "startTime", "");
        closeTime = ParseData.getString(data, "closeTime", "");
        teamSize = ParseData.getInt(data, "teamSize", teamSize);
        readMembers(data);
    }

    public void genPairs(Guild guild, LeagueData league, MessageChannelUnion debugChannel) {
        debugChannel.sendMessage("Generating Pairs for Queue "+id+" ").queue();
        TextChannel pairsChannel = guild.getChannelById(TextChannel.class, league.getChannelId("pairings"));
        if (pairsChannel == null) {
            debugChannel.sendMessage(Important.getError()+" Queue **"+id+"** Can't generate pairings!"
                    +" __The pairings channel is gone!__").queue();
            return;
        }
        int numPlayers = members.size();
        if (numPlayers < teamSize * 2) {
            debugChannel.sendMessage(Important.getError()+" Queue **"+id+"** Can't generate pairings!"
                    +" __Not enough players joined the queue!__").queue();
            return;
        }
        List<Contestant> contestants = new ArrayList<>();
        for (Long id : members) contestants.add(league.getContestantById(id));
        preferredTeams.forEach((name, ids) -> {
            UserData[] teamMembers = new UserData[ids.size()];
            int k = 0;
            for (Long id : ids) {
                teamMembers[k++] = league.getUserDataById(id);
                contestants.removeIf(cnt -> cnt.isIndividual() && cnt.getUserId() == id);
            }
            TeamData team = UtilUsers.getCreateTeam(name, league, teamMembers);
            contestants.add(team);
        });
        LeagueData.sortByScoreDescend(contestants);

        Integer[] groupSizes = getGroupSizes(teamSize, numPlayers);
        int k = 0;
        List<UserData> groupMembers = null;
        // TODO temporary logic until one that supports preferred teams. assumes all contestants are individual!
        for (Contestant contestant : contestants) {
            if (groupMembers == null) groupMembers = new ArrayList<>();
            groupMembers.add((UserData) contestant);
            if (groupMembers.size() == groupSizes[k]) {
                ++k;
                UtilUsers.Result result = UtilUsers.balanceTeams(groupMembers.toArray(new UserData[0]));
                TeamData team1 = UtilUsers.getCreateTeam(guild, league, result.team1());
                TeamData team2 = UtilUsers.getCreateTeam(guild, league, result.team2());
                SetData set = league.createTeamSet(team1.getName(), team2.getName());
                if (set == null) {
                    debugChannel.sendMessage(Important.getError()+" Queue **"+id+"** Failed to gen pairings!"
                            +" __Attempted to make "+team1.getName()+" and "+team2.getName()+" fight each other!__")
                            .queue();
                    return;
                }
                debugChannel.sendMessage("Successfully created set "+set.getId()).queue();
                set.displaySet(pairsChannel);
                GlobalData.saveData();
            }
        }
        /*for (Contestant contestant : contestants) {
            if (team1 == null) {
                if (contestant.isTeam()) {
                    team1 = contestant;
                    // FIXME check if team1.size() < teamSizes[k] and add a random player
                    k++;
                    continue;
                } else {
                    if (team2Members == null) team2Members = new Contestant[teamSizes[k]];

                }
            } else {

            }
        }*/
        setCloseTime(UtilCalendar.getCurrentDateTimeString());
    }

    public static Integer[] getGroupSizes(int teamSize, int queueSize) {
        List<Integer> sizes = new ArrayList<>();
        int remaining = queueSize;
        while (remaining > 0) {
            if (remaining < teamSize * 4) {
                sizes.add(remaining);
                break;
            }
            sizes.add(teamSize * 2);
            remaining -= teamSize * 2;
        }
        return sizes.toArray(new Integer[0]);
    }

    public QueueResult addIndividual(@NotNull UserData user) {
        if (isAutoPairAtClose() && UtilCalendar.isNewer(UtilCalendar.getCurrentDateTimeString(), getCloseTime()))
            return QueueResult.CLOSED;
        long id = user.getId();
        if (members.contains(id)) return QueueResult.ALREADY_JOINED;
        members.add(id);
        return QueueResult.SUCCESS;
    }

    public QueueResult addPreferredTeam(@NotNull String name, @NotNull UserData... users) {
        if (users.length != getTeamSize())
            return QueueResult.WRONG_TEAM_SIZE;
        if (isAutoPairAtClose() && UtilCalendar.isNewer(UtilCalendar.getCurrentDateTimeString(), getCloseTime()))
            return QueueResult.CLOSED;
        Set<Long> teamMembers = new HashSet<>();
        boolean teamAlreadyExisted = false;
        for (UserData user : users) {
            long id = user.getId();
            if (findClearTeam(id))
                teamAlreadyExisted = true;
            teamMembers.add(id);
            members.add(id);
        }
        preferredTeams.put(name, teamMembers);
        if (teamAlreadyExisted)
            return QueueResult.CHANGED_TEAM;
        return QueueResult.SUCCESS;
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
        boolean removed = members.remove(id);
        findClearTeam(id);
        return removed;
    }

    @Override
    public JsonObject getJson() {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("startTime", startTime);
        data.addProperty("closeTime", closeTime);
        data.addProperty("teamSize", teamSize);
        addLongSet(data, "members", members);
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
        for (int i = 0; i < members.size(); ++i)
            this.members.add(members.get(i).getAsLong());
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
        closeTime = ParseData.getString(data, "closeTime", "");
        teamSize = ParseData.getInt(data, "teamSize", teamSize);
        readMembers(data);
    }

    public int getId() {
        return id;
    }

    @NotNull
    public String getStartTime() {
        return startTime;
    }

    @NotNull
    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(@NotNull String closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isAutoPairAtClose() {
        return !closeTime.isEmpty();
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        if (teamSize != this.teamSize) preferredTeams.clear();
        this.teamSize = teamSize;
    }
}
