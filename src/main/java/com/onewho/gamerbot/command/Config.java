package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Config extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "config";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [setting] [value]`"
				+ " Settings: `max-sets-per-week`, `weeks-before-auto-inactive`, `weeks-before-set-expires`,"
				+ " `weeks-until-set-repeat`, `default-score`, `K` (elo K constant), `auto-gen-pairs`, `auto-update-ranks`,"
				+ " `challenges-per-week`, `season-end`, `season-start`";
	}
	
	public Config() {
		addSubCommand(new SubCommand("max-sets-per-week") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				int value = parseInt(params[2]);
				if (value == Integer.MIN_VALUE) {
					notNumber(event);
					return false;
				}
				value = ldata.setMaxSetsPerWeek(value);
				ldata.updateOptions(event.getGuild());
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("weeks-before-auto-inactive") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				int value = parseInt(params[2]);
				if (value == Integer.MIN_VALUE) {
					notNumber(event);
					return true;
				}
				value = ldata.setWeeksBeforeAutoInactive(value);
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("weeks-before-set-expires") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				int value = parseInt(params[2]);
				if (value == Integer.MIN_VALUE) {
					notNumber(event);
					return true;
				}
				value = ldata.setWeeksBeforeSetExpires(value);
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("weeks-until-set-repeat") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				int value = parseInt(params[2]);
				if (value == Integer.MIN_VALUE) {
					notNumber(event);
					return true;
				}
				value = ldata.setWeeksUntilSetRepeat(value);
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("default-score") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				int value = parseInt(params[2]);
				if (value == Integer.MIN_VALUE) {
					notNumber(event);
					return true;
				}
				value = ldata.setDefaultScore(value);
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("K") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				double valueD = parseDouble(params[2]);
				if (valueD < 0) {
					notNumber(event);
					return true;
				}
				ldata.setK(valueD);
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueD+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("auto-gen-pairs") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				boolean valueB;
				if (params[2].equals("true")) valueB = true;
				else if (params[2].equals("false")) valueB = false;
				else {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not true or false!").queue();
					return true;
				}
				ldata.autoGenPairs = valueB;
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueB+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("auto-update-ranks") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				boolean valueB;
				if (params[2].equals("true")) valueB = true;
				else if (params[2].equals("false")) valueB = false;
				else {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not true or false!").queue();
					return true;
				}
				ldata.autoUpdateRanks = valueB;
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueB+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("challenges-per-week") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				int value = parseInt(params[2]);
				if (value == Integer.MIN_VALUE) {
					notNumber(event);
					return true;
				}
				value = ldata.setChallengesPerWeek(value);
				event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("season-end") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				String end = params[2];
				if (end.equals("none")) end = "";
				if (ldata.setSeasonEnd(end)) event.getChannel().sendMessage("`"+params[1]+"` set to `"+end+"`").queue();
				else event.getChannel().sendMessage(Important.getError(end+" is not in `dd-mm-yyyy` format!")).queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
		addSubCommand(new SubCommand("season-start") {
			@Override
			public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
				String start = params[2];
				if (ldata.setSeasonStart(start)) event.getChannel().sendMessage("`"+params[1]+"` set to `"+start+"`").queue();
				else event.getChannel().sendMessage(Important.getError(start+" is not in `dd-mm-yyyy` format!")).queue();
				GlobalData.markReadyToSave();
				return true;
			}
		});
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 3) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		return runSubCommands(event, params, gdata, ldata) == SubCommandResult.SUCCESS;
	}
	
	private int parseInt(String param) {
		try {
			return Integer.parseInt(param);
		} catch (NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
	}
	
	private double parseDouble(String param) {
		try {
			return Double.parseDouble(param);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	private void notNumber(MessageReceivedEvent event) {
		event.getChannel().sendMessage(Important.getError()+" is not a number!").queue();
	}

}
