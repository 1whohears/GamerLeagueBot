package com.onewho.gamerbot.net;

import com.onewho.gamerbot.TokenReader;

import java.io.IOException;
import java.util.Map;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.*;

public class NetApiManager {

    static String[] keys;

    public static void init() throws IOException {
        keys = TokenReader.getAPIKeys();

        port(8080);

        before((req, res) -> res.type("application/json"));

        // chack if a league exists.
        get("/league/ping", (LeagueDataRoute) (req, res, guild, league) -> {
            return getGson().toJson(Map.of("league_exists", true));
        });

        LinkAccountRequests.init();
        CreateTeamRequests.init();
        ReportSetRequests.inti();
        QueueRequests.init();

        notFound((req, res) -> getGson().toJson(Map.of("error", "Not found")));
    }

}
