package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ManageUser extends LeagueCommand {

	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "manageuser";
	}

	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [setting] [user ping] [value]`";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	public ManageUser() {
		subCommands.put("get", new SubCommand() {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				
				return true;
			}
		});
		subCommands.put("lock", new SubCommand() {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				
				return true;
			}
		});
		subCommands.put("join", new SubCommand() {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				
				return true;
			}
		});
		subCommands.put("remove", new SubCommand() {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				
				return true;
			}
		});
		subCommands.put("sets-per-week", new SubCommand() {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				
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
		// TODO manage user sub commands
		event.getChannel().sendMessage(Important.getError()+" This command doesn't do anything yet!").queue();
		return runSubCommands(event, params, gdata, ldata) == SubCommandResult.SUCCESS;
	}

}
