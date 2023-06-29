package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.SetData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CancelChallenge extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return false;
	}

	@Override
	public String getCommandString() {
		return "cancelcha";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [set id]' cancel a challenge";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 2) {
			event.getChannel().sendMessage(Important.getError("DO: "+getHelp())).queue();
			return false;
		}
		long selfId = event.getAuthor().getIdLong();
		if (ldata.getUserDataById(selfId) == null) {
			event.getChannel().sendMessage(Important.getError("You are not in this league!")).queue();
			return false;
		}
		int setid;
		try {
			setid = Integer.parseInt(params[1]);
		} catch (NumberFormatException e) {
			event.getChannel().sendMessage(Important.getError(params[1]+" is not a number!")).queue();
			return false;
		}
		SetData set = ldata.getSetDataById(setid);
		if (set == null) {
			event.getChannel().sendMessage(Important.getError("Set "+params[1]+" does not exist!")).queue();
			return false;
		}
		if (!set.isChallenge()) {
			event.getChannel().sendMessage(Important.getError("This is not a challenge! Canceling requires a TO.")).queue();
			return false;
		}
		if (!set.hasPlayer(selfId)) {
			event.getChannel().sendMessage(Important.getError("You are not in this challenge!")).queue();
			return false;
		}
		if (set.isUnconfirmed() || set.isComplete()) {
			event.getChannel().sendMessage(Important.getError("Results for this challenge have been submitted. Canceling requires a TO.")).queue();
			return false;
		}
		if (set.isProcessed()) {
			event.getChannel().sendMessage(Important.getError("Results for this challenge have been finalized. Undoing requires using a backup. (dangerous!)")).queue();
			return false;
		}
		int num = ldata.removeSets(event.getGuild(), event.getChannel(), new int[]{setid});
		if (num == 0) {
			event.getChannel().sendMessage(Important.getError("None of these sets could be removed!")).queue();
			return false;
		}
		event.getChannel().sendMessage("Removed the challenge!").queue();
		GlobalData.saveData();
		return true;
	}

}
