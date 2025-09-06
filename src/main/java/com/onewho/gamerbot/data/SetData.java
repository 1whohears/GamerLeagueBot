package com.onewho.gamerbot.data;

import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class SetData {
	
	private final int id;
    private final Contestant c1;
    private final Contestant c2;
	private int p1s = 0;
	private int p2s = 0;
	private boolean p1c = false;
	private boolean p2c = false;
	private String created = "";
	private String completed = "";
	private long messageId = -1;
	private boolean processed = false;
	private boolean challenge = false;
	
	protected SetData(LeagueData league, JsonObject data) {
		this.id = ParseData.getInt(data, "id", -1);
		long p1Id = ParseData.getLong(data, "p1Id", -1);
        c1 = league.getContestantById(p1Id);
		long p2Id = ParseData.getLong(data, "p2Id", -1);
        c2 = league.getContestantById(p2Id);
		p1s = ParseData.getInt(data, "p1s", p1s);
		p2s = ParseData.getInt(data, "p2s", p2s);
		p1c = ParseData.getBoolean(data, "p1c", false);
		p2c = ParseData.getBoolean(data, "p2c", false);
		created = ParseData.getString(data, "created", created);
		completed = ParseData.getString(data, "completed", completed);
		messageId = ParseData.getLong(data, "messageId", messageId);
		processed = ParseData.getBoolean(data, "processed", false);
		challenge = ParseData.getBoolean(data, "challenge", false);
	}
	
	protected SetData(int id, Contestant c1, Contestant c2, String created) {
		this.id = id;
		this.c1 = c1;
        this.c2 = c2;
		this.created = created;
	}
	
	public JsonObject getJson() {
		JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.addProperty("p1Id", c1.getId());
		data.addProperty("p2Id", c2.getId());
		data.addProperty("p1s", p1s);
		data.addProperty("p2s", p2s);
		data.addProperty("p1c", p1c);
		data.addProperty("p2c", p2c);
		data.addProperty("created", created);
		data.addProperty("completed", completed);
		data.addProperty("messageId", messageId);
		data.addProperty("processed", processed);
		data.addProperty("challenge", challenge);
		return data;
	}
	
	/**
	 * @return this set's id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return contestant 1
	 */
	public Contestant getContestant1() {
		return c1;
	}
	
	/**
	 * @return contestant 2
	 */
	public Contestant getContestant2() {
		return c2;
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
	
	/**
	 * @return did player 1 confirm these results
	 */
	public boolean isP1confirm() {
		return p1c;
	}
	
	/**
	 * @return did player 2 confirm these results
	 */
	public boolean isP2confirm() {
		return p2c;
	}
	
	/**
	 * the date this set was created
	 * @return dd-mm-yyyy format
	 */
	public String getCreatedDate() {
		return created;
	}
	
	/**
	 * the date this set was completed
	 * @return dd-mm-yyyy format
	 */
	public String getCompletedDate() {
		return completed;
	}
	
	/**
	 * @return did both players confirm these results
	 */
	public boolean isComplete() {
		return p1c && p2c;
	}
	
	/**
	 * @return did only one player confirm these results
	 */
	public boolean isUnconfirmed() {
		return (p1c && !p2c) || (!p1c && p2c);
	}
	
	/**
	 * @return was this set processed and players scores updated
	 */
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
	
	public boolean isChallenge() {
		return challenge;
	}
	
	public void setChallenge() {
		challenge = true;
	}
	
	/**
	 * report the results of this set
	 * @param reporterId user id
	 * @param opponentId user id
	 * @param reporterScore
	 * @param opponentScore
	 * @param date dd-mm-yyyy format
	 * @return result enum
	 */
	public ReportResult report(long reporterId, long opponentId, int reporterScore, int opponentScore, String date) {
		if (isComplete()) return ReportResult.AlreadyVerified;
		System.out.println("reporter id = "+reporterId);
		System.out.println("opponent id = "+opponentId);
		if (c1.hasUserId(reporterId) && c2.hasUserId(opponentId)) {
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
		} else if (c2.hasUserId(reporterId) && c1.hasUserId(opponentId)) {
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
	
	/**
	 * override a normal players report
	 * @param id1
	 * @param id2
	 * @param score1
	 * @param score2
	 * @param date dd-mm-yyyy format
	 * @return result enum
	 */
	public ReportResult reportAdmin(long id1, long id2, int score1, int score2, String date) {
		if (isProcessed()) return ReportResult.AlreadyVerified;
		if (c1.hasUserId(id1) && c2.hasUserId(id2)) {
			p1c = p2c = true;
			completed = date;
			p1s = score1;
			p2s = score2;
			return ReportResult.SetVerified;
		} else if (c1.hasUserId(id2) && c2.hasUserId(id1)) {
			p1c = p2c = true;
			completed = date;
			p1s = score2;
			p2s = score1;
			return ReportResult.SetVerified;
		} else return ReportResult.IDsDontMatch;
	}
	
	@Override
	public String toString() {
		return id+"/"+c1.getId()+":"+p1s+"/"+c2.getId()+":"+p2s+"/"+created+"/"+completed;
	}
	
	/**
	 * @param id
	 * @return is this user id the same as p1Id or p2Id
	 */
	public boolean hasPlayer(long id) {
		return c1.hasUserId(id) || c2.hasUserId(id);
	}
	
	/**
	 * a string representation of the status of this set
	 * @return "P1 WIN", "P2 WIN", "DRAW", "UNCONFIRMED", or "ASSIGNED"
	 */
	public String getStatus() {
		if (this.isP1Win()) return "P1 WIN";
		if (this.isP2Win()) return "P2 WIN";
		if (this.isDraw()) return "DRAW";
		if (this.isUnconfirmed()) return "UNCONFIRMED";
		return "ASSIGNED";
	}
	
	/**
	 * display this set in discord
	 * @param channel the channel this set should be displayed in
	 */
	public void displaySet(TextChannel channel) {
		String p1Name = "not_in_server", p2Name = "not_in_server", date = "";
		if (isComplete()) {
            boolean validC1 = false, validC2 = false;
            for (Long id : c1.getUserIds()) {
                Member m = channel.getGuild().getMemberById(id);
                if (m != null) {
                    if (!validC1) {
                        p1Name = "";
                        validC1 = true;
                    }
                    p1Name += "*"+m.getEffectiveName()+"*";
                }
            }
            for (Long id : c2.getUserIds()) {
                Member m = channel.getGuild().getMemberById(id);
                if (m != null) {
                    if (!validC2) {
                        p2Name = "";
                        validC2 = true;
                    }
                    p2Name += "*"+m.getEffectiveName()+"*";
                }
            }
			date = completed;
		} else {
            p1Name = ""; p2Name = "";
            for (Long id : c1.getUserIds()) p1Name += "<@"+id+">";
            for (Long id : c2.getUserIds()) p2Name += "<@"+id+">";
			date = created;
		}
		String challengeMark = "";
		if (isChallenge()) challengeMark = "!";
		MessageCreateData mcd = new MessageCreateBuilder()
				.addContent("__**ID:"+getId()+challengeMark+"**__ ")
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
				channel.editMessageById(messageId, med).complete();
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
	
	/**
	 * remove this set from being displayed in discord
	 * @param channel the channel this set should be removed from
	 */
	public boolean removeSetDisplay(TextChannel channel) {
		try { channel.deleteMessageById(messageId).queue(); }
		catch (ErrorResponseException e) { return false; }
		messageId = -1;
		return true;
	}
	
	/**
	 * if this set is complete and not processed yet, update the scores of the users in this set
	 * @param league the league data this set was played in
	 */
	public void processSet(LeagueData league) {
		if (!isComplete() || processed) return;
		int change = (int)getChangeInScore(p1s, p2s, c1.getScore(), c2.getScore(), league.getK());
		c1.changeScore(change);
		c2.changeScore(-change);
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
