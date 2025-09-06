package com.onewho.gamerbot.util;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.Collection;

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
	
}
