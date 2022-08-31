package com.onewho.gamerbot.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateSet implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "createset";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 5) {
			event.getChannel().sendMessage(Report.getInsult()
					+" do: `~createset [p1 ping] [p2 ping]`").queue();
			return true;
		}
		long id1 = getIdFromMention(params[1]);
		long id2 = getIdFromMention(params[2]);
		// TODO check if id are -1 and create set
		return true;
	}
	
	private boolean checkIfMention(String m) {
		return m.length() > 10 && m.charAt(0) == '<' && m.charAt(1) == '@' && m.charAt(m.length()-1) == '>';
	}
	
	private long getIdFromMention(String m) {
		if (!checkIfMention(m)) return -1;
		long id = -1;
		String pingString = m.substring(2, m.length()-1);
		try { id = Long.parseLong(pingString); } 
		catch (NumberFormatException e) {}
		return id;
	}

}
