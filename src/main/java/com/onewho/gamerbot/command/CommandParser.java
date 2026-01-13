package com.onewho.gamerbot.command;

import java.util.ArrayList;
import java.util.List;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.Important;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandParser {
	
	public static List<ICommand> commands;
	
	public static void parseCommand(MessageReceivedEvent event) {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) return;
		if (commands == null) loadCommands();
		String text = event.getMessage().getContentRaw();
		if (text.length() < 2) return;
		String[] command = text.substring(1).trim().split("\\s+");
		for (ICommand c : commands) {
			if (c.getCommandString().equals(command[0])) {
				c.runCommand(event, command);
				return;
			}
		}
		event.getChannel().sendMessage(Important.getError("That command doesn't exist! Try `"+BotMain.PREFIX+"help`!")).queue();
	}
	
	public static void loadCommands() {
		commands = new ArrayList<>();
		commands.add(new Help());
		commands.add(new Setup());
		commands.add(new Config());
		commands.add(new GenPairs());
		commands.add(new UpdateRanks());
		commands.add(new Report());
		commands.add(new ReportAdmin());
		commands.add(new ManageUser());
		commands.add(new LeagueInfo());
		commands.add(new Challenge());
		commands.add(new CancelChallenge());
		commands.add(new CreateSet());
		commands.add(new RemoveSet());
		commands.add(new Backup());
		commands.add(new ReadBackup());
		commands.add(new CreateLeague());
		commands.add(new RenameLeague());
		commands.add(new RemoveLeague());
		//commands.add(new Test()); // for testing
		//commands.add(new Reload()); // for testing
		commands.add(new RefreshSet()); // for testing
		commands.add(new NewSeason());
        commands.add(new CreateTeam());
        commands.add(new CreateTeamSet());
        commands.add(new CreateRandomTeamSet());
        commands.add(new TestPairings());
        commands.add(new CreateQueue());
        commands.add(new ManageQueue());
        commands.add(new LinkDiscord());
	}
	
	private static List<ICommand> userCommands;
	private static List<ICommand> toCommands;
	private static List<ICommand> adminCommands;
	
	public static List<ICommand> getUserCommands() {
		if (userCommands == null) {
			userCommands = new ArrayList<>();
			for (ICommand c : commands) 
				if (!c.getNeedsTO() && !c.getNeedsAdmin()) 
					userCommands.add(c);
		}
		return userCommands;
	}
	
	public static List<ICommand> getTOCommands() {
		if (toCommands == null) {
			toCommands = new ArrayList<>();
			for (ICommand c : commands) 
				if (c.getNeedsTO() && !c.getNeedsAdmin()) 
					toCommands.add(c);
		}
		return toCommands;
	}
	
	public static List<ICommand> getAdminCommands() {
		if (adminCommands == null) {
			adminCommands = new ArrayList<>();
			for (ICommand c : commands) 
				if (c.getNeedsAdmin()) 
					adminCommands.add(c);
		}
		return adminCommands;
	}
	
}
