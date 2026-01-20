package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.function.Consumer;

public class CreateRandomTeamSet extends LeagueCommand {

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        if (params.length < 5) {
            event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
            return false;
        }
        // create even teams
        UserData[] allUsers = new UserData[params.length-3];
        for (int i = 3, j = 0; i < params.length; ++i, ++j) {
            long id = getIdFromMention(params[i]);
            allUsers[j] = ldata.getUserDataById(id);
            if (allUsers[j] == null) {
                event.getChannel().sendMessage(Important.getError()+" "+params[i]+" is not in this league!").queue();
                return false;
            }
        }
        String teamName1 = params[1];
        String teamName2 = params[2];

        return run(ldata, event.getGuild(), msg -> event.getChannel().sendMessage(msg).queue(),
                teamName1, teamName2, allUsers) != null;
    }

    public static SetData run(LeagueData ldata, Guild guild, Consumer<String> debugConsumer,
                              String teamName1, String teamName2, UserData... allUsers) {
        UtilUsers.Result result = UtilUsers.balanceTeams(allUsers);
        // create new team names
        TeamData team1 = UtilUsers.getCreateTeam(teamName1, ldata, result.team1());
        if (team1 == null) {
            debugConsumer.accept(Important.getError()+" "+teamName1+" team failed to create!");
            return null;
        }
        if (!team1.getName().equals(teamName1)) {
            debugConsumer.accept("Team "+teamName1+" already exists and is being renamed to "+team1.getName());
        }
        TeamData team2 = UtilUsers.getCreateTeam(teamName2, ldata, result.team2());
        if (team2 == null) {
            debugConsumer.accept(Important.getError()+" "+teamName2+" team failed to create!");
            return null;
        }
        if (!team2.getName().equals(teamName2)) {
            debugConsumer.accept("Team "+teamName2+" already exists and is being renamed to "+team2.getName());
        }
        // create set
        SetData set = ldata.createTeamSet(team1.getName(), team2.getName());
        if (set == null) {
            debugConsumer.accept(Important.getError() +" You can't make someone fight themself!");
            return null;
        }
        debugConsumer.accept("Successfully created set "+set.getId());
        TextChannel pairsChannel = guild.getChannelById(TextChannel.class, ldata.getChannelId("pairings"));
        set.displaySet(pairsChannel);
        GlobalData.markReadyToSave();
        return set;
    }

    @Override
    public boolean getNeedsTO() {
        return true;
    }

    @Override
    public String getCommandString() {
        return "createrandomteamset";
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
