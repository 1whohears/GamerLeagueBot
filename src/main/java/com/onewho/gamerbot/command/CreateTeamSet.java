package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.function.Consumer;

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
        return run(team1, team2, event.getGuild(), ldata,
                msg -> event.getChannel().sendMessage(msg).queue()) != null;
    }

    public static SetData run(TeamData team1, TeamData team2, Guild guild,
                              LeagueData ldata, Consumer<String> debugConsumer) {
        SetData set = ldata.createTeamSet(team1.getName(), team2.getName());
        if (set == null) {
            debugConsumer.accept(Important.getError()+" You can't make someone fight themself!");
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
