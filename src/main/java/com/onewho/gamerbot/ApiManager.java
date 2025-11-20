package com.onewho.gamerbot;

import java.util.HashMap;
import java.util.Map;

import static com.onewho.gamerbot.data.GlobalData.getGson;
import static spark.Spark.*;

public class ApiManager {

    public static void init() {
        port(8080);

        // Allow JSON requests
        before((req, res) -> {
            res.type("application/json");
        });

        // Health check
        get("/ping", (req, res) -> {
            return getGson().toJson(Map.of("status", "ok"));
        });

        // Example: Paper plugin sends a message to Discord
        post("/paper/message", (req, res) -> {
            Map<String, Object> body = getGson().fromJson(req.body(), HashMap.class);

            String text = (String) body.get("text");
            String sender = (String) body.get("sender");

            System.out.println("Paper sent: " + text + " (from " + sender + ")");

            // TODO: send the message to a Discord channel
            // Example:
            // DiscordMain.sendToChannel(text);

            return getGson().toJson(Map.of("received", true));
        });

        // Example: Paper asks the bot for info
        get("/paper/status", (req, res) -> {
            Map<String, Object> status = new HashMap<>();
            status.put("botOnline", true);
            status.put("guilds", 4); // example
            status.put("version", "1.0");

            return getGson().toJson(status);
        });

        // Catch-all
        notFound((req, res) -> getGson().toJson(Map.of("error", "Not found")));

    }

}
