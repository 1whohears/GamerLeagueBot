package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Challenge extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return false;
	}

	@Override
	public String getCommandString() {
		return "challenge";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [p2 ping]' request to challenge this player";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 2) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		// TODO challenge command
		event.getChannel().sendMessage(Important.getError()+" This command doesn't do anything yet!").queue();
		return true;
	}

}
