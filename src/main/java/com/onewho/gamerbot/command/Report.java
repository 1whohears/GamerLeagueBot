package com.onewho.gamerbot.command;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
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
		try {
			id = Integer.parseInt(params[1]);
			s1 = Integer.parseInt(params[2]);
			s2 = Integer.parseInt(params[3]);
		} catch (NumberFormatException e) {
		}
		long pingId = getIdFromMention(params[4]);
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
			event.getChannel().sendMessage(Important.getError()+" "+params[4]+" is not a mention!").queue();
			return false;
		}
		SetData set = ldata.getSetDataById(id);
		if (set == null) {
			event.getChannel().sendMessage(Important.getError()+" The set with id "+id+" does not exist!").queue();
			return false;
		}
		String currentDate = UtilCalendar.getCurrentDateString();
		ReportResult result = set.report(event.getAuthor().getIdLong(), pingId, s1, s2, currentDate);
		switch (result) {
		case IDsDontMatch:
			event.getChannel().sendMessage(Important.getError()+" This set id does not have those players!").queue();
			break;
		case ScoreConflict:
			event.getChannel().sendMessage("This conflicts with the score that your opponent reported! "
					+ "If you are correct have your opponent report again, or get a hold of an admin.").queue();
			break;
		case SetVerified:
			ldata.getUserDataById(event.getAuthor().getIdLong()).setLastActive(currentDate);
			event.getChannel().sendMessage("Set reported and verified by opponent!").queue();
			break;
		case WaitingForOpponent:
			MessageCreateData mcd = new MessageCreateBuilder()
				.addEmbeds(getVerifyEmbed(set, pingId))
				.addActionRow(getVerifyButtons())
				.build();
			ldata.getUserDataById(event.getAuthor().getIdLong()).setLastActive(currentDate);
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
	
	private MessageEmbed getVerifyEmbed(SetData set, long opponentId) {
		EmbedBuilder jleb = new EmbedBuilder();
		jleb.setDescription(getMention(opponentId)+" Verify Report?"
				+ "\n**SET ID ["+set.getId()+"]**"
				+ "\n**"+set.getP1score()+"** "+getMention(set.getContestant1())
				+ "\n**"+set.getP2score()+"** "+getMention(set.getContestant2()));
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

    public static String getMention(Contestant c) {
        String m = "";
        for (Long id : c.getUserIds()) m += "<@"+id+">";
        return m;
    }

}
