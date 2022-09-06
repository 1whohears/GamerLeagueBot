package com.onewho.gamerbot.command;

import java.util.ArrayList;
import java.util.List;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandParser {
	
	public static List<ICommand> commands;
	
	public static boolean parseCommand(MessageReceivedEvent event) {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return true;
		if (commands == null) loadCommands();
		String text = event.getMessage().getContentRaw();
		if (text.length() < 2) return true;
		String[] command = text.substring(1, text.length()).split(" ");
		//System.out.println("command received: "+command[0]+" in channel "+event.getChannel().getName());
		//System.out.println("the user "+event.getMember().getNickname()+" has admin? "+event.getMember().hasPermission(Permission.ADMINISTRATOR));
		for (ICommand c : commands) {
			if (!c.getCommandString().equals(command[0])) continue;
			//System.out.println("command "+c.getCommandString()+" requires channel name "+c.getRequiredChannelName()+" and admin? "+c.getNeedsAdmin());
			if (c.getRequiredChannelName() != null && !event.getChannel().getName().equals(c.getRequiredChannelName())) return true;
			boolean admin = event.getMember().hasPermission(Permission.ADMINISTRATOR);
			if (c.getNeedsAdmin() && !admin) {
				event.getChannel().sendMessage("That command requires admin permission to use!").queue();
				return true;
			}
			if (c.getNeedsTO() && !admin) {
				Guild guild = event.getGuild();
				GuildData gdata = GlobalData.createGuildData(guild.getIdLong());
				if (gdata == null) {
					event.getChannel().sendMessage("That command requires tournament organizer role to use in a league!").queue();
					return true;
				}
				LeagueData ldata = gdata.getLeagueByChannel(event.getChannel());
				if (ldata == null) {
					event.getChannel().sendMessage("That command requires tournament organizer role to use in a league!").queue();
					return true;
				}
				Role toRole = guild.getRoleById(ldata.getToRoleId());
				if (toRole == null) {
					event.getChannel().sendMessage("This league doesn't have a TO role."
							+ " Please use the `setup` command!").queue();
					return true;
				}
				if (event.getMember().getRoles().contains(toRole)) {
					event.getChannel().sendMessage("That command requires tournament organizer role to use!").queue();
					return true;
				}
			}
			c.runCommand(event, command);
			return true;
		}
		return false;
	}
	
	public static void loadCommands() {
		commands = new ArrayList<ICommand>();
		commands.add(new Help());
		commands.add(new Setup());
		commands.add(new Config());
		commands.add(new GenPairs());
		commands.add(new Report());
		commands.add(new ReportAdmin());
		commands.add(new UpdateRanks());
		commands.add(new Backup());
		commands.add(new ReadBackup());
		commands.add(new CreateSet());
		commands.add(new CreateLeague());
		//commands.add(new Reload()); // for testing
		//commands.add(new RefreshSet()); // for testing
	}
	
	private static List<ICommand> userCommands;
	private static List<ICommand> toCommands;
	private static List<ICommand> adminCommands;
	
	public static List<ICommand> getUserCommands() {
		if (userCommands == null) {
			userCommands = new ArrayList<ICommand>();
			for (ICommand c : commands) 
				if (!c.getNeedsTO() && !c.getNeedsAdmin()) 
					userCommands.add(c);
		}
		return userCommands;
	}
	
	public static List<ICommand> getTOCommands() {
		if (toCommands == null) {
			toCommands = new ArrayList<ICommand>();
			for (ICommand c : commands) 
				if (c.getNeedsTO() && !c.getNeedsAdmin()) 
					toCommands.add(c);
		}
		return toCommands;
	}
	
	public static List<ICommand> getAdminCommands() {
		if (adminCommands == null) {
			adminCommands = new ArrayList<ICommand>();
			for (ICommand c : commands) 
				if (c.getNeedsAdmin()) 
					adminCommands.add(c);
		}
		return adminCommands;
	}
	
}
