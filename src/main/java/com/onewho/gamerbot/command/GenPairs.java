package com.onewho.gamerbot.command;

import java.util.List;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
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
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		System.out.println("running gen pairs command");
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("pairings"));
		gdata.removeOldSets(pairsChannel);
		List<UserData> activeUsers = gdata.getAvailableSortedUsers();
		boolean createdSet = true;
		while (createdSet) {
			createdSet = false;
			System.out.println("BIG LOOP");
			for (UserData udata : activeUsers) {
				System.out.println("user "+udata.getId());
				List<SetData> incompleteSets = gdata.getIncompleteOrCurrentSetsByPlayer(udata.getId());
				System.out.println("incomplete sets "+incompleteSets.size());
				if (incompleteSets.size() >= udata.getSetsPerWeek()) continue;
				int[] ksort = GuildData.getClosestUserIndexsByScore(udata, activeUsers);
				for (int i = 0; i < ksort.length; ++i) {
					UserData userk = activeUsers.get(ksort[i]);
					System.out.println("userk "+userk.getId());
					List<SetData> incompleteSetsK = gdata.getIncompleteOrCurrentSetsByPlayer(userk.getId());
					System.out.println("incomplete sets k "+incompleteSetsK.size());
					if (incompleteSetsK.size() >= userk.getSetsPerWeek()) continue;
					SetData recentSet = gdata.getNewestSetBetweenUsers(udata.getId(), userk.getId());
					System.out.println("recent set "+recentSet);
					if (recentSet != null) {
						int diff = UtilCalendar.getWeekDiff(
								UtilCalendar.getDate(recentSet.getCreatedDate()), UtilCalendar.getCurrentDate());
						if (diff <= gdata.getWeeksBeforeSetRepeat()) continue;
					}
					long id1 = udata.getId(), id2 = activeUsers.get(ksort[i]).getId();
					if (id1 == id2) continue;
					gdata.createSet(id1, id2);
					createdSet = true;
					break;
				}
			}
		}
		//display new sets
		gdata.displaySetsByDate(UtilCalendar.getCurrentDateString(), pairsChannel);
		//debug
		GlobalData.saveData();
		System.out.println("Pairings Generated");
		event.getChannel().sendMessage("Finished Generating Pairings!").queue();
		return true;
	}

}
