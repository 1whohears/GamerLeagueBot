package com.onewho.gamerbot.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
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
		JsonObject guildData = LeagueData.getGuildDataById(guild.getIdLong());
		// TODO use guild data to get roles and channels by id
		//setup roles
		Role gamerRole = null;
		if (guildData.get("league role id") == null) {
			gamerRole = guild.createRole().complete();
			guildData.addProperty("league role id", gamerRole.getIdLong());
			LeagueData.saveData();
		} else gamerRole = guild.getRoleById(guildData.get("league role id").getAsLong());
		gamerRole.getManager()
			.setName("gamers")
			.setColor(Color.CYAN)
			.complete();
		//setup category
		Category gamerCat = null;
		if (guildData.get("league category id") == null) {
			gamerCat = guild.createCategory("Gamer League").complete();
			guildData.addProperty("league category id", gamerCat.getIdLong());
			LeagueData.saveData();
		} else gamerCat = guild.getCategoryById(guildData.get("league category id").getAsLong());
		Collection<Permission> perm1 = new ArrayList<Permission>();
		Collection<Permission> perm2 = new ArrayList<Permission>();
		perm1.add(Permission.MESSAGE_HISTORY);
		perm2.add(Permission.MESSAGE_SEND);
		perm2.add(Permission.MESSAGE_ADD_REACTION);
		gamerCat.getManager()
			.putRolePermissionOverride(guild.getBotRole().getIdLong(), perm1, null)
			.putRolePermissionOverride(guild.getBotRole().getIdLong(), perm2, null)
			.putRolePermissionOverride(guild.getPublicRole().getIdLong(), perm1, perm2)
			.complete();
		perm1.clear();
		perm2.clear();
		//setup channels
		TextChannel commandsChannel = setupChannel("bot-commands", gamerCat, guild, guildData);
		TextChannel optionsChannel = setupChannel("options", gamerCat, guild, guildData);
		TextChannel historyChannel = setupChannel("set-history", gamerCat, guild, guildData);
		TextChannel ranksChannel = setupChannel("ranks", gamerCat, guild, guildData);
		TextChannel pairingsChannel = setupChannel("pairings", gamerCat, guild, guildData);
		perm1.add(Permission.MESSAGE_SEND);
		perm1.add(Permission.MESSAGE_ADD_REACTION);
		commandsChannel.getManager()
			.sync(gamerCat.getPermissionContainer())
			.putRolePermissionOverride(gamerRole.getIdLong(), perm1, null)
			.complete();
		perm2.add(Permission.MESSAGE_ADD_REACTION);
		optionsChannel.getManager()
			.sync(gamerCat.getPermissionContainer())
			.putRolePermissionOverride(gamerRole.getIdLong(), perm2, null)
			.complete();
		perm1.clear();
		perm2.clear();
		historyChannel.getManager().sync(gamerCat.getPermissionContainer()).complete();
		ranksChannel.getManager().sync(gamerCat.getPermissionContainer()).complete();
		pairingsChannel.getManager().sync(gamerCat.getPermissionContainer()).complete();
		//finish
		System.out.println("setup command complete");
		event.getChannel().sendMessage("Bot Channel Setup Complete!").queue();
		return true;
	}
	
	private TextChannel setupChannel(String name, Category cat, Guild guild, JsonObject data) {
		TextChannel channel = null;
		if (data.get(name+" id") == null) {
			channel = cat.createTextChannel(name).complete();
			data.addProperty(name+" id", channel.getIdLong());
			LeagueData.saveData();
		} else channel = guild.getTextChannelById(data.get(name+" id").getAsLong());
		return channel;
	}

}
