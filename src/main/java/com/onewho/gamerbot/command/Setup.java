package com.onewho.gamerbot.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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
		//setup roles
		Role gamerRole = null;
		if (guildData.get("league role id") == null) {
			gamerRole = guild.createRole().complete();
			guildData.addProperty("league role id", gamerRole.getIdLong());
		} else gamerRole = guild.getRoleById(guildData.get("league role id").getAsLong());
		gamerRole.getManager()
			.setName("GAMERS")
			.setColor(Color.CYAN)
			.complete();
		//setup category
		Category gamerCat = null;
		if (guildData.get("league category id") == null) {
			gamerCat = guild.createCategory("Gamer League").complete();
			guildData.addProperty("league category id", gamerCat.getIdLong());
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
		setupChannel("set-history", gamerCat, guild, guildData);
		setupChannel("ranks", gamerCat, guild, guildData);
		setupChannel("pairings", gamerCat, guild, guildData);
		perm1.add(Permission.MESSAGE_SEND);
		perm1.add(Permission.MESSAGE_ADD_REACTION);
		commandsChannel.getManager()
			.putRolePermissionOverride(gamerRole.getIdLong(), perm1, null)
			.complete();
		perm2.add(Permission.MESSAGE_ADD_REACTION);
		optionsChannel.getManager()
			.putRolePermissionOverride(gamerRole.getIdLong(), perm2, null)
			.complete();
		perm1.clear();
		perm2.clear();
		//setup options channel
		setupOptions(optionsChannel, guildData);
		//finish
		LeagueData.saveData();
		System.out.println("setup command complete");
		event.getChannel().sendMessage("Bot Channel Setup Complete!").queue();
		return true;
	}
	
	private TextChannel setupChannel(String name, Category cat, Guild guild, JsonObject data) {
		TextChannel channel = null;
		if (data.get(name+" id") == null) {
			channel = cat.createTextChannel(name).complete();
			data.addProperty(name+" id", channel.getIdLong());
		} else channel = guild.getTextChannelById(data.get(name+" id").getAsLong());
		channel.getManager().sync(cat.getPermissionContainer()).complete();
		return channel;
	}
	
	private void setupOptions(TextChannel channel, JsonObject data) {
		if (data.get("join league option id") == null) {
			EmbedBuilder jleb = new EmbedBuilder();
			jleb.setTitle("Join this Server's Gamer League?");
			jleb.setColor(Color.GREEN);
			jleb.setDescription("You will be pinged often and must complete your assigned matches!");
			Button join = Button.success("join-gamer-league", "Join");
			Button quit = Button.danger("quit-gamer-league", "Quit");
			MessageCreateData jlc = new MessageCreateBuilder()
					.addEmbeds(jleb.build())
					.addActionRow(Arrays.asList(join, quit))
					.build();
			Message jlb = channel.sendMessage(jlc).complete();
			data.addProperty("join league option id", jlb.getIdLong());
			jlb.addReaction(Emoji.fromUnicode("U+2714"));
		}
		
		LeagueData.saveData();
	}

}