package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.ReportResult;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReportAdmin implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "reportadmin";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 6) {
			event.getChannel().sendMessage("WRONG! do: ~report [id] [p1 ping] [p1 score] [p2 ping] [p2 score]").queue();
			return true;
		}
		int id = -1, s1 = -1, s2 = -1;
		long pingId1 = -1, pingId2 = -1;
		if (checkIfMention(params[2])) {
			event.getChannel().sendMessage("BRUH! "+params[2]+" is not a mention!").queue();
			return true;
		}
		if (checkIfMention(params[4])) {
			event.getChannel().sendMessage("BRUH! "+params[4]+" is not a mention!").queue();
			return true;
		}
		String pingS1 = params[2].substring(2, params[2].length()-1);
		String pingS2 = params[4].substring(2, params[4].length()-1);
		try {
			id = Integer.parseInt(params[1]);
			s1 = Integer.parseInt(params[3]);
			s2 = Integer.parseInt(params[5]);
			pingId1 = Long.parseLong(pingS1);
			pingId2 = Long.parseLong(pingS2);
		} catch (NumberFormatException e) {
		}
		if (id == -1) {
			event.getChannel().sendMessage("IDIOT! "+params[1]+" is not a number!").queue();
			return true;
		} else if (s1 == -1) {
			event.getChannel().sendMessage("UHHGGG! "+params[3]+" is not a number!").queue();
			return true;
		} else if (s2 == -1) {
			event.getChannel().sendMessage("WHY!? "+params[5]+" is not a number!").queue();
			return true;
		} else if (pingId1 == -1) {
			event.getChannel().sendMessage("OUCH! you didn't mention/ping player 1 correctly!").queue();
			return true;
		} else if (pingId2 == -1) {
			event.getChannel().sendMessage("YIKES! you didn't mention/ping player 2 correctly!").queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		SetData set = gdata.getSetDataById(id);
		if (set == null) {
			event.getChannel().sendMessage("INCORRECT! The set with id "+id+" does not exist!").queue();
			return true;
		}
		ReportResult result = set.reportAdmin(pingId1, pingId2, s1, s2, UtilCalendar.getCurrentDateString());
		if (result == ReportResult.IDsDontMatch) {
			event.getChannel().sendMessage("LOL! This set id does not have those players!").queue();
			return true;
		} else if (result == ReportResult.SetVerified) {
			event.getChannel().sendMessage("Admin Override Successful!").queue();
		} else if (result == ReportResult.AlreadyVerified) {
			event.getChannel().sendMessage("This set has already been processed"
					+ " and the scores have been updated. You must use a backup of this"
					+ " server's league data to go back before these sets were processed!").queue();
			return true;
		}
		//display new sets
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		LeagueData.saveData();
		return true;
	}
	
	private boolean checkIfMention(String m) {
		return m.length() > 10 && m.charAt(0) == '<' && m.charAt(1) == '@' && m.charAt(m.length()-1) == '>';
	}
	
}