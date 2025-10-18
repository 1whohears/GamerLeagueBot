package com.onewho.gamerbot.util;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.*;
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

    public record Result(UserData[] team1, UserData[] team2) { }

    private static class ResultHolder {
        Result best = null;
        double bestFairness = Double.MAX_VALUE;
    }

    public static Result balanceTeams(UserData[] allUsers) {
        int n = allUsers.length;
        int teamSize1 = n / 2;
        int teamSize2 = n - teamSize1;
        int totalScore = Arrays.stream(allUsers).mapToInt(UserData::getScore).sum();

        List<UserData> sorted = new ArrayList<>(Arrays.asList(allUsers));
        sorted.sort(Comparator.comparingInt(UserData::getScore).reversed());

        long top1Id = sorted.get(0).getId();
        long top2Id = sorted.get(1).getId();

        final ResultHolder holder = new ResultHolder();

        List<UserData> chosen = new ArrayList<>();
        backtrack(allUsers, 0, teamSize1, chosen, totalScore, teamSize1, teamSize2,
                (team1) -> {
                    List<UserData> team2 = new ArrayList<>(Arrays.asList(allUsers));
                    team2.removeAll(team1);

                    double sum1 = team1.stream().mapToInt(UserData::getScore).sum();
                    double sum2 = team2.stream().mapToInt(UserData::getScore).sum();
                    double fairness = Math.abs(sum1 - sum2);

                    if (teamSize1 != teamSize2 && (teamHasBothPlayers(team1, top1Id, top2Id)
                            || teamHasBothPlayers(team2, top1Id, top2Id))) {
                        fairness += 1000;
                    }

                    if (fairness < holder.bestFairness) {
                        holder.bestFairness = fairness;
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

    public static boolean teamHasBothPlayers(List<UserData> team, long p1Id, long p2Id) {
        boolean hasP1 = false;
        boolean hasP2 = false;

        for (UserData user : team) {
            long id = user.getId();
            if (id == p1Id) hasP1 = true;
            if (id == p2Id) hasP2 = true;
        }

        return hasP1 && hasP2;
    }
	
}
