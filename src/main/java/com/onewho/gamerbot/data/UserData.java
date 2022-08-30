package com.onewho.gamerbot.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class UserData {
	
	private long id;
	private boolean active = false;
	private int setsPerWeek = 0;
	private String lastActive = "";
	private int score = 0;
	
	public UserData(JsonObject data) {
		id = data.get("id").getAsLong();
		active = data.get("active").getAsBoolean();
		setsPerWeek = data.get("sets per week").getAsInt();
		setLastActive(data.get("last active").getAsString());
		setScore(data.get("score").getAsInt());
	}
	
	public UserData(long id) {
		this.id = id;
		this.setLastActive(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("active", active);
		data.addProperty("sets per week", setsPerWeek);
		data.addProperty("last active", lastActive);
		data.addProperty("score", score);
		return data;
	}
	
	public long getId() {
		return id;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getSetsPerWeek() {
		return setsPerWeek;
	}

	public void setSetsPerWeek(int setsPerWeek) {
		this.setsPerWeek = setsPerWeek;
	}

	public String getLastActive() {
		return lastActive;
	}

	public void setLastActive(String lastActive) {
		this.lastActive = lastActive;
	}
	
	@Override
	public String toString() {
		return id+"/"+active+"/"+setsPerWeek+"/"+setsPerWeek;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}