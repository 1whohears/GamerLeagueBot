package com.onewho.gamerbot.command;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.ReportResult;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class Report extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
		return false;
	}

	@Override
	public String getCommandString() {
		return "report";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [set id] [your score] [opponent score] [opponent ping]`"
				+ " report one of your sets";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (params.length != 5) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
		int id = -1, s1 = -1, s2 = -1;
		long pingId = -1;
		if (!checkIfMention(params[4])) {
			event.getChannel().sendMessage(Important.getError()+" "+params[4]+" is not a mention!").queue();
			return false;
		}
		String pingString = params[4].substring(2, params[4].length()-1);
		try {
			id = Integer.parseInt(params[1]);
			s1 = Integer.parseInt(params[2]);
			s2 = Integer.parseInt(params[3]);
			pingId = Long.parseLong(pingString);
		} catch (NumberFormatException e) {
		}
		if (id == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a number!").queue();
			return false;
		} else if (s1 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[2]+" is not a number!").queue();
			return false;
		} else if (s2 == -1) {
			event.getChannel().sendMessage(Important.getError()+" "+params[3]+" is not a number!").queue();
			return false;
		} else if (pingId == -1) {
			event.getChannel().sendMessage(Important.getError()+" you didn't mention/ping your opponent correctly!").queue();
			return false;
		}
		SetData set = ldata.getSetDataById(id);
		if (set == null) {
			event.getChannel().sendMessage(Important.getError()+" The set with id "+id+" does not exist!").queue();
			return false;
		}
		String currentData = UtilCalendar.getCurrentDateString();
		ReportResult result = set.report(event.getAuthor().getIdLong(), pingId, s1, s2, currentData);
		switch (result) {
		case IDsDontMatch:
			event.getChannel().sendMessage(Important.getError()+" This set id does not have those players!").queue();
			break;
		case ScoreConflict:
			event.getChannel().sendMessage("This conflicts with the score that your opponent reported! "
					+ "If you are correct have your opponent report again, or get a hold of an admin.").queue();
			break;
		case SetVerified:
			ldata.getUserDataById(event.getAuthor().getIdLong()).setLastActive(currentData);
			event.getChannel().sendMessage("Set reported and verified by opponent!").queue();
			break;
		case WaitingForOpponent:
			MessageCreateData mcd = new MessageCreateBuilder()
				.addEmbeds(getVerifyEmbed(set, pingId))
				.addActionRow(getVerifyButtons())
				.build();
			ldata.getUserDataById(event.getAuthor().getIdLong()).setLastActive(currentData);
			event.getChannel().sendMessage(mcd).queue();
			break;
		case AlreadyVerified:
			event.getChannel().sendMessage("This set has already been verified. Admin required to update.").queue();
			break;
		}
		//display new sets
		TextChannel pairsChannel = event.getGuild().getChannelById(TextChannel.class, 
				ldata.getChannelId("pairings"));
		set.displaySet(pairsChannel);
		GlobalData.saveData();
		return true;
	}
	
	private boolean checkIfMention(String m) {
		return m.length() > 10 && m.charAt(0) == '<' && m.charAt(1) == '@' && m.charAt(m.length()-1) == '>';
	}
	
	private MessageEmbed getVerifyEmbed(SetData set, long opponentId) {
		EmbedBuilder jleb = new EmbedBuilder();
		jleb.setDescription(getMention(opponentId)+" Verify Report?"
				+ "\n**SET ID ["+set.getId()+"]**"
				+ "\n**"+set.getP1score()+"** "+getMention(set.getP1Id())
				+ "\n**"+set.getP2score()+"** "+getMention(set.getP2Id()));
		jleb.setColor(getRandomColor());
		return jleb.build();
	}
	
	private Color getRandomColor() {
		Random random = new Random();
		float hue = random.nextFloat();
		float saturation = 0.9f;
		float luminance = 1.0f;
		return Color.getHSBColor(hue, saturation, luminance);
	}
	
	private List<Button> getVerifyButtons() {
		Button verify = Button.success("report-verify", "Verify");
		Button disbute = Button.danger("report-dispute", "Dispute");
		return Arrays.asList(verify, disbute);
	}
	
	private String getMention(long id) {
		return "<@"+id+">";
	}

}
