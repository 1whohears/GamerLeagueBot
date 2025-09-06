package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateTeam extends LeagueCommand {

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        if (params.length <= 2) {
            event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
            return false;
        }
        String teamName = params[1];
        UserData[] users = new UserData[params.length-1];
        long senderId = event.getAuthor().getIdLong();
        users[0] = ldata.getUserDataById(senderId);
        for (int i = 2, j = 1; i < users.length; ++i, ++j) {
            int id;
            try {
                id = Integer.parseInt(params[i]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(Important.getError()+" "+params[i]+" is not a number!").queue();
                return false;
            }
            users[j] = ldata.getUserDataById(id);
            if (users[j] == null) {
                event.getChannel().sendMessage(Important.getError()+" "+params[i]+" is not in this league!").queue();
                return false;
            }
        }
        TeamData team = ldata.createTeam(teamName, users);
        if (team == null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName+" already exists!").queue();
            return false;
        }
        String memberPings = Report.getMention(team);
        event.getChannel().sendMessage("Created Team "+teamName+" with members "+memberPings+"!").queue();
        GlobalData.saveData();
        return true;
    }

    @Override
    public boolean getNeedsTO() {
        return false;
    }

    @Override
    public String getCommandString() {
        return "create-team";
    }

    @Override
    public String getHelp() {
        return "`"+BotMain.PREFIX+getCommandString()+" [team_name] [member_ping] [member_ping]...`";
    }

    @Override
    public String getRequiredChannelName() {
        return "bot-commands";
    }
}
