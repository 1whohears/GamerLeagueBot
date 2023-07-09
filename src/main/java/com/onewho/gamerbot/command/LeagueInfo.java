package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LeagueInfo extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "leagueinfo";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` get info about league for debugging";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		// TODO display set data sub command
		if (params.length != 1) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		MessageChannelUnion mcu = event.getChannel();
		mcu.sendMessage("__**"+ldata.getName()+" INFO**__"
				+ "\nDefault Score = "+ldata.getDefaultScore()
				+ "\nSeason Number = "+ldata.getSeasonId()
				+ "\nSeason Start = "+ldata.getSeasonStart()
				+ "\nSeason End = "+ldata.getSeasonEnd()
				+ "\nChallenges Per Week = "+ldata.getChallengesPerWeek()
				+ "\nElo K Constant = "+ldata.getK()
				+ "\nDefault Score = "+ldata.getDefaultScore()
				+ "\nLoaded Sets = "+ldata.getNumberOfCachedSets())
			.queue();
		return true;
	}

}
