package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SubCommand {
	
	public final String param;
	
	public SubCommand(String param) {
		this.param = param;
	}
	
	public String getHelp() {
		return "`"+BotMain.PREFIX+param+" [setting] [value]`";
	}
	
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		return true;
	}
	
}
