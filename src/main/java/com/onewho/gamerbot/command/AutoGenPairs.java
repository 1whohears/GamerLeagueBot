package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AutoGenPairs implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "autogenpairs";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 2) {
			event.getChannel().sendMessage(Report.getInsult()
					+" do: `"+BotMain.PREFIX+"autogenpairs [true/false]`").queue();
			return true;
		}
		boolean auto = false;
		if (params[1].equals("true")) auto = true;
		else if (params[1].equals("false")) auto = false;
		else {
			event.getChannel().sendMessage(Report.getInsult()+" you didn't input true or false.").queue();
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
		ldata.autoGenPairs = auto;
		GlobalData.saveData();
		return true;
	}

}
