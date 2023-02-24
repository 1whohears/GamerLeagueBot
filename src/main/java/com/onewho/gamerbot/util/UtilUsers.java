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
		//json data
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) return "This channel isn't in a league!";
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) userData = data.createUser(user.getIdLong());
		if (userData.isLocked()) return "You are not allowed to join this league because"
				+ " a TO locked you out!";
		//join role
		guild.addRoleToMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		//update league data
		userData.setActive(true);
		userData.setLastActive(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		GlobalData.saveData();
		return "You have joined the Gamer League! Please select how many sets you want to do per week!"
				+ " Use $help in #bot-commands for more info!";
	}
	
	public static String userQuitLeague(Guild guild, User user, Channel channel) {
		//json data
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) return false;
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) return false;
		//leave role
		guild.removeRoleFromMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		//update league data
		userData.setActive(false);
		GlobalData.saveData();
		return true;
	}
	
	public static String userSetsAWeek(Guild guild, User user, int sets, Channel channel) {
		//json data
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) return "This channel isn't in a league!";
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) userData = data.createUser(user.getIdLong());
		//update league data
		if (userData.isLocked()) return "You are not allowed to join this league because"
				+ " a TO locked you out!";
		if (!userData.getActive()) return "You have been set as an inactive player."
				+ " Please rejoin the league first!";
		userData.setSetsPerWeek(sets);
		GlobalData.saveData();
		return true;
	}
	
}
