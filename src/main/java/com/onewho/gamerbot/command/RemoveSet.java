package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveSet implements ICommand {

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
		return "removeset";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [set id] (set id) (set id)...` remove this set.";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length < 2) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return true;
		}
		int[] ids = new int[params.length-1];
		for (int i = 0; i < ids.length; ++i) {
			try { ids[i] = Integer.parseInt(params[i+1]); } 
			catch (NumberFormatException e) {
				event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a number!").queue();
				ids[i] = -1;
			}
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
		int num = ldata.removeSets(guild, event.getChannel(), ids);
		if (num == 0) {
			event.getChannel().sendMessage(Important.getError()+" None of these sets could be removed!").queue();
			return true;
		}
		event.getChannel().sendMessage(Important.getError()+" Removed "+num+" sets!").queue();
		GlobalData.saveData();
		return true;
	}

}
