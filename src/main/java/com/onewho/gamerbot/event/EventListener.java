package com.onewho.gamerbot.event;

import java.io.IOException;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.command.CommandParser;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.interact.ButtonManager;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {
	
	@Override
    public void onReady(ReadyEvent event) {
        try {
			LeagueData.readJsonData();
			System.out.println("Gaming Time in "+event.getGuildTotalCount()+" guilds!");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public void onReconnected(ReconnectedEvent event) {
		try {
			LeagueData.readJsonData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String text = event.getMessage().getContentRaw();
		if (text.length() > 0 && text.charAt(0) != BotMain.PREFIX) return;
		if (!CommandParser.parseCommand(event)) event.getChannel().sendMessage("That command doesn't exist! Try `"+BotMain.PREFIX+"help`!").queue();
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		ButtonManager.handleButton(event);
	}
}
