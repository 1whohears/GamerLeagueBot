package com.onewho.gamerbot.command;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.ReportResult;
import com.onewho.gamerbot.data.SetData;
import com.onewho.gamerbot.util.UtilCalendar;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class Report implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
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
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 5) {
			event.getChannel().sendMessage(getInsult()
					+" do: `"+BotMain.PREFIX+"report [set id] [your score] [opponent score] [opponent ping]`").queue();
			return true;
		}
		int id = -1, s1 = -1, s2 = -1;
		long pingId = -1;
		if (!checkIfMention(params[4])) {
			event.getChannel().sendMessage(getInsult()+" "+params[4]+" is not a mention!").queue();
			return true;
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
			event.getChannel().sendMessage(getInsult()+" "+params[1]+" is not a number!").queue();
			return true;
		} else if (s1 == -1) {
			event.getChannel().sendMessage(getInsult()+" "+params[2]+" is not a number!").queue();
			return true;
		} else if (s2 == -1) {
			event.getChannel().sendMessage(getInsult()+" "+params[3]+" is not a number!").queue();
			return true;
		} else if (pingId == -1) {
			event.getChannel().sendMessage(getInsult()+" you didn't mention/ping your opponent correctly!").queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = GlobalData.getGuildDataById(guild.getIdLong());
		if (gdata == null) {
			event.getChannel().sendMessage("This guild doesn't have any leagues.").queue();
			return true;
		}
		LeagueData ldata = gdata.getLeagueByChannel(event.getChannel());
		if (ldata == null) {
			event.getChannel().sendMessage("This is not a valid league.").queue();
			return true;
		}
		SetData set = ldata.getSetDataById(id);
		if (set == null) {
			event.getChannel().sendMessage(getInsult()+" The set with id "+id+" does not exist!").queue();
			return true;
		}
		String currentData = UtilCalendar.getCurrentDateString();
		ReportResult result = set.report(event.getAuthor().getIdLong(), pingId, s1, s2, currentData);
		switch (result) {
		case IDsDontMatch:
			event.getChannel().sendMessage(getInsult()+" This set id does not have those players!").queue();
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
		TextChannel pairsChannel = guild.getChannelById(TextChannel.class, ldata.getChannelId("pairings"));
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
	
	private static String[] insults = {"STUPID!", "IDIOT!", "BRUH!", "WRONG!", "UHHGGG!",  
			"WOW!", "WHY!?", "DONKEYKONG?", "This is why I didn't go to your birthday party.",
			"Your faliures would be so funny if they weren't so sad.", "sigh...", "SAD!",
			"You have less brain cells than a yoshi player.", "wrong wrong wrong...",
			"Dr Doofenshmirtz is no longer the dumbest person in the tristate area!",
			"CRINGE!", "BAD!", "L", "RATIO", "LOL!", "yikes...", "man", "ROFL!", 
			"It's nice to know that no one read the docs.", "oof", "INCORRECT!",
			"I'm going to start a twitter account just to make fun of you.", "get good",
			"no", "Wrong again.", "Are you even trying?", "yeeeaahhh....no", "ur bad!",
			"You would think after billions of years of evolution all Earthlings"
			+ " would be competent by now. I'll tell my buddies from Proxima Centauri"
			+ " to delay the invasion at least a thousand earth years so y'all have a chance.",
			"This is just sad.", "You won't like the side of the IQ bell curve that your on.",
			"When the gurus tell you to clear your mind they don't mean remove your brain"
			+ " from your skull you idiot.", "I would feel bad for you..but I can't because"
			+ " I am hundreds of lines of code.", "Why are people so afraid of AI taking over?"
			+ " Y'all are going to destroy yourselves from incompetence.", "Go see a doctor."};
	
	public static String getInsult() {
		return insults[(int)(Math.random()*insults.length)];
	}

}
