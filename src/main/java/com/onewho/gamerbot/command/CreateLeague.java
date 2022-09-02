package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateLeague implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "createleague";
	}

	@Override
	public String getRequiredChannelName() {
		return null;
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length < 2) {
			event.getChannel().sendMessage(Report.getInsult()
					+" do: `"+BotMain.PREFIX+"createleague [league name]`").queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.createGuildData(guild.getIdLong());
		String name = "";
		for (int i = 1; i < params.length; ++i) name += params[i];
		gdata.createLeague(guild, event.getChannel(), name);
		return true;
	}

}
