package com.onewho.gamerbot.command;

import java.util.concurrent.TimeUnit;

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
		event.getChannel().sendMessage("hmmmm....").queue();
		event.getChannel().sendMessage("after some serious consideration...").queueAfter(2, TimeUnit.SECONDS);
		event.getChannel().sendMessage("I think your problem is...").queueAfter(5, TimeUnit.SECONDS);
		event.getChannel().sendMessage("I forgot").queueAfter(10, TimeUnit.SECONDS);
		event.getChannel().sendMessage("sorry!").queueAfter(11, TimeUnit.SECONDS);
		return true;
	}

}
