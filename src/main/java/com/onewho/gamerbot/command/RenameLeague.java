package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RenameLeague implements ICommand {

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
		return "renameleague";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` ";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		
		return true;
	}

}
