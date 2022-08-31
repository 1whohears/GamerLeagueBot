package com.onewho.gamerbot.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileProxy;

public class ReadBackup implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "readbackup";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		List<Attachment> att = event.getMessage().getAttachments();
		if (att.size() == 0) {
			event.getChannel().sendMessage("There were no files attached to your command.").queue();
			return true;
		}
		String url = att.get(0).getProxyUrl();
		FileProxy fp = new FileProxy(url);
		InputStream stream = null;
		try {
			stream = fp.download().get();
		} catch (InterruptedException e) {
			event.getChannel().sendMessage("There was an error while downloading this file."
					+ "\n"+e.getMessage()).queue();
		} catch (ExecutionException e) {
			event.getChannel().sendMessage("There was an error while downloading this file."
					+ "\n"+e.getMessage()).queue();
		}
		if (stream == null) return true;
		Reader reader = new InputStreamReader(stream);
		JsonObject backup = null;
		try {
			backup = LeagueData.getGson().fromJson(reader, JsonObject.class);
			reader.close();
		} catch (JsonSyntaxException e) {
			event.getChannel().sendMessage("There is a syntax error in this json file").queue();
			return true;
		} catch (JsonIOException e) {
		} catch (IOException e) {
		}
		if (backup == null || backup.get("users") == null || backup.get("sets") == null) {
			event.getChannel().sendMessage("The uploaded file is not a backup file").queue();
			return true;
		}
		Guild guild = event.getGuild();
		Backup.createBackup(guild, "pre-readbackup");
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		try {
			gdata.readBackup(backup);
		} catch (IllegalStateException e) {
			event.getChannel().sendMessage("The uploaded file is not a backup file").queue();
			return true;
		} catch (ClassCastException e) {
			event.getChannel().sendMessage("The uploaded file is not a backup file").queue();
			return true;
		}
		LeagueData.saveData();
		event.getChannel().sendMessage("Backup has been loaded!").queue();
		return true;
	}

}
