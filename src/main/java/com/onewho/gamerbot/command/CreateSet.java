package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.SetData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
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
		if (params.length != 3) {
			event.getChannel().sendMessage(Report.getInsult()
					+" do: `"+BotMain.PREFIX+"createset [p1 ping] [p2 ping]`").queue();
			return true;
		}
		long id1 = getIdFromMention(params[1]);
		long id2 = getIdFromMention(params[2]);
		if (id1 == -1) {
			event.getChannel().sendMessage(Report.getInsult()+" "+params[1]
					+" is not a mention!").queue();
			return true;
		}
		if (id2 == -1) {
			event.getChannel().sendMessage(Report.getInsult()+" "+params[2]
					+" is not a mention!").queue();
			return true;
		}
		Guild guild = event.getGuild();
		LeagueData gdata = GlobalData.getGuildDataById(guild.getIdLong())
				.getLeagueByChannel(event.getChannel());
		if (gdata.getUserDataById(id1) == null) {
			event.getChannel().sendMessage(Report.getInsult()
					+" The first user/mention is not in this league!").queue();
			return true;
		}
		if (gdata.getUserDataById(id2) == null) {
			event.getChannel().sendMessage(Report.getInsult()
					+" The second user/mention is not in this league!").queue();
			return true;
		}
		SetData set = gdata.createSet(id1, id2);
		if (set == null) {
			event.getChannel().sendMessage(Report.getInsult()
					+" You can't make someone fight themself!").queue();
			return true;
		}
		event.getChannel().sendMessage("Successfully created set "+set.getId()).queue();
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, gdata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		GlobalData.saveData();
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
