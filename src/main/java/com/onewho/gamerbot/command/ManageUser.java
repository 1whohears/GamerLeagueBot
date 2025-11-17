package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;

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
		return "`"+BotMain.PREFIX+getCommandString()+" [setting] [user ping] (value)`"
				+ " Settings: `get`, `lock`, `join`, `remove`, `sets-per-week`" +
                ", `join-queue`, `quit-queue`";
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
				long pingId = getIdFromMention(params[2]);
				if (pingId == -1) {
					event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
					return false;
				}
				event.getChannel().sendMessage(ldata.addUser(event.getGuild(), pingId, true));
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
        addSubCommand(new SubCommand("override-score") {
            @Override
            public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
                long pingId = getIdFromMention(params[2]);
                if (pingId == -1) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
                    return false;
                }
                int score;
                try {
                    score = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[3]+" is not a number!").queue();
                    return false;
                }
                if (!ldata.userOverrideScore(event.getGuild(), event.getChannel(), pingId, score)) return false;
                GlobalData.saveData();
                return true;
            }
        });
        addSubCommand(new SubCommand("join-queue") {
            @Override
            public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
                long pingId = getIdFromMention(params[2]);
                if (pingId == -1) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
                    return false;
                }
                int id;
                try {
                    id = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[3]+" is not a number!").queue();
                    return false;
                }
                QueueData queue = ldata.getQueueById(id);
                if (queue == null) {
                    event.getChannel().sendMessage(Important.getError()+" there is no queue with id "+id+"!").queue();
                    return false;
                }
                UserData user = ldata.getUserDataById(pingId);
                if (user == null) {
                    event.getChannel().sendMessage(Important.getError()+" that member is not in this league!").queue();
                    return false;
                }
                QueueResult result = queue.addIndividual(user);
                switch (result) {
                    case SUCCESS -> {
                        event.getChannel().sendMessage("Added that player to queue "+id+"!").queue();
                    }
                    case ALREADY_JOINED -> {
                        event.getChannel().sendMessage("That player already joined queue "+id+"!").queue();
                    }
                    case CLOSED -> {
                        event.getChannel().sendMessage(Important.getError()+" That queue is no longer open!").queue();
                        return false;
                    }
                    case CHANGED_TEAM -> {
                        event.getChannel().sendMessage(Important.getError()+" this should not be possible 1?").queue();
                        return false;
                    }
                    case WRONG_TEAM_SIZE -> {
                        event.getChannel().sendMessage(Important.getError()+" this should not be possible 2?").queue();
                        return false;
                    }
                }
                GlobalData.saveData();
                return true;
            }
        });
        addSubCommand(new SubCommand("quit-queue") {
            @Override
            public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
                long pingId = getIdFromMention(params[2]);
                if (pingId == -1) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a valid ping!").queue();
                    return false;
                }
                int id;
                try {
                    id = Integer.parseInt(params[3]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(Important.getError()+" "+params[3]+" is not a number!").queue();
                    return false;
                }
                QueueData queue = ldata.getQueueById(id);
                if (queue == null) {
                    event.getChannel().sendMessage(Important.getError()+" there is no queue with id "+id+"!").queue();
                    return false;
                }
                boolean removed = queue.removeFromQueue(pingId);
                if (removed) {
                    event.getChannel().sendMessage("Removed that player from queue "+id+"!").queue();
                } else {
                    event.getChannel().sendMessage(Important.getError()+" that player is not in queue "+id+"!").queue();
                }
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
