package com.onewho.gamerbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.command.CreateRandomTeamSet;
import com.onewho.gamerbot.command.CreateTeamSet;
import com.onewho.gamerbot.data.*;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.*;

public class ApiManager {

    public static void init() {
        port(8080);

        before((req, res) -> res.type("application/json"));

        get("/ping", (req, res) -> getGson().toJson(Map.of("status", "ok")));

        // chack if a league exists.
        get("/league/ping", (LeagueDataRoute) (req, res, guild, league) -> {
            return getGson().toJson(Map.of("league_exists", true));
        });

        // used to link a minecraft account to a discord league account.
        get("/league/link/minecraft/player", (LeagueDataRoute) (req, res, guild, league) -> {
            String mcUUIDStr = req.queryParams("mcUUID");
            if (mcUUIDStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            String discordUsername = req.queryParams("discordUsername");
            if (discordUsername == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            List<Member> memberList = guild.getMembersByName(discordUsername, false);
            if (memberList.isEmpty()) {
                res.status(400);
                return getGson().toJson(Map.of("error", "There is no user with that username"));
            } else if (memberList.size() > 1) {
                res.status(400);
                return getGson().toJson(Map.of("error", "More than one user has that name???"));
            }
            Member member = memberList.get(0);

            UserData userData = league.getUserDataById(member.getIdLong());
            if (userData == null) {
                String error = league.addUser(guild, member.getIdLong(), false);
                if (!error.contains("You have joined the Gamer League!")) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", error));
                }
                userData = league.getUserDataById(member.getIdLong());
                if (userData == null) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", "Somehow failed to create new user data"));
                }
            }

            userData.getExtraData().addProperty("mcUUID", mcUUIDStr);
            GlobalData.saveData();

            return getGson().toJson(Map.of("result", "Successfully linked minecraft account with discord account!"));
        });

        // create random team set using a list of minecraft player uuids
        get("/league/createset/autoteams", (LeagueDataRoute) (req, res, guild, league) -> {
            String team1Name = req.queryParams("team1Name");
            if (team1Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            String team2Name = req.queryParams("team2Name");
            if (team2Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            String mcUUIDListStr = req.queryParams("mcUUIDList");
            if (mcUUIDListStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
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

            return getTeamSetResult(set, team1Name, team2Name, msg.get(), league);
        });

        // create a set using the teams already in game
        get("/league/createset/ingameteams", (LeagueDataRoute) (req, res, guild, league) -> {
            String team1Name = req.queryParams("team1Name");
            if (team1Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            String team2Name = req.queryParams("team2Name");
            if (team2Name == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            String team1MCUUIDListStr = req.queryParams("team1MCUUIDList");
            if (team1MCUUIDListStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
            }

            String team2MCUUIDListStr = req.queryParams("team2MCUUIDList");
            if (team2MCUUIDListStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Missing Parameters"));
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

        notFound((req, res) -> getGson().toJson(Map.of("error", "Not found")));
    }

    private static String getTeamSetResult(SetData set, String team1Name, String team2Name,
                                           String msg, LeagueData league) {
        JsonObject json = new JsonObject();
        json.addProperty("result", msg);
        json.addProperty("set_id", set.getId());
        json.add("team1", encodeTeamData(league, team1Name));
        json.add("team2", encodeTeamData(league, team2Name));
        return getGson().toJson(json);
    }

    private static JsonObject encodeTeamData(LeagueData league, String teamName) {
        JsonObject json = new JsonObject();
        json.addProperty("name", teamName);
        TeamData team = league.getTeamByName(teamName);
        if (team == null) return json;
        JsonArray members = new JsonArray();
        for (UserData user : team.getUsers()) {
            JsonObject j = new JsonObject();
            j.addProperty("id", user.getId());
            j.addProperty("mcUUID", user.getExtraData().get("mcUUID").getAsString());
        }
        json.add("members", members);
        return json;
    }

    private static String[] parseUUIDList(String uuidListStr) {
        return uuidListStr.split(",");
    }

    public interface LeagueDataRoute extends spark.Route {
        default Object handle(Request req, Response res) throws Exception {
            String guildIdStr = req.queryParams("guildId");
            if (guildIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "Missing Parameters"
                ));
            }
            long guildId;
            try {
                guildId = Long.parseLong(guildIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "Not a number"
                ));
            }

            String leagueName = req.queryParams("leagueName");
            if (leagueName == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "Missing parameters"
                ));
            }

            GuildData guildData = GlobalData.getGuildDataById(guildId);
            if (guildData == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "No League Data in that Guild"
                ));
            }

            Guild guild = BotMain.JDA.getGuildById(guildData.getId());
            if (guild == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "Guild does Not Exist"
                ));
            }

            LeagueData leagueData = guildData.getLeagueByName(leagueName);
            if (leagueData == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "No League Data with that Name"
                ));
            }

            return handleLeague(req, res, guild, leagueData);
        }

        Object handleLeague(Request req, Response res, Guild guild, LeagueData league) throws Exception;
    }

}
