package com.onewho.gamerbot.command;

import java.util.List;

import net.dv8tion.jda.api.entities.Category;
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
		if (guild.getCategoriesByName("Gamer League", true).size() == 0) guild.createCategory("Gamer League").complete();
		Category gamerCat = guild.getCategoriesByName("Gamer League", true).get(0);
		if (!doesChannelExist("bot-commands", channels)) guild.createTextChannel("bot-commands", gamerCat).complete();
		if (!doesChannelExist("options", channels)) guild.createTextChannel("options", gamerCat).complete();
		if (!doesChannelExist("set-history", channels)) guild.createTextChannel("set-history", gamerCat).complete();
		if (!doesChannelExist("ranks", channels)) guild.createTextChannel("ranks", gamerCat).complete();
		if (!doesChannelExist("pairings", channels)) guild.createTextChannel("pairings", gamerCat).complete();
		
		channels = guild.getChannels();
		
		System.out.println("setup command complete");
		event.getChannel().sendMessage("Bot Channel Setup Complete!").queue();
		return true;
	}
	
	private boolean doesChannelExist(String name, List<GuildChannel> channels) {
		for (GuildChannel c : channels) if (c.getName().equals(name)) return true;
		return false;
	}
	
	private GuildChannel getChannelByName(String name, List<GuildChannel> channels) {
		for (GuildChannel c : channels) if (c.getName().equals(name)) return c;
		return null;
	}

}
