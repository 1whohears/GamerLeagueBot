package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Config implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "config";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		
		return true;
	}

}
