package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.SetData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RefreshSet implements ICommand {

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
		return "refreshset";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` ";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		int setId = -1;
		if (params.length < 2) {
			event.getChannel().sendMessage("no").queue();
			return false;
		}
		try {
			setId = Integer.parseInt(params[1]);
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage(params[1]+" is not a number").queue();
			return false;
		}
		Guild guild = event.getGuild();
		LeagueData gdata = GlobalData.getGuildDataById(guild.getIdLong())
				.getLeagueByChannel(event.getChannel());
		SetData set = gdata.getSetDataById(setId);
		if (set == null) {
			event.getChannel().sendMessage("this set doesn't exist").queue();
			return true;
		}
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		event.getChannel().sendMessage(set+" refreshed!").queue();
		return true;
	}

}
