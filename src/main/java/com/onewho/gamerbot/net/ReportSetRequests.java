package com.onewho.gamerbot.net;

import com.onewho.gamerbot.command.ReportAdmin;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.data.UserData;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static com.onewho.gamerbot.net.CreateTeamRequests.parseUUIDList;
import static spark.Spark.get;

public class ReportSetRequests {

    public static void inti() {
        // cancel set
        get("/league/set/cancel", (LeagueDataRoute) (req, res, guild, league) -> {
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

            String penaltyMcUUIDListStr = req.queryParams("penaltyMcUUIDList");
            String[] penaltyMcUUIDList;
            if (penaltyMcUUIDListStr == null) penaltyMcUUIDList = new String[0];
            else penaltyMcUUIDList = parseUUIDList(penaltyMcUUIDListStr);

            AtomicReference<String> result = new AtomicReference<>("Success");
            if (!league.cancelSet(setId, penaltyMcUUIDList, result::set)) {
                res.status(400);
                return getGson().toJson(Map.of("error", result.get()));
            }

            GlobalData.markReadyToSave();
            return getGson().toJson(Map.of("result", result.get()));
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

            String updateRanksStr = req.queryParams("updateRanks");
            boolean updateRanks = false;
            if (updateRanksStr != null) {
                updateRanks = Boolean.parseBoolean(updateRanksStr);
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

            if (updateRanks) {
                MessageChannelUnion channel = guild.getChannelById(MessageChannelUnion.class,
                        league.getChannelId("bot-commands"));
                league.updateRanks(guild, channel);
                GlobalData.markReadyToSave();
            }
            String result = "Set "+set.getId()+" successfully reported.";
            if (!msg.get().isEmpty()) result = msg.get();
            return getGson().toJson(Map.of("result", result));
        });

        // update ranks
        get("/league/updateranks", (LeagueDataRoute) (req, res, guild, league) -> {
            MessageChannelUnion channel = guild.getChannelById(MessageChannelUnion.class,
                    league.getChannelId("bot-commands"));
            league.updateRanks(guild, channel);
            GlobalData.markReadyToSave();
            return getGson().toJson(Map.of("result", "Updated ranks!"));
        });
    }

}
