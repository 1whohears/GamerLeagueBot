package com.onewho.gamerbot.command;

import java.util.HashMap;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	
	public boolean getNeedsAdmin();
	
	public boolean getNeedsTO();
	
	public String getCommandString();
	
	public String getHelp();
	
	public boolean runCommand(MessageReceivedEvent event, String[] params);
	
	public HashMap<String, SubCommand> subCommands = new HashMap<>();
	
	default SubCommandResult runSubCommands(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length < 2) return SubCommandResult.NO_PARAMS;
		SubCommand sc = subCommands.get(params[1]);
		if (sc == null) {
			event.getChannel().sendMessage(Important.getError()
					+" setting "+params[1]+" doesn't exist. Try `"+BotMain.PREFIX+"help`!")
				.queue();
			return SubCommandResult.PARAM_DNE;
		}
		if (!sc.runCommand(event, params, gdata, ldata)) return SubCommandResult.COMMAND_FAIL;
		return SubCommandResult.SUCCESS;
	}
	
	public static enum SubCommandResult {
		NO_PARAMS,
		PARAM_DNE,
		COMMAND_FAIL,
		SUCCESS
	}
	
}
