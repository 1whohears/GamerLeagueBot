package com.onewho.gamerbot.command;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.Permission;
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
			if (c.getCommandString().equals(command[0])) {
				//System.out.println("command "+c.getCommandString()+" requires channel name "+c.getRequiredChannelName()+" and admin? "+c.getNeedsAdmin());
				if (c.getRequiredChannelName() != null && !event.getChannel().getName().equals(c.getRequiredChannelName())) return true;
				if (c.getNeedsAdmin() && !event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
					event.getChannel().sendMessage("That command requires admin permission to use!").queue();
					return true;
				} 
				c.runCommand(event, command);
				return true;
			}
		}
		return false;
	}
	
	public static void loadCommands() {
		commands = new ArrayList<ICommand>();
		commands.add(new Help());
		commands.add(new Setup());
		commands.add(new Reload());
		commands.add(new Config());
		commands.add(new GenPairs());
		commands.add(new Report());
		commands.add(new ReportAdmin());
		commands.add(new UpdateRanks());
		commands.add(new Backup());
		commands.add(new ReadBackup());
		commands.add(new RefreshSet());
		commands.add(new CreateSet());
	}
	
}
