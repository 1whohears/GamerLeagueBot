package com.onewho.gamerbot.command;

import java.util.List;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.data.UserData;

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
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		gdata.removeOldSets();
		List<UserData> activeUsers = gdata.getAvailableSortedUsers();
		boolean allPaired = false;
		while (!allPaired) {
			allPaired = true;
			for (UserData udata : activeUsers) {
				List<SetData> incompleteSets = gdata.getIncompleteSetsWithPlayer(udata.getId());
				if (incompleteSets.size() >= udata.getSetsPerWeek()) continue;
				int[] ksort = GuildData.getClosestUserIndexsByScore(udata, activeUsers);
				for (int i = 0; i < ksort.length; ++i) {
					List<SetData> incompleteSetsK = gdata.getIncompleteSetsWithPlayer(activeUsers.get(ksort[i]).getId());
					if (incompleteSetsK.size() >= udata.getSetsPerWeek()) continue;
					//TODO have they played?
					SetData recentSet = gdata.getNewestSetBetweenUsers(udata.getId(), activeUsers.get(ksort[i]).getId());
					
					//TODO create set
				}
			}
		}
		//debug
		LeagueData.saveData();
		System.out.println("Pairings Generated");
		event.getChannel().sendMessage("Finished Generating Pairings!").queue();
		return true;
	}

}
