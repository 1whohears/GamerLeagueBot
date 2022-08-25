package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help implements ICommand {
	
	@Override
	public boolean getNeedsAdmin() {
		return false;
	}
	
	@Override
	public String getCommandString() {
		return "help";
	}
	
	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		event.getChannel().sendMessage("LMAO!!!").queue();
		return true;
	}

}
