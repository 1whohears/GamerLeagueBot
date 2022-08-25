package com.onewho.gamerbot.command;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Setup implements ICommand {
	
	@Override
	public boolean getNeedsAdmin() {
		return true;
	}
	
	@Override
	public String getCommandString() {
		return "setup";
	}
	
	@Override
	public String getRequiredChannelName() {
		return null;
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		System.out.println("running setup command");
		Guild guild = event.getGuild();
		List<GuildChannel> channels = guild.getChannels();
		if (!doesChannelExist("bot-commands", channels)) guild.createTextChannel("bot-commands").queue();
		// this experiment shows guild.getChannels() does not get updated by guild.createTextChannel(...).queue()
		//if (!doesChannelExist("bot-commands", channels)) guild.createTextChannel("bot-commands").queue();
		
		event.getChannel().sendMessage("Bot Channel Setup Complete!").queue();
		System.out.println("setup command complete");
		return true;
	}
	
	private boolean doesChannelExist(String name, List<GuildChannel> channels) {
		for (GuildChannel c : channels) if (c.getName().equals(name)) return true;
		return false;
	}

}
