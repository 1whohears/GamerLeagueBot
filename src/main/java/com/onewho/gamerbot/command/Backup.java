package com.onewho.gamerbot.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

public class Backup implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return false;
	}
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "backup";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		createBackup(event.getGuild(), "backup", event.getChannel());
		return true;
	}
	
	public static boolean createBackup(Guild guild, String name, MessageChannelUnion leagueChannel) {
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		if (gdata == null) {
			leagueChannel.sendMessage("This guild doesn't have any leagues.").queue();
			return false;
		}
		LeagueData ldata = gdata.getLeagueByChannel(leagueChannel);
		if (ldata == null) {
			leagueChannel.sendMessage("This is not a valid league.").queue();
			return false;
		}
		JsonObject backup = ldata.getBackupJson();
		TextChannel historyChannel = guild.getChannelById(TextChannel.class, ldata.getChannelId("set-history"));
		String data = GlobalData.getGson().toJson(backup);
		FileUpload fu = FileUpload.fromData(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), 
				guild.getName()+"_"+name+"_"+UtilCalendar.getCurrentDateTimeString()+".json");
		historyChannel.sendFiles(fu).queue();
		try { fu.close(); } 
		catch (IOException e) { e.printStackTrace(); return false; }
		return true;
	}

}
