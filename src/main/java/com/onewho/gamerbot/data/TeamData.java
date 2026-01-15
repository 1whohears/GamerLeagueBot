package com.onewho.gamerbot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilUsers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TeamData implements Contestant {

    private final long id;
    private final String name;
    private final Map<Long, UserData> members = new HashMap<>();

    protected TeamData(LeagueData league, JsonObject data) {
        this.id = ParseData.getLong(data, "id", -1);
        this.name = ParseData.getString(data, "name", "bruh");
        JsonArray idsArray = ParseData.getJsonArray(data, "members");
        for (int i = 0; i < idsArray.size(); ++i) {
            long id = idsArray.get(i).getAsLong();
            UserData user = league.getUserDataById(id);
            if (user == null) continue;
            this.members.put(user.getId(), user);
        }
    }

    protected TeamData(String name, UserData... members) {
        this.id = System.currentTimeMillis();
        this.name = name;
        for (UserData user : members) this.members.put(user.getId(), user);
    }

    public JsonObject getJson() {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("name", name);
        JsonArray idsArray = new JsonArray();
        members.forEach((id, user) -> idsArray.add(id));
        data.add("members", idsArray);
        data.addProperty("type", getType().name());
        return data;
    }

    public JsonObject getBackupJson() {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        return data;
    }

    public void readBackup(JsonObject data) {
        if (data.get("id") == null || data.get("id").getAsLong() != id) return;
    }

    @Override
    public boolean isIndividual() {
        return false;
    }

    @Override
    public boolean isTeam() {
        return true;
    }

    @Override
    public int getTeamSize() {
        return members.size();
    }

    @Override
    public long getUserId() {
        return members.keySet().stream().findFirst().orElse(-1L);
    }

    @Override
    public Collection<Long> getUserIds() {
        return members.keySet();
    }

    public Collection<UserData> getUsers() {
        return members.values();
    }

    @Override
    public void setLastActive(String date) {
        members.forEach((id, user) -> user.setLastActive(date));
    }

    @Override
    public String getTeamName() {
        return getName();
    }

    @Override
    public boolean hasUserId(long id) {
        return members.containsKey(id);
    }

    @Override
    public Type getType() {
        return Type.TEAM;
    }

    @Override
    public int getScore() {
        return UtilUsers.getAverageScore(this.members.values());
    }

    @Override
    public void changeScore(int change) {
        members.forEach((id, user) -> user.changeScore(change));
    }

    @Override
    public void setScore(int score) {
        members.forEach((id, user) -> user.setScore(score));
    }

    @Override
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasOverlappingMembers(TeamData other) {
        for (Long id : members.keySet())
            if (other.members.containsKey(id))
                return true;
        return false;
    }

    @Override
    public String getNamePrefix() {
        return "__"+getName()+"__";
    }

    public boolean hasSameMembers(UserData... members) {
        if (members.length != this.members.size()) return false;
        for (UserData user : members)
            if (!this.members.containsKey(user.getId()))
                return false;
        return true;
    }
}
