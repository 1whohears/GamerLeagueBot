package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help implements ICommand {

	@Override
	public String getCommandString() {
		return "help";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event) {
		event.getChannel().sendMessage("LMAO!!!").queue();
		return true;
	}

}
