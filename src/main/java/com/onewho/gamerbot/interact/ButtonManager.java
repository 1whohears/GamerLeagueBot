package com.onewho.gamerbot.interact;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.ReportResult;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.util.UtilCalendar;
import com.onewho.gamerbot.util.UtilUsers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonManager {
	
	public static void handleButton(ButtonInteractionEvent event) {
		LeagueData data = GlobalData.getGuildDataById(event.getGuild().getIdLong())
				.getLeagueByChannel(event.getChannel());
		if (data == null) return;
		String id = event.getButton().getId();
		if (id.startsWith("setsaweek-")) {
			int sets = Integer.parseInt(id.substring("setsaweek-".length(), id.length()));
			String message = UtilUsers.userSetsAWeek(event.getGuild(), event.getUser(), event.getChannel(), sets);
			event.reply(message).setEphemeral(true).queue();
			return;
		}
		switch (id) {
		case "join-gamer-league":
			handleJoinButton(event, event.getGuild(), event.getUser(), data);
			break;
		case "quit-gamer-league":
			handleLeaveButton(event, event.getGuild(), event.getUser(), data);
			break;
		case "report-verify":
			handleReportVerifyButton(event);
			break;
		case "report-dispute":
			handleReportDisputeButton(event);
			break;
		}
	}
	
	private static void handleJoinButton(ButtonInteractionEvent event, Guild guild, User user, LeagueData data) {
		event.reply(
			data.addUser(guild, user))
				.setEphemeral(true).queue();
	}
	
	private static void handleLeaveButton(ButtonInteractionEvent event, Guild guild, User user, LeagueData data) {
		event.reply(
			UtilUsers.userQuitLeague(guild, user, event.getChannel()))
				.setEphemeral(true).queue();
	}
	
	private static void handleSetsPerWeekButton(ButtonInteractionEvent event) {
		
		GlobalData.saveData();
	}
	
	private static void handleReportVerifyButton(ButtonInteractionEvent event) {
		int setId = getSetId(event);
		SetData set = GlobalData.getGuildDataById(event.getGuild().getIdLong())
				.getLeagueByChannel(event.getChannel()).getSetDataById(setId);
		long id1 = -1, id2 = -1;
		int s1 = -1, s2 = -1;
		if (set.isP1confirm()) {
			id1 = set.getP2Id();
			id2 = set.getP1Id();
			s1 = set.getP2score();
			s2 = set.getP1score();
		} else if (set.isP2confirm()) {
			id2 = set.getP2Id();
			id1 = set.getP1Id();
			s2 = set.getP2score();
			s1 = set.getP1score();
		}
		if (id1 != event.getUser().getIdLong()) {
			event.reply("You can't press this button!").setEphemeral(true).queue();
			return;
		}
		ReportResult result = set.report(id1, id2, s1, s2, UtilCalendar.getCurrentDateString());
		if (result == ReportResult.AlreadyVerified) {
			event.reply("You already verified this set!").setEphemeral(true).queue();
			return;
		}
		event.reply("The set has been verified!").queue();
		Guild guild = event.getGuild();
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, 
				GlobalData.getGuildDataById(guild.getIdLong())
					.getLeagueByChannel(event.getChannel()).getChannelId("pairings"));
		set.displaySet(pairsChannel);
		GlobalData.saveData();
	}
	
	private static void handleReportDisputeButton(ButtonInteractionEvent event) {
		int setId = getSetId(event);
		SetData set = GlobalData.getGuildDataById(event.getGuild().getIdLong())
				.getLeagueByChannel(event.getChannel()).getSetDataById(setId);
		if (set.isComplete()) {
			event.reply("This set has already been verified!"
					+ " If you still dispute the results then contact an admin!").setEphemeral(true).queue();
			return;
		}
		long id1 = -1;
		if (set.isP1confirm()) id1 = set.getP2Id();
		else if (set.isP2confirm()) id1 = set.getP1Id();
		if (id1 != event.getUser().getIdLong()) {
			event.reply("You can't press this button!").setEphemeral(true).queue();
			return;
		}
		event.reply("The set has been disputed!"
				+ " The origional reporter needs to change their report or an admin should be contacted!").queue();
	}
	
	private static int getSetId(ButtonInteractionEvent event) {
		String disc = event.getMessage().getEmbeds().get(0).getDescription();
		String setIdPrefix = "SET ID [";
		String setIdString = disc.substring(disc.indexOf(setIdPrefix)+setIdPrefix.length(), disc.indexOf(']'));
		System.out.println("setIdString = "+setIdString);
		return Integer.parseInt(setIdString); 
	}
	
}
