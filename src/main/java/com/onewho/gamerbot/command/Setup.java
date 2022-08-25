package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Setup implements ICommand {

	@Override
	public String getCommandString() {
		return "setup";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event) {
		
		return false;
	}

}
