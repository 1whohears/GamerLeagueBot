package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class NewSeason extends LeagueCommand {
	
	@Override
	public boolean getNeedsAdmin() {
		return true;
	}
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}
	
	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getCommandString() {
		return "newseason";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` manually check to start a new season. use with caution!";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 1) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		event.getChannel().sendMessage("Running Start New Season Function...").queue();
		ldata.startNewSeason(event.getGuild(), event.getChannel());
		return true;
	}
	
	

}
