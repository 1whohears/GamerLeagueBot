package com.onewho.gamerbot.net;

import com.onewho.gamerbot.command.CreateQueue;
import com.onewho.gamerbot.data.QueueData;
import com.onewho.gamerbot.data.QueueResult;
import com.onewho.gamerbot.data.UserData;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.get;

public class QueueRequests {

    public static void init() {
        // TODO queue reset timeout request
        // TODO queue start pregame request
        // TODO queue create set request
        // TODO queue close request

        // create queue
        get("/league/queue/create", (LeagueDataRoute) (req, res, guild, league) -> {
            AtomicReference<String> msg = new AtomicReference<>("");
            QueueData queue = CreateQueue.run(league, msg::set);

            String minPlayersStr = req.queryParams("minPlayers");
            if (minPlayersStr != null) {
                try {
                    queue.setMinPlayers(Integer.parseInt(minPlayersStr));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", minPlayersStr + "is not a number"));
                }
            }

            String teamSizeStr = req.queryParams("teamSize");
            if (teamSizeStr != null) {
                try {
                    queue.setTeamSize(Integer.parseInt(teamSizeStr));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", teamSizeStr + "is not a number"));
                }
            }

            String timeoutTimeStr = req.queryParams("timeoutTime");
            if (timeoutTimeStr != null) {
                try {
                    queue.setTimeoutTime(Integer.parseInt(timeoutTimeStr));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", timeoutTimeStr + "is not a number"));
                }
            }

            String subRequestTimeStr = req.queryParams("subRequestTime");
            if (subRequestTimeStr != null) {
                try {
                    queue.setSubRequestTime(Integer.parseInt(subRequestTimeStr));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", subRequestTimeStr + "is not a number"));
                }
            }

            String pregameTimeStr = req.queryParams("pregameTime");
            if (pregameTimeStr != null) {
                try {
                    queue.setPregameTime(Integer.parseInt(pregameTimeStr));
                } catch (NumberFormatException e) {
                    res.status(400);
                    return getGson().toJson(Map.of("error", pregameTimeStr + "is not a number"));
                }
            }

            String allowLargerTeamsStr = req.queryParams("allowLargerTeams");
            if (allowLargerTeamsStr != null) {
                if (allowLargerTeamsStr.equals("true")) queue.setAllowLargerTeams(true);
                else if (allowLargerTeamsStr.equals("false")) queue.setAllowLargerTeams(false);
                else {
                    res.status(400);
                    return getGson().toJson(Map.of("error", allowLargerTeamsStr + "is not true or false"));
                }
            }

            String allowOddNumStr = req.queryParams("allowOddNum");
            if (allowOddNumStr != null) {
                if (allowOddNumStr.equals("true")) queue.setAllowLargerTeams(true);
                else if (allowOddNumStr.equals("false")) queue.setAllowLargerTeams(false);
                else {
                    res.status(400);
                    return getGson().toJson(Map.of("error", allowOddNumStr + "is not true or false"));
                }
            }

            String resetTimeoutOnJoinStr = req.queryParams("resetTimeoutOnJoin");
            if (resetTimeoutOnJoinStr != null) {
                if (resetTimeoutOnJoinStr.equals("true")) queue.setAllowLargerTeams(true);
                else if (resetTimeoutOnJoinStr.equals("false")) queue.setAllowLargerTeams(false);
                else {
                    res.status(400);
                    return getGson().toJson(Map.of("error", resetTimeoutOnJoinStr + "is not true or false"));
                }
            }

            String ifEnoughPlayersAutoStartStr = req.queryParams("ifEnoughPlayersAutoStart");
            if (ifEnoughPlayersAutoStartStr != null) {
                if (ifEnoughPlayersAutoStartStr.equals("true")) queue.setAllowLargerTeams(true);
                else if (ifEnoughPlayersAutoStartStr.equals("false")) queue.setAllowLargerTeams(false);
                else {
                    res.status(400);
                    return getGson().toJson(Map.of("error", ifEnoughPlayersAutoStartStr + "is not true or false"));
                }
            }

            String allowJoinViaDiscordStr = req.queryParams("allowJoinViaDiscord");
            if (allowJoinViaDiscordStr != null) {
                if (allowJoinViaDiscordStr.equals("true")) queue.setAllowLargerTeams(true);
                else if (allowJoinViaDiscordStr.equals("false")) queue.setAllowLargerTeams(false);
                else {
                    res.status(400);
                    return getGson().toJson(Map.of("error", allowJoinViaDiscordStr + "is not true or false"));
                }
            }

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
            QueueData queue = league.getQueueById(queueId);
            if (queue == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Queue with ID "+queueId+" does not exist."));
            }

            QueueResult result = queue.addIndividual(user);

            return getGson().toJson(Map.of("result", result.name(), "queue", queue.getJson()));
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
            QueueData queue = league.getQueueById(queueId);
            if (queue == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Queue with ID "+queueId+" does not exist."));
            }

            boolean result = queue.removeFromQueue(user.getId());

            return getGson().toJson(Map.of("result", result ? "REMOVED" : "ALREADY_REMOVED", "queue", queue.getJson()));
        });

        // check in queue
        get("/league/queue/check_in", (LeagueDataRoute) (req, res, guild, league) -> {
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
            QueueData queue = league.getQueueById(queueId);
            if (queue == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Queue with ID "+queueId+" does not exist."));
            }

            QueueResult result = queue.checkIn(user.getId());

            return getGson().toJson(Map.of("result", result.name(), "queue", queue.getJson()));
        });

        // check out queue
        get("/league/queue/check_out", (LeagueDataRoute) (req, res, guild, league) -> {
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
            QueueData queue = league.getQueueById(queueId);
            if (queue == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Queue with ID "+queueId+" does not exist."));
            }

            QueueResult result = queue.checkOut(user.getId());

            return getGson().toJson(Map.of("result", result.name(), "queue", queue.getJson()));
        });

        // check queue state
        get("/league/queue/state", (LeagueDataRoute) (req, res, guild, league) -> {
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

            QueueData queue = league.getQueueById(queueId);
            if (queue == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "Queue with ID "+queueId+" does not exist."));
            }

            return getGson().toJson(Map.of("result", "Success", "queue", queue.getJson()));
        });
    }

}
