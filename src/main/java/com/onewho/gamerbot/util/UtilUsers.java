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
	
	public static boolean userJoinLeague(Guild guild, User user, Channel channel) {
		//json data
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) return false;
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) userData = data.createUser(user.getIdLong());
		//join role
		guild.addRoleToMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		//update league data
		userData.setActive(true);
		userData.setLastActive(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		GlobalData.saveData();
		return true;
	}
	
	public static boolean userQuitLeague(Guild guild, User user, Channel channel) {
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
	
	public static boolean userSetsAWeek(Guild guild, User user, int sets, Channel channel) {
		//json data
		LeagueData data = GlobalData.getGuildDataById(guild.getIdLong()).getLeagueByChannel(channel);
		if (data == null) return false;
		UserData userData = data.getUserDataById(user.getIdLong());
		if (userData == null) userData = data.createUser(user.getIdLong());
		//update league data
		if (!userData.getActive()) return false;
		userData.setSetsPerWeek(sets);
		GlobalData.saveData();
		return true;
	}
	
}
