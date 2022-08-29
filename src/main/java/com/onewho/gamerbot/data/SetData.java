package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

public class SetData {
	
	private int id;
	private long p1Id;
	private long p2Id;
	private int p1s = 0;
	private int p2s = 0;
	private boolean p1c = false;
	private boolean p2c = false;
	private String created = "";
	private String completed = "";
	
	public SetData(JsonObject data) {
		id = data.get("id").getAsInt();
		p1Id = data.get("p1Id").getAsLong();
		p2Id = data.get("p2Id").getAsLong();
		p1s = data.get("p1s").getAsInt();
		p2s = data.get("p2s").getAsInt();
		p1c = data.get("p1c").getAsBoolean();
		p2c = data.get("p2c").getAsBoolean();
		created = data.get("created").getAsString();
		completed = data.get("completed").getAsString();
	}
	
	public SetData(int id, long p1Id, long p2Id, String created) {
		this.id = id;
		this.p1Id = p1Id;
		this.p2Id = p2Id;
		this.created = created;
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
		data.addProperty("created", created);
		data.addProperty("completed", completed);
		return data;
	}
	
	public int getId() {
		return id;
	}
	
	public long getP1Id() {
		return p1Id;
	}
	
	public long getP2Id() {
		return p2Id;
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

	public String getCreatedDate() {
		return created;
	}

	public void setCreatedData(String created) {
		this.created = created;
	}

	public String getCompletedDate() {
		return completed;
	}

	public void setCompletedDate(String completed) {
		this.completed = completed;
	}
	
	public boolean isComplete() {
		return p1c && p2c;
	}
	
	public boolean isUnconfirmed() {
		return (p1c && !p2c) || (!p1c && p2c);
	}
	
	public boolean isP1Win() {
		return isComplete() && p1s > p2s;
	}
	
	public boolean isP2Win() {
		return isComplete() && p2s > p1s;
	}
	
	public boolean isDraw() {
		return isComplete() && p1s == p2s;
	}
	
	public ReportResult report(long reporterId, int reporterScore, int opponentScore, String date) {
		if (reporterId == p1Id) {
			if (p2c) {
				if (p1s == reporterScore && p2s == opponentScore) {
					p1c = true;
					completed = date;
					return ReportResult.SetVerified;
				} else return ReportResult.ScoreConflict;
			} else {
				p1s = reporterScore;
				p2s = opponentScore;
				p1c = true;
				return ReportResult.WaitingForOpponent;
			}
		} else if (reporterId == p2Id) {
			if (p1c) {
				if (p2s == reporterScore && p1s == opponentScore) {
					p2c = true;
					completed = date;
					return ReportResult.SetVerified;
				} else return ReportResult.ScoreConflict;
			} else {
				p2s = reporterScore;
				p1s = opponentScore;
				p2c = true;
				return ReportResult.WaitingForOpponent;
			}
		} else return ReportResult.IDsDontMatch;
	}
	
	public ReportResult reportAdmin(long id1, long id2, int score1, int score2, String date) {
		if (id1 == p1Id && id2 == p2Id) {
			p1c = p2c = true;
			completed = date;
			p1s = score1;
			p2s = score2;
			return ReportResult.SetVerified;
		} else if (id1 == p2Id && id2 == p1Id) {
			p1c = p2c = true;
			completed = date;
			p1s = score2;
			p2s = score1;
			return ReportResult.SetVerified;
		} else return ReportResult.IDsDontMatch;
	}
	
	@Override
	public String toString() {
		return id+"/"+p1Id+":"+p1s+"/"+p2Id+":"+p2s+"/"+created+"/"+completed;
	}
	
	public boolean hasPlayer(long id) {
		return id == p1Id || id == p2Id; 
	}
	
	public String getStatus() {
		if (this.isP1Win()) return "P1 WIN";
		if (this.isP2Win()) return "P2 WIN";
		if (this.isDraw()) return "DRAW";
		if (this.isUnconfirmed()) return "UNCONFIRMED";
		return "ASIGNED";
	}
	
}
