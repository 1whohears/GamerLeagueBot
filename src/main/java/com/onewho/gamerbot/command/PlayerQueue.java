package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerQueue extends LeagueCommand {

    public static class JoinQueue extends PlayerQueue {
        @Override
        public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata,
                                  LeagueData league, @NotNull UserData user, @NotNull QueueData queue) {
            QueueResult result = queue.addIndividual(user);
            event.getChannel().sendMessage("Join Queue "+queue.getId()+" result: "+result.name()).queue();
            if (result == QueueResult.SUCCESS) GlobalData.saveData();
            return true;
        }
        @Override
        public String getCommandString() {
            return "joinqueue";
        }
        @Override
        public String getHelp() {
            return "`"+BotMain.PREFIX+getCommandString()+" <queue_id>`";
        }
    }

    public static class LeaveQueue extends PlayerQueue {
        @Override
        public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata,
                                  LeagueData league, @NotNull UserData user, @NotNull QueueData queue) {
            boolean result = queue.removeFromQueue(user.getId());
            event.getChannel().sendMessage("Leave Queue "+queue.getId()+" result: "+result).queue();
            if (result) GlobalData.saveData();
            return true;
        }
        @Override
        public String getCommandString() {
            return "joinqueue";
        }
        @Override
        public String getHelp() {
            return "`"+BotMain.PREFIX+getCommandString()+" <queue_id>`";
        }
    }

    public static class CheckInQueue extends PlayerQueue {
        @Override
        public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata,
                                  LeagueData league, @NotNull UserData user, @NotNull QueueData queue) {
            QueueResult result = queue.checkIn(user.getId());
            event.getChannel().sendMessage("Check In Queue "+queue.getId()+" result: "+result.name()).queue();
            if (result == QueueResult.SUCCESS) GlobalData.saveData();
            return true;
        }
        @Override
        public String getCommandString() {
            return "joinqueue";
        }
        @Override
        public String getHelp() {
            return "`"+BotMain.PREFIX+getCommandString()+" <queue_id>`";
        }
    }

    public static class CheckOutQueue extends PlayerQueue {
        @Override
        public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata,
                                  LeagueData league, @NotNull UserData user, @NotNull QueueData queue) {
            QueueResult result = queue.checkOut(user.getId());
            event.getChannel().sendMessage("Check Out Queue "+queue.getId()+" result: "+result.name()).queue();
            if (result == QueueResult.SUCCESS) GlobalData.saveData();
            return true;
        }
        @Override
        public String getCommandString() {
            return "joinqueue";
        }
        @Override
        public String getHelp() {
            return "`"+BotMain.PREFIX+getCommandString()+" <queue_id>`";
        }
    }

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        if (params.length < 2) {
            event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
            return false;
        }
        long selfId = event.getAuthor().getIdLong();
        UserData user = ldata.getUserDataById(selfId);
        if (user == null) {
            event.getChannel().sendMessage(Important.getError()+" you are not in this league!").queue();
            return false;
        }
        int id;
        try {
            id = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a number!").queue();
            return false;
        }
        QueueData queue = ldata.getQueueById(id);
        if (queue == null) {
            event.getChannel().sendMessage(Important.getError()+" there is no queue with id "+id+"!").queue();
            return false;
        }
        return runCommand(event, params, gdata, ldata, user, queue);
    }

    protected abstract boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata,
                                          LeagueData league, @NotNull UserData user, @NotNull QueueData queue);

    @Override
    public String getRequiredChannelName() {
        return "bot-commands";
    }

    @Override
    public boolean getNeedsTO() {
        return false;
    }

}
