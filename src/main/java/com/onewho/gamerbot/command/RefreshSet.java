package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.SetData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RefreshSet extends AdminCommand {
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
	@Override
	public String getCommandString() {
		return "refreshset";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` manually redisplay a set";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		int setId = -1;
		if (params.length < 2) {
			event.getChannel().sendMessage(Important.getError("no")).queue();
			return false;
		}
		try {
			setId = Integer.parseInt(params[1]);
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage(Important.getError(params[1]+" is not a number")).queue();
			return false;
		}
		Guild guild = event.getGuild();
		LeagueData gdata = GlobalData.getGuildDataById(guild.getIdLong())
				.getLeagueByChannel(event.getChannel());
		SetData set = gdata.getSetDataById(setId);
		if (set == null) {
			event.getChannel().sendMessage(Important.getError("this set doesn't exist")).queue();
			return true;
		}
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		event.getChannel().sendMessage(set+" refreshed!").queue();
		return true;
	}

}
