package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.util.UtilCalendar;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class UserData implements Contestant {
	
	private final long id;
	private boolean active = false;
	private int setsPerWeek = 0;
	private String lastActive = "";
	private int score = 0;
	private boolean locked = false;
    private JsonObject extraData;
	
	protected UserData(JsonObject data) {
		id = ParseData.getLong(data, "id", -1);
		active = ParseData.getBoolean(data, "active", false);
		setsPerWeek = ParseData.getInt(data, "sets per week", setsPerWeek);
		lastActive = ParseData.getString(data, "last active", lastActive); // DO NOT USE setLastActive HERE!
		setScore(ParseData.getInt(data, "score", score));
		locked = ParseData.getBoolean(data, "locked", locked);
        extraData = ParseData.getJsonObject(data, "extra_data");
	}
	
	protected UserData(long id) {
		this.id = id;
		this.setLastActive(UtilCalendar.getCurrentDateString());
        this.extraData = new JsonObject();
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("active", active);
		data.addProperty("sets per week", setsPerWeek);
		data.addProperty("last active", lastActive);
		data.addProperty("score", score);
		data.addProperty("locked", locked);
        data.addProperty("type", getType().name());
        data.add("extra_data", extraData);
		return data;
	}
	
	public JsonObject getBackupJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("score", score);
        data.add("extra_data", extraData);
		return data;
	}
	
	public void readBackup(JsonObject data) {
		if (data.get("id") == null || data.get("id").getAsLong() != id) return;
		setScore(ParseData.getInt(data, "score", score));
        extraData = ParseData.getJsonObject(data, "extra_data");
	}

    @Override
    public boolean isIndividual() {
        return true;
    }

    @Override
    public boolean isTeam() {
        return false;
    }

	@Override
	public int getTeamSize() {
		return 1;
	}

	@Override
    public boolean hasUserId(long id) {
        return id == getId();
    }

    @Override
    public Collection<Long> getUserIds() {
        return List.of(id);
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
	public boolean isActive() {
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
		if (setsPerWeek < 0) setsPerWeek = 0;
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
    public String getTeamName() {
        return "";
    }

    @Override
	public String toString() {
		return id+"/"+active+"/"+setsPerWeek+"/"+score;
	}

    @Override
    public Type getType() {
        return Type.INDIVIDUAL;
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

    public void changeScore(int change) {
        setScore(getScore() + change);
    }
	
	public boolean isLocked() {
		return locked;
	}
	
	public void lockUser() {
		locked = true;
	}
	
	public void unlockUser() {
		locked = false;
	}

    @NotNull
    public JsonObject getExtraData() {
        return extraData;
    }
	
}
