package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

import java.util.Collection;

public interface Contestant extends Storable {
    enum Type {
        INDIVIDUAL,
        TEAM
    }
    Type getType();
    int getScore();
    void changeScore(int change);
    void setScore(int score);
    long getId();
    boolean isIndividual();
    boolean isTeam();
    boolean hasUserId(long id);
    Collection<Long> getUserIds();
    void setLastActive(String date);
    String getTeamName();

    default long getUserId() {
        return getId();
    }

    default String getNamePrefix() {
        return "";
    }

    static Contestant read(LeagueData league, JsonObject json) {
        String type = ParseData.getString(json, "type", Type.INDIVIDUAL.name());
        if (type.equals(Type.TEAM.name())) return new TeamData(league, json);
        else return new UserData(json);
    }
}
