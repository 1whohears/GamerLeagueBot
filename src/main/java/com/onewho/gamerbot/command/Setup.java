package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

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
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` Sets up channels/roles for this server's leagues."
				+ " Run this command if you accidentally delete a channel.";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		if (gdata == null) {
			event.getChannel().sendMessage("This guild doesn't have any leagues.").queue();
			return true;
		}
		LeagueData ldata = gdata.getLeagueByChannel(event.getChannel());
		if (ldata == null) gdata.setupLeagues(guild, event.getChannel());
		else               ldata.setupDiscordStuff(guild, event.getChannel());
		return true;
	}
	
}
