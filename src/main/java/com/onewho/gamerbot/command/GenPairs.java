package com.onewho.gamerbot.command;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class GenPairs implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "genpairs";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot_commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		System.out.println("running gen pairs command");
		Guild guild = event.getGuild();
		JsonObject data = LeagueData.getGuildDataById(guild.getIdLong());
		int maxPairs = data.get("max sets a week").getAsInt();
		int weeksBeforeAutoInactive = 1;
		//TODO sort players by score
		//TODO remove old pairs
		//TODO get available/active players
		//TODO while loop
			//TODO loop through all players
				//TODO check if they haven't reached their requested limit
					//TODO use k closest to find the closest player based on score
		//debug
		System.out.println("Pairings Generated");
		event.getChannel().sendMessage("Finished Generating Pairings!").queue();
		return true;
	}

}
