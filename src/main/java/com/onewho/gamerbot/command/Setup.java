package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Setup implements ICommand {
	
	@Override
	public boolean getNeedsAdmin() {
		return true;
	}
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}
	
	@Override
	public String getCommandString() {
		return "setup";
	}
	
	@Override
	public String getRequiredChannelName() {
		return null;
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.createGuildData(guild.getIdLong());
		if (gdata == null) {
			event.getChannel().sendMessage("This guild doesn't have any leagues.").queue();
			return true;
		}
		gdata.setupLeagues(guild, event.getChannel());
		return true;
	}
	
}
