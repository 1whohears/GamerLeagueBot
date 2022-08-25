package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	
	public String getCommandString();
	
	public boolean runCommand(MessageReceivedEvent event);
	
}
