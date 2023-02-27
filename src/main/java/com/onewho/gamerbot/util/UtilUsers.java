package com.onewho.gamerbot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;

import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class UtilUsers {
	
	/**
	 * @param guild
	 * @param user
	 * @param channel
	 * @return result message
	 */
	public static String userJoinLeague(Guild guild, User user, Channel channel) {
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) 
			return "This channel isn't in a league!";
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) userData = data.createUser(user.getIdLong());
		if (userData.isLocked()) 
			return "You are not allowed to join this league because a TO locked you out!";
		guild.addRoleToMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		userData.setActive(true);
		userData.setLastActive(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		GlobalData.saveData();
		return "You have joined the Gamer League! Please select how many sets you want to do per week!"
				+ " Use $help in #bot-commands for more info!";
	}
	
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
		if (!userData.getActive()) 
			return "You have been set as an inactive player. Please rejoin the league first!";
		userData.setSetsPerWeek(sets);
		GlobalData.saveData();
		return "I will try to give you "+sets+" pairings next week!";
	}
	
}
