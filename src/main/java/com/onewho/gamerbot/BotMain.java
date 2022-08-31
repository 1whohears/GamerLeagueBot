package com.onewho.gamerbot;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import com.onewho.gamerbot.event.EventListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class BotMain {
	
	public static JDA jda;
	
	public static char PREFIX = '~';
	
	public static void main(String [] arg) {
		try {
			jda = JDABuilder.createDefault(TokenReader.getJDAToken(), 
					GatewayIntent.GUILD_MEMBERS, 
					GatewayIntent.GUILD_MESSAGES,
					GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
					GatewayIntent.MESSAGE_CONTENT)
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setChunkingFilter(ChunkingFilter.ALL)
					.build();
			jda.addEventListener(new EventListener());
		} catch (LoginException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
