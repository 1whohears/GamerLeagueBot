package com.onewho.gamerbot.interact;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonManager {
	
	public static void handleButton(ButtonInteractionEvent event) {
		JsonObject data = LeagueData.getGuildDataById(event.getGuild().getIdLong());
		switch (event.getButton().getId()) {
		case "join-gamer-league":
			System.out.println(event.getUser().getName()+" pressed the join button");
			event.getGuild().addRoleToMember(event.getUser(), event.getGuild().getRoleById(data.get("league role id").getAsLong())).queue();
			event.reply("You have joined the Gamer League! Please select how many sets you want to do per week!"
					+ " Use ~help in #bot-commands for more info!").setEphemeral(true).queue();
			// TODO update league data
			break;
		case "quit-gamer-league":
			System.out.println(event.getUser().getName()+" pressed the quit button");
			event.getGuild().removeRoleFromMember(event.getUser(), event.getGuild().getRoleById(data.get("league role id").getAsLong())).queue();
			event.reply("You have left the gamer league...sad...").setEphemeral(true).queue();
			break;
		}
		LeagueData.saveData();
	}
	
}
