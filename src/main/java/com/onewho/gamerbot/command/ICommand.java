package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	
	public boolean getNeedsAdmin();
	
	public String getCommandString();
	
	public String getRequiredChannelName();
	
	public boolean runCommand(MessageReceivedEvent event, String[] params);
	
}
