package com.onewho.gamerbot.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.Contestant;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.data.TeamData;
import com.onewho.gamerbot.data.UserData;

import java.util.Map;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.get;

public class GetInfoRequests {

    public static void init() {
        // get info about an existing set by its ID
        get("/league/info/set", (LeagueDataRoute) (req, res, guild, league) -> {
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
                return getGson().toJson(Map.of("error", setIdStr+"is not a number"));
            }
            SetData set = league.getSetDataById(setId);
            if (set == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "There is no set with ID "+setId));
            }

            String includeContestantsStr = req.queryParams("includeContestants");
            boolean includeContestants;
            if (includeContestantsStr == null) {
                includeContestants = false;
            } else if (includeContestantsStr.equals("true")) {
                includeContestants = true;
            } else if (includeContestantsStr.equals("false")) {
                includeContestants = false;
            } else {
                res.status(400);
                return getGson().toJson(Map.of("error", includeContestantsStr+"is not true or false"));
            }

            if (includeContestants) {
                String includeFullTeamStr = req.queryParams("includeFullTeam");
                boolean includeFullTeam;
                if (includeFullTeamStr == null) {
                    includeFullTeam = false;
                } else if (includeFullTeamStr.equals("true")) {
                    includeFullTeam = true;
                } else if (includeFullTeamStr.equals("false")) {
                    includeFullTeam = false;
                } else {
                    res.status(400);
                    return getGson().toJson(Map.of("error", includeFullTeamStr+"is not true or false"));
                }

                return getGson().toJson(Map.of("result", "Success",
                        "set", set.getJson(),
                        "contestant1", getContestantData(set.getContestant1(), includeFullTeam),
                        "contestant2", getContestantData(set.getContestant2(), includeFullTeam)
                ));
            } else {
                return getGson().toJson(Map.of("result", "Success", "set", set.getJson()));
            }
        });

        // get info about an existing contestant
        get("/league/info/contestant", (LeagueDataRoute) (req, res, guild, league) -> {
            String contestantIdStr = req.queryParams("contestantId");
            if (contestantIdStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "contestantId not defined"));
            }
            int contestantId;
            try {
                contestantId = Integer.parseInt(contestantIdStr);
            } catch (NumberFormatException e) {
                res.status(400);
                return getGson().toJson(Map.of("error", contestantIdStr+"is not a number"));
            }
            Contestant contestant = league.getContestantById(contestantId);
            if (contestant == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "There is no contestant with ID "+contestantId));
            }

            String includeFullTeamStr = req.queryParams("includeFullTeam");
            boolean includeFullTeam;
            if (includeFullTeamStr == null) {
                includeFullTeam = false;
            } else if (includeFullTeamStr.equals("true")) {
                includeFullTeam = true;
            } else if (includeFullTeamStr.equals("false")) {
                includeFullTeam = false;
            } else {
                res.status(400);
                return getGson().toJson(Map.of("error", includeFullTeamStr+"is not true or false"));
            }

            return getGson().toJson(Map.of("result", "Success",
                    "contestant", getContestantData(contestant, includeFullTeam)
            ));
        });
    }

    public static JsonObject getContestantData(Contestant contestant, boolean includeFullTeam) {
        JsonObject data = contestant.getJson();
        if (includeFullTeam) {
            JsonArray teamMembers = new JsonArray();
            if (contestant.isTeam()) {
                TeamData team = (TeamData) contestant;
                for (UserData user : team.getUsers()) {
                    teamMembers.add(user.getJson());
                }
            }
            data.add("team_members", teamMembers);
        }
        return data;
    }

}
