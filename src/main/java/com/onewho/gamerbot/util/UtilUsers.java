package com.onewho.gamerbot.util;

import com.onewho.gamerbot.data.*;

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

    public static TeamData getCreateTeam(Guild guild, LeagueData ldata, UserData... members) {
        return getCreateTeam(LeagueData.createTeamName(guild, members), ldata, members);
    }

    public record Result(UserData[] team1, UserData[] team2) { }

    private static class ResultHolder {
        Result best = null;
        double bestFairness = Double.MAX_VALUE;
    }

    public static Result balanceTeams(Collection<Contestant> allContestants, Collection<Set<Long>> preferredTeams) {
        int n = 0;
        for (Contestant c : allContestants) n += c.getTeamSize();
        UserData[] allUsers = new UserData[n];
        int i = 0;
        for (Contestant c : allContestants) {
            if (c.isIndividual()) {
                allUsers[i++] = (UserData) c;
            } else if (c.isTeam()) {
                TeamData td = (TeamData) c;
                for (UserData ud : td.getUsers()) allUsers[i++] = ud;
            } else {
                System.out.println("Somehow a contestant is not a team or individual???");
            }
        }
        return balanceTeams(allUsers, preferredTeams);
    }

    public static Result balanceTeams(UserData[] allUsers) {
        return balanceTeams(allUsers, new ArrayList<>());
    }

    public static Result balanceTeams(UserData[] allUsers, Collection<Set<Long>> preferredTeams) {
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
                    if (missingPreferredTeams(team1, team2, preferredTeams)) {
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

    private static boolean missingPreferredTeams(List<UserData> team1, List<UserData> team2,
                                            Collection<Set<Long>> preferredTeams) {
        for (Set<Long> preferredTeam : preferredTeams) {
            if (!(hasPreferredTeam(team1, preferredTeam) || hasPreferredTeam(team2, preferredTeam))) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPreferredTeam(List<UserData> team, Set<Long> preferredTeam) {
        int k = 0;
        for (UserData user : team) {
            for (Long id : preferredTeam) {
                if (user.getUserId() == id) {
                    k++;
                }
            }
        }
        return k >= preferredTeam.size();
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
