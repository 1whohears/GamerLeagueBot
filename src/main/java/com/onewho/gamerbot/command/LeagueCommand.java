package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class LeagueCommand implements ICommand {
	
	public boolean getNeedsAdmin() {
		return false;
	}
	
	public abstract String getRequiredChannelName();
	
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (getRequiredChannelName() != null 
				&& !event.getChannel().getName().equals(getRequiredChannelName())) 
			return false;
		Guild guild = event.getGuild();
		MessageChannelUnion channel = event.getChannel();
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		if (gdata == null) {
			channel.sendMessage("No leagues were found in this server.").queue();
			return false;
		}
		LeagueData ldata = gdata.getLeagueByChannel(event.getChannel());
		if (ldata == null) {
			channel.sendMessage("This is not a valid league.").queue();
			return false;
		}
		Member member = event.getMember();
		if (getNeedsTO() && !member.hasPermission(Permission.ADMINISTRATOR)) {
			Role toRole = guild.getRoleById(ldata.getToRoleId());
			if (toRole == null) {
				event.getChannel().sendMessage("This league doesn't have a TO role."
						+ " Please use the `setup` command!").queue();
				return false;
			}
			if (!event.getMember().getRoles().contains(toRole)) {
				event.getChannel().sendMessage("That command requires tournament organizer"
						+ " role to use!").queue();
				return false;
			}
		}
		return runCommand(event, params, gdata, ldata);
	}
	
	public abstract boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata);
	
}
