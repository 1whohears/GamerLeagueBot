package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RenameLeague implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return false;
	}

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "renameleague";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [new name]` rename this league!";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 2) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		if (gdata == null) {
			event.getChannel().sendMessage("This guild doesn't have any leagues.").queue();
			return true;
		}
		LeagueData ldata = gdata.getLeagueByChannel(event.getChannel());
		if (ldata == null) {
			event.getChannel().sendMessage("This is not a valid league.").queue();
			return true;
		}
		ldata.setName(params[1]);
		ldata.setupDiscordStuff(guild, event.getChannel());
		return true;
	}

}
