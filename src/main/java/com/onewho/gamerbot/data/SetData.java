package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

public class SetData {
	
	private int id;
	private long p1Id;
	private long p2Id;
	private int p1s;
	private int p2s;
	private boolean p1c;
	private boolean p2c;
	//private int state;
	private String created;
	private String completed;
	
	public SetData(JsonObject data) {
		id = data.get("id").getAsInt();
		p1Id = data.get("p1Id").getAsLong();
		p2Id = data.get("p2Id").getAsLong();
		p1s = data.get("p1s").getAsInt();
		p2s = data.get("p2s").getAsInt();
		p1c = data.get("p1c").getAsBoolean();
		p2c = data.get("p2c").getAsBoolean();
		//state = data.get("state").getAsInt();
		created = data.get("created").getAsString();
		completed = data.get("completed").getAsString();
	}
	
	public SetData(int id, long p1Id, long p2Id, String created) {
		this.id = id;
		this.p1Id = p1Id;
		this.p2Id = p2Id;
		p1s = 0;
		p2s = 0;
		p1c = false;
		p2c = false;
		//state = 0;
		this.created = created;
		this.completed = "";
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("p1Id", p1Id);
		data.addProperty("p2Id", p2Id);
		data.addProperty("p1s", p1s);
		data.addProperty("p2s", p2s);
		data.addProperty("p1c", p1c);
		data.addProperty("p2c", p2c);
		//data.addProperty("state", state);
		data.addProperty("created", created);
		data.addProperty("completed", completed);
		return data;
	}
	
	public int getId() {
		return id;
	}

	public int getP1score() {
		return p1s;
	}

	public void setP1score(int p1s) {
		this.p1s = p1s;
	}

	public int getP2score() {
		return p2s;
	}

	public void setP2score(int p2s) {
		this.p2s = p2s;
	}

	public boolean isP1confirm() {
		return p1c;
	}

	public void setP1confirm(boolean p1c) {
		this.p1c = p1c;
	}

	public boolean isP2confirm() {
		return p2c;
	}

	public void setP2confirm(boolean p2c) {
		this.p2c = p2c;
	}

	/*public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}*/

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}
	
	public boolean isComplete() {
		return !completed.isEmpty();
	}
	
}
