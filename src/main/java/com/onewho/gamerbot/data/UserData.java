package com.onewho.gamerbot.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class UserData {
	
	private long id = -1;
	private boolean active = false;
	private int setsPerWeek = 0;
	private String lastActive = "";
	private int score = 0;
	
	protected UserData(JsonObject data) {
		id = ParseData.getLong(data, "id", id);
		active = ParseData.getBoolean(data, "active", active);
		setsPerWeek = ParseData.getInt(data, "sets per week", setsPerWeek);
		setLastActive(ParseData.getString(data, "last active", lastActive));
		setScore(ParseData.getInt(data, "score", score));
	}
	
	protected UserData(long id) {
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
	
	public JsonObject getBackupJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("score", score);
		return data;
	}
	
	protected void readBackup(JsonObject data) {
		if (data.get("id") == null || data.get("id").getAsLong() != id) return;
		setScore(ParseData.getInt(data, "score", score));
	}
	
	/**
	 * @return this user's id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @return is this user active in this league
	 */
	public boolean getActive() {
		return active;
	}
	
	/**
	 * @param active set is this user active in this league
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * @return max number of sets this user could be assigned to each week
	 */
	public int getSetsPerWeek() {
		return setsPerWeek;
	}
	
	/**
	 * @param setsPerWeek set max number of sets this user could be assigned to each week
	 */
	public void setSetsPerWeek(int setsPerWeek) {
		this.setsPerWeek = setsPerWeek;
	}
	
	/**
	 * get the date this user was last active
	 * @return dd-mm-yyyy format
	 */
	public String getLastActive() {
		return lastActive;
	}
	
	/**
	 * set the date this user was last active
	 * @param lastActive dd-mm-yyyy format
	 */
	public void setLastActive(String lastActive) {
		this.lastActive = lastActive;
		this.active = true;
	}
	
	@Override
	public String toString() {
		return id+"/"+active+"/"+setsPerWeek+"/"+setsPerWeek;
	}

	/**
	 * @return the score of this user
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * @param score set this user's score
	 */
	public void setScore(int score) {
		this.score = score;
	}
	
}
