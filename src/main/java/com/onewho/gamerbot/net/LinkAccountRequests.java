package com.onewho.gamerbot.net;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.UserData;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;
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
    }

}
