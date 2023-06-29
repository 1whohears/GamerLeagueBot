package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
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
		return "`"+BotMain.PREFIX+getCommandString()+" [setting] [user ping] (value)`";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	public ManageUser() {
		addSubCommand(new SubCommand("get") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				long pingId = getIdFromMention(params[2]);
				if (pingId == -1) {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
					return false;
				}
				if (!ldata.postUserData(event.getGuild(), event.getChannel(), pingId)) return false;
				GlobalData.saveData();
				return true;
			}
		});
		addSubCommand(new SubCommand("lock") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				long pingId = getIdFromMention(params[2]);
				if (pingId == -1) {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
					return false;
				}
				boolean valueB;
				if (params[2].equals("true")) valueB = true;
				else if (params[2].equals("false")) valueB = false;
				else {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not true or false!").queue();
					return true;
				}
				if (!ldata.lockUser(event.getGuild(), event.getChannel(), pingId, valueB)) return false;
				return true;
			}
		});
		addSubCommand(new SubCommand("join") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				// TODO join sub command
				event.getChannel().sendMessage(Important.getError()+" This command doesn't do anything yet!").queue();
				return true;
			}
		});
		addSubCommand(new SubCommand("remove") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				long pingId = getIdFromMention(params[2]);
				if (pingId == -1) {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
					return false;
				}
				if (!ldata.removeUser(event.getGuild(), event.getChannel(), pingId)) return false;
				GlobalData.saveData();
				return true;
			}
		});
		addSubCommand(new SubCommand("sets-per-week") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				long pingId = getIdFromMention(params[2]);
				if (pingId == -1) {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
					return false;
				}
				int sets;
				try {
					sets = Integer.parseInt(params[3]);
				} catch (NumberFormatException e) {
					event.getChannel().sendMessage(Important.getError()+" "+params[3]+" is not a number!").queue();
					return false;
				}
				if (!ldata.userSetsPerWeek(event.getGuild(), event.getChannel(), pingId, sets)) return false;
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
