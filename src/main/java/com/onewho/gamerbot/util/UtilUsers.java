package com.onewho.gamerbot.util;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.TeamData;
import com.onewho.gamerbot.data.UserData;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class UtilUsers {
	
	public static String userQuitLeague(Guild guild, User user, Channel channel) {
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) 
			return "This channel isn't in a league!";
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) 
			return "How can you leave something that you aren't in?";
		guild.removeRoleFromMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		userData.setActive(false);
		GlobalData.saveData();
		return "You have left this league...sad...";
	}
	
	public static String userSetsAWeek(Guild guild, User user, Channel channel, int sets) {
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) 
			return "This channel isn't in a league!";
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) 
			return "You must join this league first!";
		if (userData.isLocked()) 
			return "A TO locked your sets per week!";
		if (!userData.isActive()) 
			return "You have been set as an inactive player. Please rejoin the league first!";
		userData.setSetsPerWeek(sets);
		GlobalData.saveData();
		return "I will try to give you "+sets+" pairings next week!";
	}

    public static int getAverageScore(Collection<UserData> users) {
        int total = 0;
        for (UserData u : users) total += u.getScore();
        return total / users.size();
    }

    public static TeamData getCreateTeam(String teamName, LeagueData ldata, UserData... members) {
        TeamData team;
        String baseName = teamName;
        int suffix = 1;
        while (true) {
            team = ldata.getTeamByName(teamName);
            if (team == null) return ldata.createTeam(teamName, members);
            if (team.hasSameMembers(members)) return team;
            int lastSpace = baseName.lastIndexOf('-');
            if (lastSpace != -1) {
                try {
                    suffix = Integer.parseInt(baseName.substring(lastSpace + 1)) + 1;
                    baseName = baseName.substring(0, lastSpace);
                } catch (NumberFormatException ignored) {}
            }
            teamName = baseName + "-" + suffix;
            suffix++;
        }
    }

    public record Result(UserData[] team1, UserData[] team2) { }

    private static class ResultHolder {
        Result best = null;
        double bestDiff = Double.MAX_VALUE;
    }

    public static Result balanceTeams(UserData[] allUsers) {
        int n = allUsers.length;
        int teamSize1 = n / 2;
        int teamSize2 = n - teamSize1;
        int totalScore = Arrays.stream(allUsers).mapToInt(UserData::getScore).sum();

        final ResultHolder holder = new ResultHolder();

        List<UserData> chosen = new ArrayList<>();
        backtrack(allUsers, 0, teamSize1, chosen, totalScore, teamSize1, teamSize2,
                (team1) -> {
                    List<UserData> team2 = new ArrayList<>(Arrays.asList(allUsers));
                    team2.removeAll(team1);

                    double sum1 = team1.stream().mapToInt(UserData::getScore).sum();
                    double sum2 = team2.stream().mapToInt(UserData::getScore).sum();

                    double diff = Math.abs(sum1 - sum2);
                    if (diff < holder.bestDiff) {
                        holder.bestDiff = diff;
                        holder.best = new Result(team1.toArray(new UserData[0]), team2.toArray(new UserData[0]));
                    }
                });

        return holder.best;
    }

    private static void backtrack(UserData[] allUsers, int start, int remaining,
                                  List<UserData> chosen, int totalScore,
                                  int teamSize1, int teamSize2,
                                  Consumer<List<UserData>> consumer) {
        if (remaining == 0) {
            consumer.accept(new ArrayList<>(chosen));
            return;
        }
        for (int i = start; i <= allUsers.length - remaining; i++) {
            chosen.add(allUsers[i]);
            backtrack(allUsers, i + 1, remaining - 1, chosen, totalScore,
                    teamSize1, teamSize2, consumer);
            chosen.remove(chosen.size() - 1);
        }
    }
	
}
