package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveSet extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "removeset";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [set id] (set id) (set id)...` remove these sets.";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length < 2) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		int[] ids = new int[params.length-1];
		for (int i = 0; i < ids.length; ++i) {
			try { ids[i] = Integer.parseInt(params[i+1]); } 
			catch (NumberFormatException e) {
				event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a number!").queue();
				ids[i] = -1;
			}
		}
		int num = ldata.removeSets(event.getGuild(), event.getChannel(), ids);
		if (num == 0) {
			event.getChannel().sendMessage(Important.getError()+" None of these sets could be removed!").queue();
			return false;
		}
		event.getChannel().sendMessage("Removed "+num+" sets!").queue();
		GlobalData.saveData();
		return true;
	}

}
