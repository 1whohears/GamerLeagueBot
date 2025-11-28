package com.onewho.gamerbot;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.command.CreateQueue;
import com.onewho.gamerbot.command.CreateRandomTeamSet;
import com.onewho.gamerbot.command.CreateTeamSet;
import com.onewho.gamerbot.command.ReportAdmin;
import com.onewho.gamerbot.data.*;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.*;

public class ApiManager {

    private static String[] keys;

    public static void init() throws IOException {
        keys = TokenReader.getAPIKeys();

        port(8080);

        before((req, res) -> res.type("application/json"));

        // chack if a league exists.
        get("/league/ping", (LeagueDataRoute) (req, res, guild, league) -> {
            return getGson().toJson(Map.of("league_exists", true));
        });

        // used to link a minecraft account to a discord league account.
        get("/league/link/minecraft/player", (LeagueDataRoute) (req, res, guild, league) -> {
            String mcUUIDStr = req.queryParams("mcUUID");
            if (mcUUIDStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "mcUUID not defined"));
            }

            String discordUsername = req.queryParams("discordUsername");
            if (discordUsername == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "discordUsername not defined"));
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

        // report admin command hook
        get("/league/reportadmin", (LeagueDataRoute) (req, res, guild, league) -> {
            String setIdStr = req.queryParams("setId");
            if (setIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "setId not defined"));
            }
            int setId;
            try {
                setId = Integer.parseInt(setIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", setIdStr+" is not a number"));
            }

            String player1UUID = req.queryParams("player1UUID");
            if (player1UUID == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "player1UUID not defined"));
            }

            String player2UUID = req.queryParams("player2UUID");
            if (player2UUID == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "player2UUID not defined"));
            }

            String player1ScoreStr = req.queryParams("player1Score");
            if (player1ScoreStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "player1Score not defined"));
            }
            int player1Score;
            try {
                player1Score = Integer.parseInt(player1ScoreStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", player1ScoreStr+" is not a number"));
            }

            String player2ScoreStr = req.queryParams("player2Score");
            if (player2ScoreStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "player2Score not defined"));
            }
            int player2Score;
            try {
                player2Score = Integer.parseInt(player2ScoreStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", player2ScoreStr+" is not a number"));
            }

            UserData player1 = league.getUserByExtraData("mcUUID", player1UUID);
            if (player1 == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Player 1 does not have a linked discord account"));
            }

            UserData player2 = league.getUserByExtraData("mcUUID", player2UUID);
            if (player2 == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Player 2 does not have a linked discord account"));
            }

            AtomicReference<String> msg = new AtomicReference<>("");
            SetData set = ReportAdmin.run(guild, league, msg::set, setId,
                    player1.getId(), player2.getId(), player1Score, player2Score);
            if (set == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", msg.get()));
            }

            String result = "Set "+set.getId()+" successfully reported.";
            if (!msg.get().isEmpty()) result = msg.get();
            return getGson().toJson(Map.of("result", result));
        });

        // create queue
        get("/league/queue/create", (LeagueDataRoute) (req, res, guild, league) -> {
            String teamSizeStr = req.queryParams("teamSize");
            if (teamSizeStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "teamSize not defined"));
            }
            int teamSize;
            try {
                teamSize = Integer.parseInt(teamSizeStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", teamSizeStr+"is not a number"));
            }

            String endTime = req.queryParams("endTime");
            LocalDate endDate = UtilCalendar.getDate(endTime);
            if (endTime != null && endDate == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", Important.getError()+" "+endTime
                        +" is not a valid time format! DO: dd-MM-yyyy HH-mm"));
            }

            AtomicReference<String> msg = new AtomicReference<>("");
            QueueData queue = CreateQueue.run(league, msg::set, teamSize, endTime);

            return getGson().toJson(Map.of("result", msg.get(), "queue", queue.getJson()));
        });

        // join queue
        get("/league/queue/join", (LeagueDataRoute) (req, res, guild, league) -> {
            String mcUUIDStr = req.queryParams("mcUUID");
            if (mcUUIDStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "mcUUID not defined"));
            }
            UserData user = league.getUserByExtraData("mcUUID", mcUUIDStr);
            if (user == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "You have not linked a discord account!"));
            }

            String queueIdStr = req.queryParams("queueId");
            if (queueIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "queueId not defined"));
            }
            int queueId;
            try {
                queueId = Integer.parseInt(queueIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", queueIdStr+"is not a number"));
            }



            return getGson().toJson(Map.of("result", "bruh"));
        });

        // leave queue
        get("/league/queue/leave", (LeagueDataRoute) (req, res, guild, league) -> {
            String mcUUIDStr = req.queryParams("mcUUID");
            if (mcUUIDStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "mcUUID not defined"));
            }
            UserData user = league.getUserByExtraData("mcUUID", mcUUIDStr);
            if (user == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "You have not linked a discord account!"));
            }

            String queueIdStr = req.queryParams("queueId");
            if (queueIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "queueId not defined"));
            }
            int queueId;
            try {
                queueId = Integer.parseInt(queueIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", queueIdStr+"is not a number"));
            }



            return getGson().toJson(Map.of("result", "bruh"));
        });

        // resolve queue
        get("/league/queue/leave", (LeagueDataRoute) (req, res, guild, league) -> {
            String queueIdStr = req.queryParams("queueId");
            if (queueIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "queueId not defined"));
            }
            int queueId;
            try {
                queueId = Integer.parseInt(queueIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", queueIdStr+"is not a number"));
            }



            return getGson().toJson(Map.of("result", "bruh"));
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
            members.add(j);
        }
        json.add("members", members);
        return json;
    }

    private static String[] parseUUIDList(String uuidListStr) {
        return uuidListStr.split(",");
    }

    public interface LeagueDataRoute extends spark.Route {
        default Object handle(Request req, Response res) throws Exception {
            String apikey = req.queryParams("apikey");
            if (apikey == null || !Arrays.stream(keys).allMatch(apikey::equals)) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "Invalid API Key"
                ));
            }

            String guildIdStr = req.queryParams("guildId");
            if (guildIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "guildId not defined"
                ));
            }
            long guildId;
            try {
                guildId = Long.parseLong(guildIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", guildIdStr+" is not a number"
                ));
            }

            String leagueName = req.queryParams("leagueName");
            if (leagueName == null) {
                res.status(400);
                return getGson().toJson(Map.of(
                        "league_exists", false,
                        "error", "leagueName not defined"
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
