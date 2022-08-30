package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.ReportResult;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Report implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return false;
	}

	@Override
	public String getCommandString() {
		return "report";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 5) {
			// TODO make insults random
			event.getChannel().sendMessage("WRONG! do: ~report [id] [your score] [opponent score] [opponent ping]").queue();
			return true;
		}
		int id = -1, s1 = -1, s2 = -1;
		long pingId = -1;
		if (checkIfMention(params[4])) {
			event.getChannel().sendMessage("BRUH! "+params[4]+" is not a mention!").queue();
			return true;
		}
		String pingString = params[4].substring(2, params[4].length()-1);
		try {
			id = Integer.parseInt(params[1]);
			s1 = Integer.parseInt(params[2]);
			s2 = Integer.parseInt(params[3]);
			pingId = Long.parseLong(pingString);
		} catch (NumberFormatException e) {
		}
		if (id == -1) {
			event.getChannel().sendMessage("IDIOT! "+params[1]+" is not a number!").queue();
			return true;
		} else if (s1 == -1) {
			event.getChannel().sendMessage("UHHGGG! "+params[2]+" is not a number!").queue();
			return true;
		} else if (s2 == -1) {
			event.getChannel().sendMessage("WHY!? "+params[3]+" is not a number!").queue();
			return true;
		} else if (pingId == -1) {
			event.getChannel().sendMessage("DONKEYKONG? you didn't mention/ping your opponent correctly!").queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		SetData set = gdata.getSetDataById(id);
		if (set == null) {
			event.getChannel().sendMessage("INCORRECT! The set with id "+id+" does not exist!").queue();
			return true;
		}
		ReportResult result = set.report(event.getAuthor().getIdLong(), pingId, s1, s2, UtilCalendar.getCurrentDateString());
		switch (result) {
		case IDsDontMatch:
			event.getChannel().sendMessage("WOW! This set id does not have those players!").queue();
			break;
		case ScoreConflict:
			event.getChannel().sendMessage("This conflicts with the score that your opponent reported! "
					+ "If you are correct have your opponent report again, or get a hold of an admin.").queue();
			break;
		case SetVerified:
			event.getChannel().sendMessage("Set reported and verified by opponent!").queue();
			break;
		case WaitingForOpponent:
			event.getChannel().sendMessage("Set reported. Waiting for opponent to verify!").queue();
			break;
		case AlreadyVerified:
			event.getChannel().sendMessage("This set has already been verified. Admin required to update.").queue();
			break;
		}
		//display new sets
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("pairings"));
		gdata.displaySetsByDate(set.getCreatedDate(), pairsChannel);
		LeagueData.saveData();
		return true;
	}
	
	private boolean checkIfMention(String m) {
		return m.length() > 10 && m.charAt(0) == '<' && m.charAt(1) == '@' && m.charAt(m.length()-1) == '>';
	}

}
