package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateRandomTeamSet extends LeagueCommand {

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        if (params.length < 5) {
            event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
            return false;
        }
        String teamName1 = params[1];
        String teamName2 = params[2];
        TeamData team1 = ldata.getTeamByName(teamName1);
        if (team1 != null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName1+" already exists!").queue();
            return false;
        }
        TeamData team2 = ldata.getTeamByName(teamName2);
        if (team2 != null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName2+" already exists!").queue();
            return false;
        }
        UserData[] allUsers = new UserData[params.length-3];
        for (int i = 3, j = 0; i < params.length; ++i, ++j) {
            long id = getIdFromMention(params[i]);
            allUsers[j] = ldata.getUserDataById(id);
            if (allUsers[j] == null) {
                event.getChannel().sendMessage(Important.getError()+" "+params[i]+" is not in this league!").queue();
                return false;
            }
        }
        UtilUsers.Result result = UtilUsers.balanceTeams(allUsers);
        team1 = ldata.createTeam(teamName1, result.team1());
        team2 = ldata.createTeam(teamName2, result.team2());
        if (team1 == null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName1+" team failed to create!").queue();
            return false;
        }
        if (team2 == null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName2+" team failed to create!").queue();
            return false;
        }
        SetData set = ldata.createTeamSet(team1.getName(), team2.getName());
        if (set == null) {
            event.getChannel().sendMessage(Important.getError()
                    +" You can't make someone fight themself!").queue();
            return false;
        }
        event.getChannel().sendMessage("Successfully created set "+set.getId()).queue();
        TextChannel pairsChannel = event.getGuild().getChannelById(TextChannel.class,
                ldata.getChannelId("pairings"));
        set.displaySet(pairsChannel);
        GlobalData.saveData();
        return true;
    }

    @Override
    public boolean getNeedsTO() {
        return true;
    }

    @Override
    public String getCommandString() {
        return "create-random-team-set";
    }

    @Override
    public String getHelp() {
        return "`"+BotMain.PREFIX+getCommandString()+" [team_name_1] [team_name_2] [player_ping] [player_ping]...`";
    }

    @Override
    public String getRequiredChannelName() {
        return "bot-commands";
    }
}
