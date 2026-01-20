package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.SetData;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateSet extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "createset";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [p1 ping] [p2 ping]` create a set with these 2 users.";
	}
	
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 3) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		long id1 = getIdFromMention(params[1]);
		long id2 = getIdFromMention(params[2]);
		if (id1 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a mention!").queue();
			return false;
		}
		if (id2 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a mention!").queue();
			return false;
		}
		if (ldata.getUserDataById(id1) == null) {
			event.getChannel().sendMessage(Important.getError()+" The first user/mention is not in this league!").queue();
			return false;
		}
		if (ldata.getUserDataById(id2) == null) {
			event.getChannel().sendMessage(Important.getError()+" The second user/mention is not in this league!").queue();
			return false;
		}
		SetData set = ldata.createSet(id1, id2);
		if (set == null) {
			event.getChannel().sendMessage(Important.getError()
					+" You can't make someone fight themself!").queue();
			return false;
		}
		event.getChannel().sendMessage("Successfully created set "+set.getId()).queue();
		TextChannel pairsChannel = event.getGuild().getChannelById(TextChannel.class, 
				ldata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		GlobalData.markReadyToSave();
		return true;
	}

}
