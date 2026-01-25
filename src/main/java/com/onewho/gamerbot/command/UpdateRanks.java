package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UpdateRanks extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "updateranks";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` Updates users scores and ranks based on their reported sets.";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        boolean force = params.length >= 2 && params[1].equals("force");
		ldata.updateRanks(event.getGuild(), event.getChannel(), false, force);
		GlobalData.markReadyToSave();
		return true;
	}

}
