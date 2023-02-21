package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class AdminCommand implements ICommand {
	
	@Override
	public boolean getNeedsAdmin() {
		return true;
	}
	
	@Override
	public boolean getNeedsTO() {
		return false;
	}
	
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		Member member = event.getMember();
		if (!member.hasPermission(Permission.ADMINISTRATOR)) {
			event.getChannel().sendMessage("That command requires admin permission to use!").queue();
			return false;
		}
		return true;
	}

}
