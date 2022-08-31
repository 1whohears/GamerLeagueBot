package com.onewho.gamerbot.command;

import java.util.List;

import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

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
		LeagueData gdata = GlobalData.getGuildDataById(guild.getIdLong())
				.getLeagueByChannel(event.getChannel());
		Backup.createBackup(guild, "pre_updateranks_backup", event.getChannel());
		int num = gdata.processSets();
		//display
		if (num == 0) {
			event.getChannel().sendMessage("There were no sets ready to be processed!").queue();
			return true;
		}
		event.getChannel().sendMessage("Processed "+num+" sets! Ranks and backups are being updated!").queue();
		TextChannel ranksChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("ranks"));
		List<UserData> users = gdata.getAllUsers();
		LeagueData.sortByScoreDescend(users);
		MessageCreateBuilder mcb = new MessageCreateBuilder();
		mcb.addContent("__**"+UtilCalendar.getCurrentDateString()+" RANKS**__");
		int r = 0, r2 = 0, prevScore = Integer.MAX_VALUE;
		for (UserData user : users) {
			++r2;
			if (user.getScore() < prevScore) r = r2;
			mcb.addContent("\n**"+r+")** "+getMention(user.getId())+" **"+user.getScore()+"**");
			prevScore = user.getScore();
		}
		MessageCreateData mcd = mcb.build();
		ranksChannel.sendMessage(mcd).queue();
		GlobalData.saveData();
		return true;
	}
	
	private String getMention(long id) {
		return "<@"+id+">";
	}

}
