package com.onewho.gamerbot.net;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import net.dv8tion.jda.api.entities.Guild;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.Map;

import static com.onewho.gamerbot.net.NetApiManager.keys;
import static com.onewho.gamerbot.data.GlobalData.getGson;

public interface LeagueDataRoute  extends spark.Route {
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
