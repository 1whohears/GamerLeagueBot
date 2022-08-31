package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class SetData {
	
	private int id = -1;
	private long p1Id = -1;
	private long p2Id = -1;
	private int p1s = 0;
	private int p2s = 0;
	private boolean p1c = false;
	private boolean p2c = false;
	private String created = "";
	private String completed = "";
	private long messageId = -1;
	private boolean processed = false;
	
	public SetData(JsonObject data) {
		id = ParseData.getInt(data, "id", id);
		p1Id = ParseData.getLong(data, "p1Id", p1Id);
		p2Id = ParseData.getLong(data, "p2Id", p2Id);
		p1s = ParseData.getInt(data, "p1s", p1s);
		p2s = ParseData.getInt(data, "p2s", p2s);
		p1c = ParseData.getBoolean(data, "p1c", p1c);
		p2c = ParseData.getBoolean(data, "p2c", p2c);
		created = ParseData.getString(data, "created", created);
		completed = ParseData.getString(data, "completed", completed);
		messageId = ParseData.getLong(data, "messageId", messageId);
		processed = ParseData.getBoolean(data, "processed", processed);
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
		data.addProperty("messageId", messageId);
		data.addProperty("processed", processed);
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
	
	public boolean isProcessed() {
		return processed;
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
	
	public ReportResult report(long reporterId, long opponentId, int reporterScore, int opponentScore, String date) {
		if (isComplete()) return ReportResult.AlreadyVerified;
		System.out.println("reporter id = "+reporterId);
		System.out.println("opponent id = "+opponentId);
		if (reporterId == p1Id && opponentId == p2Id) {
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
		} else if (reporterId == p2Id && opponentId == p1Id) {
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
		if (isProcessed()) return ReportResult.AlreadyVerified;
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
		return "ASSIGNED";
	}
	
	public void displaySet(TextChannel channel) {
		String p1Name = "", p2Name = "", date = "";
		if (this.isComplete()) {
			p1Name = "*"+channel.getGuild().getMemberById(getP1Id()).getEffectiveName()+"*";
			p2Name = "*"+channel.getGuild().getMemberById(getP2Id()).getEffectiveName()+"*";
			date = completed;
		} else {
			p1Name = "<@"+getP1Id()+">";
			p2Name = "<@"+getP2Id()+">";
			date = created;
		}
		MessageCreateData mcd = new MessageCreateBuilder()
				.addContent("__**ID:"+getId()+"**__ ")
				.addContent(p1Name)
				.addContent(" **"+getP1score()+"** ")
				.addContent(p2Name)
				.addContent(" **"+getP2score()+"** ")
				.addContent("__**"+getStatus()+"**__ ")
				.addContent("__"+date+"__")
				.build();
		if (messageId == -1) messageId = channel.sendMessage(mcd).complete().getIdLong();
		else {
			MessageEditData med = new MessageEditBuilder().applyCreateData(mcd).build();
			try {
				channel.editMessageById(messageId, med).queue();
			} catch (ErrorResponseException e) {
				switch (e.getErrorResponse()) {
				case UNKNOWN_MESSAGE:
					messageId = channel.sendMessage(mcd).complete().getIdLong();
					return;
				default:
					return;
				}
			}
		}
	}
	
	public void removeSetDisplay(TextChannel channel) {
		try {
			channel.deleteMessageById(messageId).queue();
		} catch (ErrorResponseException e) {
		}
		messageId = -1;
	}
	
	public void processSet(LeagueData guild) {
		if (!isComplete() || processed) return;
		UserData p1 = guild.getUserDataById(p1Id);
		UserData p2 = guild.getUserDataById(p2Id);
		int change = (int)getChangeInScore(p1s, p2s, p1.getScore(), p2.getScore(), guild.getK());
		p1.setScore(p1.getScore() + change);
		p2.setScore(p2.getScore() - change);
		processed = true;
	}
	
	private static double getChangeInScore(int points1, int points2, int score1, int score2, double k) {
		return k * (getActualScore(points1, points2) - getExpectedScore(score1, score2));
	}
	
	private static double getActualScore(int points1, int points2) {
		if (points1 > points2) return 1.0;
		else if (points1 < points2) return 0.0;
		return 0.5;
	}
	
	private static double getExpectedScore(int score1, int score2) {
		return getQ(score1) / (getQ(score1) + getQ(score2));
	}
	
	private static double getQ(int score) {
		return Math.pow(10.0, score / 400.0);
	}
	
}
