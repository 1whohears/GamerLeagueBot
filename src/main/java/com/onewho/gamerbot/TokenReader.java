package com.onewho.gamerbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.onewho.gamerbot.data.GlobalData;

public class TokenReader {
	
	public static String getJDAToken() throws JsonSyntaxException, JsonIOException, IOException {
		return GlobalData.getGson().fromJson(
                Files.newBufferedReader(Paths.get("token.json")), JsonObject.class
        ).get("token").getAsString();
	}

    public static String[] getAPIKeys() throws JsonSyntaxException, JsonIOException, IOException {
        JsonArray ja = GlobalData.getGson().fromJson(Files.newBufferedReader(Paths.get("apikeys.json")), JsonArray.class);
        String[] keys = new String[ja.size()];
        for (int i = 0; i < ja.size(); ++i) keys[i] = ja.get(i).getAsString();
        return keys;
    }
	
}
