package com.onewho.gamerbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class TokenReader {
	
	public static String getJDAToken() throws JsonSyntaxException, JsonIOException, IOException {
		return new GsonBuilder().create().fromJson(Files.newBufferedReader(Paths.get("token.json")), JsonObject.class).get("token").getAsString();
	}
	
}
