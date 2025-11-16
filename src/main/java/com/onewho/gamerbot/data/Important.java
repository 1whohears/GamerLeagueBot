package com.onewho.gamerbot.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Important {

    public static final Random RANDOM = new Random();

	public static String getError() {
		return errors[(int)(RANDOM.nextDouble()*errors.length)];
	}
	
	public static String getError(String msg) {
		return getError()+" "+msg;
	}
	
	private static String[] errors = { "ERROR!" };
	
	public static void load() {
        try {
            JsonObject jsonO = GlobalData.getGson().fromJson(Files.newBufferedReader(Paths.get("important.json")), JsonObject.class);
            if (jsonO.get("insults") == null) return;
            JsonArray json = jsonO.get("insults").getAsJsonArray();
            errors = new String[json.size()];
            for (int i = 0; i < json.size(); ++i) errors[i] = json.get(i).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            errors = new String[] {"ERROR!"};
        }
	}
	
}
