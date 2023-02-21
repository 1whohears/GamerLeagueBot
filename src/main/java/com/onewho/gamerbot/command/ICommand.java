package com.onewho.gamerbot.command;

import java.util.HashMap;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	
	public boolean getNeedsAdmin();
	
	public boolean getNeedsTO();
	
	public String getCommandString();
	
	public String getHelp();
	
	public boolean runCommand(MessageReceivedEvent event, String[] params);
	
	public HashMap<String, SubCommand> subCommands = new HashMap<>();
	
	default boolean runSubCommands(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length < 2) return false;
		SubCommand sc = subCommands.get(params[1]);
		if (sc == null) return false;
		return sc.runCommand(event, params, gdata, ldata);
	}
	
}
