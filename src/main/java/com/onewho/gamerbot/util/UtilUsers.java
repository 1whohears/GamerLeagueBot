package com.onewho.gamerbot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class UtilUsers {
	
	public static boolean userJoinLeague(Guild guild, User user) {
		//json data
		JsonObject data = LeagueData.getGuildDataById(guild.getIdLong());
		JsonObject userData = LeagueData.getUserDataById(user.getIdLong(), data);
		//join role
		guild.addRoleToMember(user, guild.getRoleById(data.get("league role id").getAsLong())).queue();
		//update league data
		userData.addProperty("active", true);
		userData.addProperty("last active", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
		LeagueData.saveData();
		return true;
	}
	
	public static boolean userQuitLeague(Guild guild, User user) {
		//json data
		JsonObject data = LeagueData.getGuildDataById(guild.getIdLong());
		JsonObject userData = LeagueData.getUserDataById(user.getIdLong(), data);
		//leave role
		guild.removeRoleFromMember(user, guild.getRoleById(data.get("league role id").getAsLong())).queue();
		//update league data
		userData.addProperty("active", false);
		LeagueData.saveData();
		return true;
	}
	
	public static boolean userSetsAWeek(Guild guild, User user, int sets) {
		//json data
		JsonObject data = LeagueData.getGuildDataById(guild.getIdLong());
		JsonObject userData = LeagueData.getUserDataById(user.getIdLong(), data);
		//update league data
		if (!userData.get("active").getAsBoolean()) return false;
		userData.addProperty("sets per week", sets);
		LeagueData.saveData();
		return true;
	}
	
}
