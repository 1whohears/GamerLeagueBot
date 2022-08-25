package com.onewho.gamerbot.command;

import java.util.ArrayList;
import java.util.List;

import com.onewho.gamerbot.BotMain;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandParser {
	
	public static List<ICommand> commands;
	
	public static void parseCommand(MessageReceivedEvent event) {
		if (commands == null) loadCommands();
		String text = event.getMessage().getContentRaw();
		if (text.length() < 2) {
			commandDoesntExist(event);
			return;
		}
		String[] command = text.substring(1, text.length()).split(" ");
		for (ICommand c : commands) {
			if (c.getCommandString().equals(command[0])) {
				c.runCommand(event);
				return;
			}
		}
		commandDoesntExist(event);
	}
	
	private static void commandDoesntExist(MessageReceivedEvent event) {
		event.getChannel().sendMessage("That command doesn't exist! Try "+BotMain.PREFIX+"help!").queue();
	}
	
	public static void loadCommands() {
		commands = new ArrayList<ICommand>();
		commands.add(new Help());
		commands.add(new Setup());
	}
	
}
