package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

public class SetData {
	
	private int id;
	
	public SetData(JsonObject data) {
		id = data.get("id").getAsInt();
		
	}
	
	public SetData(int id) {
		this.id = id;
		
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		
		return data;
	}
	
	public int getId() {
		return id;
	}
	
}
