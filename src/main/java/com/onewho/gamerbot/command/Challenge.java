package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Challenge extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return false;
	}

	@Override
	public String getCommandString() {
		return "challenge";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [p2 ping]` request to challenge this player";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 2) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		long selfId = event.getAuthor().getIdLong();
		long enemyId = getIdFromMention(params[1]);
		if (enemyId == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a mention!").queue();
			return false;
		}
		if (selfId == enemyId) {
			event.getChannel().sendMessage(Important.getError()+" You can't challenge yourself!").queue();
			return false;
		}
		if (ldata.getUserDataById(selfId) == null) {
			event.getChannel().sendMessage(Important.getError()+" You are not in this league!").queue();
			return false;
		}
		if (ldata.getUserDataById(enemyId) == null) {
			event.getChannel().sendMessage(Important.getError()+" Your desired opponent is not in this league!").queue();
			return false;
		}
		if (!ldata.createChallenge(event.getGuild(), event.getChannel(), selfId, enemyId)) return false;
		GlobalData.saveData();
		return true;
	}

}
