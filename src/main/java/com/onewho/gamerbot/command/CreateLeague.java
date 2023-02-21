package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateLeague extends AdminCommand {

	@Override
	public String getCommandString() {
		return "createleague";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [league name]` ";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (!super.runCommand(event, params)) return false;
		if (params.length < 2) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.createGuildData(guild.getIdLong());
		String name = "";
		for (int i = 1; i < params.length; ++i) name += params[i];
		gdata.createLeague(guild, event.getChannel(), name);
		GlobalData.saveData();
		return true;
	}

}
