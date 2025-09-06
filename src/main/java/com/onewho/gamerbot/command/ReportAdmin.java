package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.ReportResult;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ReportAdmin extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
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
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [set id] [p1 ping] [p1 score] [p2 ping] [p2 score]`"
				+ " override the results for these user's sets.";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 6) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		int id = -1, s1 = -1, s2 = -1;
		try {
			id = Integer.parseInt(params[1]);
			s1 = Integer.parseInt(params[3]);
			s2 = Integer.parseInt(params[5]);
		} catch (NumberFormatException e) {
		}
		long pingId1 = getIdFromMention(params[2]);
		long pingId2 = getIdFromMention(params[4]);
		if (id == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a number!").queue();
			return false;
		} else if (s1 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[3]+" is not a number!").queue();
			return false;
		} else if (s2 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[5]+" is not a number!").queue();
			return false;
		} else if (pingId1 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a mention!").queue();
			return false;
		} else if (pingId2 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[4]+" is not a mention!").queue();
			return false;
		}
		SetData set = ldata.getSetDataById(id);
		if (set == null) {
			event.getChannel().sendMessage(Important.getError()+" The set with id "+id+" does not exist!").queue();
			return false;
		}
		String currentDate = UtilCalendar.getCurrentDateString();
		ReportResult result = set.reportAdmin(pingId1, pingId2, s1, s2, currentDate);
		if (result == ReportResult.IDsDontMatch) {
			event.getChannel().sendMessage(Important.getError()+" This set id does not have those players!").queue();
			return false;
		} else if (result == ReportResult.SetVerified) {
			set.getContestant1().setLastActive(currentDate);
            set.getContestant2().setLastActive(currentDate);
			event.getChannel().sendMessage("Admin Override Successful!").queue();
		} else if (result == ReportResult.AlreadyVerified) {
			event.getChannel().sendMessage("This set has already been processed"
					+ " and the scores have been updated. You must use a backup of this"
					+ " server's league data to go back before these sets were processed!").queue();
			return true;
		}
		//display new sets
		TextChannel pairsChannel = event.getGuild().getChannelById(TextChannel.class, 
				ldata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		GlobalData.saveData();
		return true;
	}
	
}
