package com.onewho.gamerbot.command;

import java.util.List;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
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
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` does this";
	}
	
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		Role toRole = event.getGuild().getRoleById(ldata.getToRoleId());
		boolean to = false;
		if (toRole != null) to = event.getMember().getRoles().contains(toRole);
		boolean admin = event.getMember().hasPermission(Permission.ADMINISTRATOR);
		helpAll(event.getChannel());
		if (to || admin) helpTO(event.getChannel());
		if (admin) helpAdmin(event.getChannel());
		return true;
	}
	
	private void helpAll(MessageChannelUnion channel) {
		String help = "__**All Users Commands**__";
		List<ICommand> commands = CommandParser.getUserCommands();
		for (ICommand c : commands) help += "\n"+c.getHelp(); 
		channel.sendMessage(help).queue();;
	}
	
	private void helpTO(MessageChannelUnion channel) {
		String help = "__**TO Commands**__";
		List<ICommand> commands = CommandParser.getTOCommands();
		for (ICommand c : commands) help += "\n"+c.getHelp(); 
		channel.sendMessage(help).queue();;
	}
	
	private void helpAdmin(MessageChannelUnion channel) {
		String help = "__**Admin Commands**__";
		List<ICommand> commands = CommandParser.getAdminCommands();
		for (ICommand c : commands) help += "\n"+c.getHelp(); 
		channel.sendMessage(help).queue();
	}

}
