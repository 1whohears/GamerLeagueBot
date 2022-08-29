package com.onewho.gamerbot.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class UserData {
	
	private long id;
	private boolean active;
	private int setsPerWeek;
	private String lastActive;
	
	public UserData(JsonObject data) {
		id = data.get("id").getAsLong();
		active = data.get("active").getAsBoolean();
		setsPerWeek = data.get("sets per week").getAsInt();
		setLastActive(data.get("last active").getAsString());
	}
	
	public UserData(long id) {
		this.id = id;
		this.active = false;
		this.setsPerWeek = 0;
		this.setLastActive(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("active", active);
		data.addProperty("sets per week", setsPerWeek);
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
	
}
