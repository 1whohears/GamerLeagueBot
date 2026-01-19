package com.onewho.gamerbot.command;

import java.util.HashMap;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface ICommand {
	
	boolean getNeedsAdmin();
	
	boolean getNeedsTO();
	
	String getCommandString();
	
	String getHelp();
	
	boolean runCommand(MessageReceivedEvent event, String[] params);
	
	HashMap<String, SubCommand> subCommands = new HashMap<>();
	
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
	
	/**
	 * @return if command is not listed help command
	 */
	default boolean isHidden() {
		return false;
	}
	
	default boolean addSubCommand(SubCommand sub) {
		if (subCommands.containsKey(sub.param)) return false;
		subCommands.put(sub.param, sub);
		return true;
	}
	
	enum SubCommandResult {
		NO_PARAMS,
		PARAM_DNE,
		COMMAND_FAIL,
		SUCCESS
	}
	
	static boolean checkIfMention(String m) {
		return m.length() > 10 && m.charAt(0) == '<' && m.charAt(1) == '@' && m.charAt(m.length()-1) == '>';
	}
	
	static long getIdFromMentionStr(String m) {
		if (!checkIfMention(m)) return -1;
		String pingString = m.substring(2, m.length()-1);
		try {
            return Long.parseLong(pingString);
        } catch (NumberFormatException e) {
            return -1;
        }
	}

    default long getIdFromMention(String m) {
        return ICommand.getIdFromMentionStr(m);
    }
	
}
