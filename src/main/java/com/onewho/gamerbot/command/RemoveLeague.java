package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.Important;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveLeague extends AdminCommand {

	@Override
	public String getCommandString() {
		return "removeleague";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [league name]`";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (!super.runCommand(event, params)) return false;
		// TODO remove league command (send user a backup json in system messages channel)
		event.getChannel().sendMessage(Important.getError()+" This Command Doesn't do anything yet!").queue();
		return true;
	}

}
