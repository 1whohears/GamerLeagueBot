package com.onewho.gamerbot.interact;

import com.onewho.gamerbot.util.UtilUsers;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonManager {
	
	public static void handleButton(ButtonInteractionEvent event) {
		String id = event.getButton().getId();
		if (id.startsWith("setsaweek-")) {
			int sets = Integer.parseInt(id.substring("setsaweek-".length(), id.length()));
			if (UtilUsers.userSetsAWeek(event.getGuild(), event.getUser(), sets)) 
				event.reply("I will try to give you "+sets+" pairings next week!").setEphemeral(true).queue();
			else event.reply("You must join the Gamer League first!").setEphemeral(true).queue();
			return;
		}
		switch (id) {
		case "join-gamer-league":
			UtilUsers.userJoinLeague(event.getGuild(), event.getUser());
			//debug
			event.reply("You have joined the Gamer League! Please select how many sets you want to do per week!"
					+ " Use ~help in #bot-commands for more info!").setEphemeral(true).queue();
			break;
		case "quit-gamer-league":
			UtilUsers.userQuitLeague(event.getGuild(), event.getUser());
			//debug
			event.reply("You have left the gamer league...sad...").setEphemeral(true).queue();
			break;
		}
	}
	
}
