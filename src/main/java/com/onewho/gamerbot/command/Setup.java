package com.onewho.gamerbot.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

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
		GuildData guildData = LeagueData.getGuildDataById(guild.getIdLong());
		//setup roles
		Role gamerRole = null;
		if (guildData.getLeagueRoleId() == -1) {
			gamerRole = guild.createRole().complete();
			guildData.setLeagueRoleId(gamerRole.getIdLong());
		} else gamerRole = guild.getRoleById(guildData.getLeagueRoleId());
		gamerRole.getManager()
			.setName("GAMERS")
			.setColor(Color.CYAN)
			.queue();
		//setup category
		Category gamerCat = null;
		if (guildData.getLeagueCategoryId() == -1) {
			gamerCat = guild.createCategory("Gamer League").complete();
			guildData.setLeagueCategoryId(gamerCat.getIdLong());
		} else gamerCat = guild.getCategoryById(guildData.getLeagueCategoryId());
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
	
	private TextChannel setupChannel(String name, Category cat, Guild guild, GuildData data) {
		TextChannel channel = null;
		if (data.getChannelId(name) == -1) {
			channel = cat.createTextChannel(name).complete();
			data.setChannelId(name, channel.getIdLong());
		} else {
			// TODO catch error if channel no longer exists
			channel = guild.getTextChannelById(data.getChannelId(name));
		}
		channel.getManager().sync(cat.getPermissionContainer()).complete();
		return channel;
	}
	
	private void setupOptions(TextChannel channel, GuildData data) {
		if (data.getJoinLeagueOptionId() == -1) {
			MessageCreateData jlc = new MessageCreateBuilder()
					.addEmbeds(getJLEmbed())
					.addActionRow(getJLButtons())
					.build();
			Message jlb = channel.sendMessage(jlc).complete();
			data.setJoinLeagueOptionId(jlb.getIdLong());
		} else {
			MessageEditData jle = new MessageEditBuilder()
					.setEmbeds(getJLEmbed())
					.setActionRow(getJLButtons())
					.build();
			channel.editMessageById(data.getJoinLeagueOptionId(), jle).complete();
		}
		int max = data.getMaxSetsPerWeek();
		if (data.getSetsaweekOptionId() == -1) {
			MessageCreateData swc = new MessageCreateBuilder()
					.addEmbeds(getSWEmbed())
					.addActionRow(getSWButtons(max))
					.build();
			Message swb = channel.sendMessage(swc).complete();
			data.setSetsaweekOptionId(swb.getIdLong());
		} else {
			MessageEditData swe = new MessageEditBuilder()
					.setEmbeds(getSWEmbed())
					.setActionRow(getSWButtons(max))
					.build();
			channel.editMessageById(data.getSetsaweekOptionId(), swe).complete();
		}
	}
	
	private MessageEmbed getJLEmbed() {
		EmbedBuilder jleb = new EmbedBuilder();
		jleb.setTitle("Join this Server's Gamer League?");
		jleb.setColor(Color.GREEN);
		jleb.setDescription("You will be pinged often and must complete your assigned matches!");
		return jleb.build();
	}
	
	private List<Button> getJLButtons() {
		Button join = Button.success("join-gamer-league", "Join");
		Button quit = Button.danger("quit-gamer-league", "Quit");
		return Arrays.asList(join, quit);
	}
	
	private MessageEmbed getSWEmbed() {
		EmbedBuilder sweb = new EmbedBuilder();
		sweb.setTitle("Sets Per Week");
		sweb.setColor(Color.BLUE);
		sweb.setDescription("Most amount of sets you can do next week?");
		return sweb.build();
	}
	
	private List<Button> getSWButtons(int max) {
		List<Button> bs = new ArrayList<Button>();
		for (int i = 0; i <= max; ++i) bs.add(Button.primary("setsaweek-"+i, i+""));
		return bs;
	}

}
