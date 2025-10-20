package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateTeamSet extends LeagueCommand {

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        if (params.length != 3) {
            event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
            return false;
        }
        String teamName1 = params[1];
        String teamName2 = params[2];
        TeamData team1 = ldata.getTeamByName(teamName1);
        if (team1 == null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName1+" doesn't exists!").queue();
            return false;
        }
        TeamData team2 = ldata.getTeamByName(teamName2);
        if (team2 == null) {
            event.getChannel().sendMessage(Important.getError()+" "+teamName2+" doesn't exists!").queue();
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
        return "createteamset";
    }

    @Override
    public String getHelp() {
        return "`"+BotMain.PREFIX+getCommandString()+" [team_name_1] [team_name_2]`";
    }

    @Override
    public String getRequiredChannelName() {
        return "bot-commands";
    }
}
