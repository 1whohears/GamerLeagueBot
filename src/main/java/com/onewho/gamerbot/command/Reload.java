/**
 * this command is for development purposes only
 */

package com.onewho.gamerbot.command;

import java.io.IOException;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Reload implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "reload";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+"` ";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		try {
			GlobalData.readJsonData();
			event.getChannel().sendMessage("Realoaded Data File!").queue();
			System.out.println("Reloading JsonData");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
