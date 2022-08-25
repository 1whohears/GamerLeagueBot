package com.onewho.gamerbot;

import javax.security.auth.login.LoginException;

import com.onewho.gamerbot.event.EventListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BotMain {
	
	public static JDA jda;
	
	public static char PREFIX = '~';
	
	public static void main(String [] arg) {
		try {
			jda = JDABuilder.createLight(TokenReader.getJDAToken(), GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MEMBERS)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setChunkingFilter(ChunkingFilter.ALL)
					.addEventListeners(new EventListener())
					.setActivity(Activity.playing("these fools"))
					.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
		
	}
	
}
