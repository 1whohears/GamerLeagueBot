package com.onewho.gamerbot.net;

import com.onewho.gamerbot.command.CreateQueue;
import com.onewho.gamerbot.data.QueueData;
import com.onewho.gamerbot.data.UserData;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.get;

public class QueueRequests {

    public static void init() {
        // TODO create queue
        get("/league/queue/create", (LeagueDataRoute) (req, res, guild, league) -> {
            // the following parameters should be optional and if not defined use the defaults defined in league data

            // minPlayers
            // teamSize
            // allowLargerTeams
            // allowOddNum
            // timeoutTime
            // subRequestTime
            // pregameTime
            // resetTimeoutOnJoin
            // ifEnoughPlayersAutoStart

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

            AtomicReference<String> msg = new AtomicReference<>("");
            QueueData queue = CreateQueue.run(league, msg::set);

            return getGson().toJson(Map.of("result", msg.get(), "queue", queue.getJson()));
        });

        // TODO join queue
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

            

            return getGson().toJson(Map.of("result", "bruh"));
        });

        // TODO leave queue
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

            

            return getGson().toJson(Map.of("result", "bruh"));
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
