package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveLeague extends AdminCommand {

	@Override
	public String getCommandString() {
		return "removeleague";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [league name]` "
				+ "creates a backup and removes a league. **USE WITH CAUTION**";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (!super.runCommand(event, params)) return false;
		MessageChannelUnion debugChannel = event.getChannel();
		if (params.length != 2) {
			debugChannel.sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		if (gdata == null) {
			debugChannel.sendMessage("No leagues were found in this server.").queue();
			return false;
		}
		return gdata.removeLeague(guild, debugChannel, params[1]);
	}

}
