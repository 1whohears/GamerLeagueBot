package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.Important;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveLeague implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "removeleague";
	}

	@Override
	public String getRequiredChannelName() {
		return null;
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [league name]`";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		// TODO remove league command (send user a backup json in system messages channel)
		event.getChannel().sendMessage(Important.getError()+" This Command Doesn't do anything yet!").queue();
		return true;
	}

}
