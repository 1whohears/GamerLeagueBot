package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class UpdateRanks implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "updateranks";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		Guild guild = event.getGuild();
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		int num = gdata.processSets();
		//display
		if (num == 0) {
			event.getChannel().sendMessage("There were no sets ready to be processed!").queue();
			return true;
		}
		event.getChannel().sendMessage("Processed "+num+" sets! Ranks and backups are being updated!").queue();
		// TODO display new ranks
		// TODO send backup
		LeagueData.saveData();
		return true;
	}

}
