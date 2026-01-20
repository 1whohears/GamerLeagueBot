package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

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
		return "`"+BotMain.PREFIX+getCommandString()+" [setting] [queue_id/default] (value)`"
				+ " Settings: `reset_timeout`, `start_pregame`, `create_set`,"
                + " `join_player`, `remove_player`, `check_in_player`, `check_out_player`,"
                + " `min_players`, `team_size`, `allow_larger_teams`, `allow_odd_num`,"
                + " `timeout_time`, `sub_request_time`, `pregame_time`, `reset_timeout_on_join`,"
                + " `if_enough_players_auto_start`, `allow_join_via_discord`";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	public ManageQueue() {
        addSubCommand(new QueueSubCommand("reset_timeout",
                ((event, params, gdata, league, queue) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    queue.resetTimeOut(msg -> event.getChannel().sendMessage(msg).queue());
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("start_pregame",
                ((event, params, gdata, league, queue) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    queue.startPreGame(msg -> event.getChannel().sendMessage(msg).queue());
                    GlobalData.markReadyToSave();
                    return true;
                })));
		addSubCommand(new QueueSubCommand("create_set",
                ((event, params, gdata, league, queue) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    queue.createSet(event.getGuild(), league, msg -> event.getChannel().sendMessage(msg).queue());
                    return true;
                })));
        addSubCommand(new QueueSubCommand("join_player",
                ((QueueSubComUserRun)(event, params, gdata, league, queue, user) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    QueueResult result = queue.addIndividual(user);
                    if (result == QueueResult.SUCCESS) {
                        event.getChannel().sendMessage("Added player to queue "+queue.getId()).queue();
                        GlobalData.markReadyToSave();
                        return true;
                    } else {
                        event.getChannel().sendMessage(Important.getError()
                                +" Player could not join queue "+queue.getId()+" because "+result.name()).queue();
                        return false;
                    }
                })));
        // TODO join team
        addSubCommand(new QueueSubCommand("remove_player",
                ((QueueSubComUserRun)(event, params, gdata, league, queue, user) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    boolean result = queue.removeFromQueue(user.getId());
                    if (result) {
                        event.getChannel().sendMessage("Removed player from queue "+queue.getId()).queue();
                        GlobalData.markReadyToSave();
                        return true;
                    } else {
                        event.getChannel().sendMessage(Important.getError()
                                +" Player is already not in queue "+queue.getId()).queue();
                        return false;
                    }
                })));
        addSubCommand(new QueueSubCommand("check_in_player",
                ((QueueSubComUserRun)(event, params, gdata, league, queue, user) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    QueueResult result = queue.checkIn(user.getId());
                    if (result == QueueResult.SUCCESS) {
                        event.getChannel().sendMessage("Checked In player to queue "+queue.getId()).queue();
                        GlobalData.markReadyToSave();
                        return true;
                    } else {
                        event.getChannel().sendMessage(Important.getError()
                                +" Player could not check in to queue "+queue.getId()+" because "+result.name()).queue();
                        return false;
                    }
                })));
        addSubCommand(new QueueSubCommand("check_out_player",
                ((QueueSubComUserRun)(event, params, gdata, league, queue, user) -> {
                    if (queue == null) {
                        event.getChannel().sendMessage("This cub command does not support `default`").queue();
                        return false;
                    }
                    QueueResult result = queue.checkOut(user.getId());
                    if (result == QueueResult.SUCCESS) {
                        event.getChannel().sendMessage("Checked Out player from queue "+queue.getId()).queue();
                        GlobalData.markReadyToSave();
                        return true;
                    } else {
                        event.getChannel().sendMessage(Important.getError()
                                +" Player could not check out of queue "+queue.getId()+" because "+result.name()).queue();
                        return false;
                    }
                })));
        addSubCommand(new QueueSubCommand("min_players",
                ((QueueSubComIntRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueMinPlayers(value);
                        event.getChannel().sendMessage("Set Default Min Players for new queues to " + value).queue();
                    } else {
                        queue.setMinPlayers(value);
                        event.getChannel().sendMessage("Set Min Players for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("team_size",
                ((QueueSubComIntRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueTeamSize(value);
                        event.getChannel().sendMessage("Set Default Team Size for new queues to " + value).queue();
                    } else {
                        queue.setTeamSize(value);
                        event.getChannel().sendMessage("Set Team Size for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("timeout_time",
                ((QueueSubComIntRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueTimeoutTime(value);
                        event.getChannel().sendMessage("Set Default Timeout Time for new queues to " + value).queue();
                    } else {
                        queue.setTimeoutTime(value);
                        event.getChannel().sendMessage("Set Timeout Time for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("sub_request_time",
                ((QueueSubComIntRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueSubRequestTime(value);
                        event.getChannel().sendMessage("Set Default Sub Request Time for new queues to " + value).queue();
                    } else {
                        queue.setSubRequestTime(value);
                        event.getChannel().sendMessage("Set Sub Request Time for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("pregame_time",
                ((QueueSubComIntRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueuePregameTime(value);
                        event.getChannel().sendMessage("Set Default Pre-Game Time for new queues to " + value).queue();
                    } else {
                        queue.setPregameTime(value);
                        event.getChannel().sendMessage("Set Pre-Game Time for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("allow_odd_num",
                ((QueueSubComBoolRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueAllowOddNum(value);
                        event.getChannel().sendMessage("Set Default Allow Odd Number for new queues to " + value).queue();
                    } else {
                        queue.setAllowOddNum(value);
                        event.getChannel().sendMessage("Set Allow Odd Number of Players for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("reset_timeout_on_join",
                ((QueueSubComBoolRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueResetTimeoutOnJoin(value);
                        event.getChannel().sendMessage("Set Default Reset Timeout for new queues to " + value).queue();
                    } else {
                        queue.setResetTimeoutOnJoin(value);
                        event.getChannel().sendMessage("Set Reset Timeout on Join for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("if_enough_players_auto_start",
                ((QueueSubComBoolRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueEnoughPlayersAutoStart(value);
                        event.getChannel().sendMessage("Set Default Auto Start if Enough Players for new queues to " + value).queue();
                    } else {
                        queue.setEnoughPlayersAutoStart(value);
                        event.getChannel().sendMessage("Set Auto Start if Enough Players for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
        addSubCommand(new QueueSubCommand("allow_join_via_discord",
                ((QueueSubComBoolRun)(event, params, gdata, league, queue, value) -> {
                    if (queue == null) {
                        league.setDefaultQueueAllowJoinViaDiscord(value);
                        event.getChannel().sendMessage("Set Default Allow Join Via Discord Commands for new queues to " + value).queue();
                    } else {
                        queue.setAllowJoinViaDiscord(value);
                        event.getChannel().sendMessage("Set Allow Join Via Discord Commands for queue " + queue.getId() + " to " + value).queue();
                    }
                    GlobalData.markReadyToSave();
                    return true;
                })));
	}

    public static class QueueSubCommand extends SubCommand {
        private final QueueSubCommandRun run;
        public QueueSubCommand(String param, QueueSubCommandRun run) {
            super(param);
            this.run = run;
        }
        @Override
        public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
            if (params[2].equals("default")) return run.run(event, params, gdata, ldata, null);
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
            return run.run(event, params, gdata, ldata, queue);
        }
    }

    public interface QueueSubCommandRun {
        boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, @Nullable QueueData queue);
    }

    public interface QueueSubComValueRun extends QueueSubCommandRun {
        default boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, QueueData queue) {
            if (params.length < 4) {
                event.getChannel().sendMessage(Important.getError()+" missing value parameter!").queue();
                return false;
            }
            return run(event, params, gdata, league, queue, params[3]);
        }
        boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, @Nullable QueueData queue, String value);
    }

    public interface QueueSubComBoolRun extends QueueSubComValueRun {
        default boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, QueueData queue, String value) {
            boolean bool;
            if (value.equals("true")) {
                bool = true;
            } else if (value.equals("false")) {
                bool = false;
            } else {
                event.getChannel().sendMessage(Important.getError()+" "+value+" is not `true` or `false`!").queue();
                return false;
            }
            return run(event, params, gdata, league, queue, bool);
        }
        boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, @Nullable QueueData queue, boolean value);
    }

    public interface QueueSubComIntRun extends QueueSubComValueRun {
        default boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, QueueData queue, String value) {
            int num;
            try {
                num = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(Important.getError()+" "+value+" is not a number!").queue();
                return false;
            }
            return run(event, params, gdata, league, queue, num);
        }
        boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, @Nullable QueueData queue, int value);
    }

    public interface QueueSubComUserRun extends QueueSubComValueRun {
        default boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, QueueData queue, String value) {
            long id = ICommand.getIdFromMentionStr(value);
            if (id == -1) {
                event.getChannel().sendMessage(Important.getError()+" "+value+" is not a ping/mention!").queue();
                return false;
            }
            UserData user = league.getUserDataById(id);
            if (user == null) {
                event.getChannel().sendMessage(Important.getError()+" that player is not in this league!").queue();
                return false;
            }
            return run(event, params, gdata, league, queue, user);
        }
        boolean run(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData league, @Nullable QueueData queue, UserData value);
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
