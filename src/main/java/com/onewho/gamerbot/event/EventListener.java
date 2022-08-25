package com.onewho.gamerbot.event;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.command.CommandParser;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventListener extends ListenerAdapter {
	
	@Override
    public void onReady(ReadyEvent event)
    {
        System.out.println("I am ready to go!");
    }
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		String text = event.getMessage().getContentRaw();
		if (text.charAt(0) != BotMain.PREFIX) return;
		CommandParser.parseCommand(event);
	}
	
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		
	}
	
	@Override 
	public void onEmojiAdded(EmojiAddedEvent event) {
		
	}
	
	@Override 
	public void onEmojiRemoved(EmojiRemovedEvent event) {
		
	}
}
