package com.onewho.gamerbot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class UtilUsers {
	
	public static boolean userJoinLeague(Guild guild, User user) {
		//json data
		GuildData data = LeagueData.getGuildDataById(guild.getIdLong());
		UserData userData = data.getUserDataById(user.getIdLong());
		//join role
		guild.addRoleToMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		//update league data
		userData.setActive(true);
		userData.setLastActive(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		LeagueData.saveData();
		return true;
	}
	
	public static boolean userQuitLeague(Guild guild, User user) {
		//json data
		GuildData data = LeagueData.getGuildDataById(guild.getIdLong());
		UserData userData = data.getUserDataById(user.getIdLong());
		//leave role
		guild.removeRoleFromMember(user, guild.getRoleById(data.getLeagueRoleId())).queue();
		//update league data
		userData.setActive(false);
		LeagueData.saveData();
		return true;
	}
	
	public static boolean userSetsAWeek(Guild guild, User user, int sets) {
		//json data
		GuildData data = LeagueData.getGuildDataById(guild.getIdLong());
		UserData userData = data.getUserDataById(user.getIdLong());
		//update league data
		if (!userData.getActive()) return false;
		userData.setSetsPerWeek(sets);
		LeagueData.saveData();
		return true;
	}
	
}
