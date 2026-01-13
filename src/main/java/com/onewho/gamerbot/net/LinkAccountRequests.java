package com.onewho.gamerbot.net;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilCalendar;

import java.util.Map;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.get;

public class LinkAccountRequests {

    public static void init() {
        // used to link a minecraft account to a discord league account.
        get("/league/link/minecraft/player", (LeagueDataRoute) (req, res, guild, league) -> {
            String mcUUIDStr = req.queryParams("mcUUID");
            if (mcUUIDStr == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "mcUUID not defined"));
            }

            String linkCode = req.queryParams("linkCode");
            if (linkCode == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "linkCode not defined"));
            }

            UserData user = league.getUserByExtraData("linkCode", linkCode);
            if (user == null) {
                res.status(400);
                return getGson().toJson(Map.of("error", "The Link Code is either Expired or Incorrect. " +
                        "Generate a new one by using the $linkdiscord command in the discord server."));
            }

            JsonObject userData = user.getExtraData();
            if (!userData.has("linkCodeCreateTime")) {
                res.status(400);
                return getGson().toJson(Map.of("error", "The Link Code is either Expired or Incorrect. " +
                        "Generate a new one by using the $linkdiscord command in the discord server."));
            }
            String linkCodeCreateTime = userData.get("linkCodeCreateTime").getAsString();
            if (!UtilCalendar.isWithin60Seconds(linkCodeCreateTime)) {
                res.status(400);
                return getGson().toJson(Map.of("error", "The Link Code is either Expired or Incorrect. " +
                        "Generate a new one by using the $linkdiscord command in the discord server."));
            }

            user.getExtraData().addProperty("mcUUID", mcUUIDStr);
            user.getExtraData().addProperty("linkCode", "");
            GlobalData.saveData();

            return getGson().toJson(Map.of("result", "Successfully linked minecraft account with discord account!"));
        });
    }

}
