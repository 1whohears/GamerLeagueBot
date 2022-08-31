package com.onewho.gamerbot.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

public class Backup implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
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
		createBackup(event.getGuild(), "backup");
		return true;
	}
	
	public static void createBackup(Guild guild, String name) {
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		JsonObject backup = gdata.getBackupJson();
		TextChannel historyChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("set-history"));
		String data = GlobalData.getGson().toJson(backup);
		FileUpload fu = FileUpload.fromData(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), 
				guild.getName()+"_"+name+"_"+UtilCalendar.getCurrentDateTimeString()+".json");
		historyChannel.sendFiles(fu).queue();
		try { fu.close(); } 
		catch (IOException e) { e.printStackTrace(); }
	}

}
