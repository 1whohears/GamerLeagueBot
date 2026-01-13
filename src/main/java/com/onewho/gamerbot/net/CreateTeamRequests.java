package com.onewho.gamerbot.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.command.CreateRandomTeamSet;
import com.onewho.gamerbot.command.CreateTeamSet;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.data.TeamData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilUsers;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.get;

public class CreateTeamRequests {

    public static void init() {
        // create random team set using a list of minecraft player uuids
        get("/league/createset/autoteams", (LeagueDataRoute) (req, res, guild, league) -> {
            String team1Name = req.queryParams("team1Name");
            if (team1Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "team1Name not defined"));
            }

            String team2Name = req.queryParams("team2Name");
            if (team2Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "team2Name not defined"));
            }

            String mcUUIDListStr = req.queryParams("mcUUIDList");
            if (mcUUIDListStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "mcUUIDList not defined"));
            }

            String[] mcUUIDList = parseUUIDList(mcUUIDListStr);
            if (mcUUIDList.length < 2) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Not enough players"));
            }

            UserData[] users = new UserData[mcUUIDList.length];
            for (int i = 0; i < mcUUIDList.length; ++i) {
                UserData user = league.getUserByExtraData("mcUUID", mcUUIDList[i]);
                if (user == null) {
                    res.status(400);
                    return getGson().toJson(Map.of(
                            "error", "One of the players does not have their discord linked!",
                            "badUUID", mcUUIDList[i]
                    ));
                }
                users[i] = user;
            }

            AtomicReference<String> msg = new AtomicReference<>();
            SetData set = CreateRandomTeamSet.run(league, guild, msg::set, team1Name, team2Name, users);
            if (set == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", msg.get()));
            }

            return getTeamSetResult(set, set.getContestant1().getTeamName(),
                    set.getContestant2().getTeamName(), msg.get(), league);
        });

        // create a set using the teams already in game
        get("/league/createset/ingameteams", (LeagueDataRoute) (req, res, guild, league) -> {
            String team1Name = req.queryParams("team1Name");
            if (team1Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "team1Name not defined"));
            }

            String team2Name = req.queryParams("team2Name");
            if (team2Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "team2Name not defined"));
            }

            String team1MCUUIDListStr = req.queryParams("team1MCUUIDList");
            if (team1MCUUIDListStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "team1MCUUIDList not defined"));
            }

            String team2MCUUIDListStr = req.queryParams("team2MCUUIDList");
            if (team2MCUUIDListStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "team2MCUUIDList not defined"));
            }

            String[] team1MCUUIDList = parseUUIDList(team1MCUUIDListStr);
            if (team1MCUUIDList.length < 1) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Not enough players in team 1"));
            }

            String[] team2MCUUIDList = parseUUIDList(team2MCUUIDListStr);
            if (team2MCUUIDList.length < 1) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Not enough players in team 2"));
            }

            UserData[] team1Users = new UserData[team1MCUUIDList.length];
            for (int i = 0; i < team1MCUUIDList.length; ++i) {
                UserData user = league.getUserByExtraData("mcUUID", team1MCUUIDList[i]);
                if (user == null) {
                    res.status(400);
                    return getGson().toJson(Map.of(
                            "error", "One of the players does not have their discord linked!",
                            "badUUID", team1MCUUIDList[i]
                    ));
                }
                team1Users[i] = user;
            }

            UserData[] team2Users = new UserData[team2MCUUIDList.length];
            for (int i = 0; i < team2MCUUIDList.length; ++i) {
                UserData user = league.getUserByExtraData("mcUUID", team2MCUUIDList[i]);
                if (user == null) {
                    res.status(400);
                    return getGson().toJson(Map.of(
                            "error", "One of the players does not have their discord linked!",
                            "badUUID", team2MCUUIDList[i]
                    ));
                }
                team2Users[i] = user;
            }

            TeamData team1 = UtilUsers.getCreateTeam(team1Name, league, team1Users);
            TeamData team2 = UtilUsers.getCreateTeam(team2Name, league, team2Users);

            AtomicReference<String> msg = new AtomicReference<>();
            SetData set = CreateTeamSet.run(team1, team2, guild, league, msg::set);
            if (set == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", msg.get()));
            }

            return getTeamSetResult(set, team1.getName(), team2.getName(), msg.get(), league);
        });
    }

    public static String getTeamSetResult(SetData set, String team1Name, String team2Name,
                                           String msg, LeagueData league) {
        JsonObject json = new JsonObject();
        json.addProperty("result", msg);
        json.addProperty("set_id", set.getId());
        json.add("team1", encodeTeamData(league, team1Name));
        json.add("team2", encodeTeamData(league, team2Name));
        return getGson().toJson(json);
    }

    public static JsonObject encodeTeamData(LeagueData league, String teamName) {
        JsonObject json = new JsonObject();
        json.addProperty("name", teamName);
        TeamData team = league.getTeamByName(teamName);
        if (team == null) return json;
        JsonArray members = new JsonArray();
        for (UserData user : team.getUsers()) {
            JsonObject j = new JsonObject();
            j.addProperty("id", user.getId());
            j.addProperty("mcUUID", user.getExtraData().get("mcUUID").getAsString());
            members.add(j);
        }
        json.add("members", members);
        return json;
    }

    public static String[] parseUUIDList(String uuidListStr) {
        return uuidListStr.split(",");
    }

}
