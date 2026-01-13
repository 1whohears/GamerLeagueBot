package com.onewho.gamerbot.net;

import com.onewho.gamerbot.command.CreateQueue;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.QueueData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilCalendar;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.get;

public class QueueRequests {

    public static void init() {
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
        get("/league/queue/resolve", (LeagueDataRoute) (req, res, guild, league) -> {
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
    }

}
