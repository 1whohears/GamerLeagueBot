package com.onewho.gamerbot.command;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

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
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		JsonObject gdataJson = gdata.getJson();
		JsonObject backup = new JsonObject();
		backup.add("users", gdataJson.get("users").getAsJsonArray());
		backup.add("sets", gdataJson.get("sets").getAsJsonArray());
		int num = gdata.processSets();
		//display
		if (num == 0) {
			event.getChannel().sendMessage("There were no sets ready to be processed!").queue();
			return true;
		}
		event.getChannel().sendMessage("Processed "+num+" sets! Ranks and backups are being updated!").queue();
		// TODO display new ranks
		TextChannel ranksChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("ranks"));
		MessageCreateBuilder mcb = new MessageCreateBuilder();
		
		MessageCreateData mcd = mcb.build();
		// TODO send backup
		LeagueData.saveData();
		return true;
	}

}
