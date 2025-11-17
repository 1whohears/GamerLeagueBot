package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ManageQueue extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "managequeue";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [setting] [queue id] (value)`"
				+ " Settings: `info`, `gen-pairs`, `set-close-time`, `remove`";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	public ManageQueue() {
		addSubCommand(new SubCommand("gen-pairs") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
                int id;
                try {
                    id = Integer.parseInt(params[2]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a number!").queue();
                    return false;
                }
                QueueData queue = ldata.getQueueById(id);
                if (queue == null) {
                    event.getChannel().sendMessage(Important.getError()+" there is no queue with id "+id+"!").queue();
                    return false;
                }
                queue.genPairs(event.getGuild(), ldata, event.getChannel());
				GlobalData.saveData();
				return true;
			}
		});
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length < 3) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		return runSubCommands(event, params, gdata, ldata) == SubCommandResult.SUCCESS;
	}

}
