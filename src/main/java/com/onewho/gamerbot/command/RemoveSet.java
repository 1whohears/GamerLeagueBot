package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.Important;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveSet implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return false;
	}

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "removeset";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [set id]` remove this set.";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		// TODO remove set command
		event.getChannel().sendMessage(Important.getError()+" This Command Doesn't do anything yet!").queue();
		return true;
	}

}
