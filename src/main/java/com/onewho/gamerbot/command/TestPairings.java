package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilUsers;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class TestPairings extends LeagueCommand {

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        if (params.length < 3) {
            event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
            return false;
        }
        UserData[] allUsers = new UserData[params.length-1];
        List<UserData> sortedUsers = ldata.getActiveUsersThisSeason();
        LeagueData.sortByScoreDescend(sortedUsers);
        for (int i = 1, j = 0; i < params.length; ++i, ++j) {
            int rank = -1;
            try {
                rank = Integer.parseInt(params[i]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(Important.getError()+" "+params[i]+" is not a number!").queue();
                return false;
            }
            rank -= 1;
            if (rank < 0 || rank >= sortedUsers.size()) {
                event.getChannel().sendMessage(Important.getError()+" "+params[i]
                        +" must be between 1 and "+sortedUsers.size()).queue();
                return false;
            }
            allUsers[j] = sortedUsers.get(rank);
        }
        UtilUsers.Result result = UtilUsers.balanceTeams(allUsers);
        event.getChannel().sendMessage("Team 1: "+listUserNames(event.getGuild(), result.team1())).queue();
        event.getChannel().sendMessage("Team 2: "+listUserNames(event.getGuild(), result.team2())).queue();
        return true;
    }

    public static String listUserNames(Guild guild, UserData... users) {
        String names = "";
        for (UserData user : users) {
            Member m = guild.getMemberById(user.getUserId());
            if (m == null) names += "??? ";
            else names += m.getEffectiveName()+" ";
        }
        return names;
    }

    @Override
    public String getRequiredChannelName() {
        return "bot-commands";
    }

    @Override
    public boolean getNeedsTO() {
        return false;
    }

    @Override
    public String getCommandString() {
        return "testpairings";
    }

    @Override
    public String getHelp() {
        return "`"+BotMain.PREFIX+getCommandString()+" [P1Rank] [P2Rank]...`";
    }
}
