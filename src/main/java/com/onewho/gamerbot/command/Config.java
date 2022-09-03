package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Config implements ICommand {

	@Override
	public boolean getNeedsAdmin() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "config";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (params.length != 3) {
			event.getChannel().sendMessage(Important.getInsult()
					+" do: `"+BotMain.PREFIX+"config [setting] [value]`").queue();
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
		int value;
		double valueD;
		boolean valueB = false;
		switch (params[1]) {
		case "max-sets-per-week":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			ldata.setMaxSetsPerWeek(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
			GlobalData.saveData();
			return true;
		case "weeks-before-auto-inactive":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			ldata.setWeeksBeforeAutoInactive(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
			GlobalData.saveData();
			return true;
		case "weeks-before-set-expires":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			ldata.setWeeksBeforeSetExpires(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
			GlobalData.saveData();
			return true;
		case "weeks-before-set-repeat":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			ldata.setWeeksBeforeSetRepeat(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
			GlobalData.saveData();
			return true;
		case "default-score":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			ldata.setDefaultScore(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`").queue();
			GlobalData.saveData();
			return true;
		case "K":
			valueD = parseDouble(params[2]);
			if (valueD < 0) {
				notNumber(event);
				return true;
			}
			ldata.setK(valueD);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueD+"`").queue();
			GlobalData.saveData();
			return true;
		case "auto-gen-pairs":
			if (params[2].equals("true")) valueB = true;
			else if (params[2].equals("false")) valueB = false;
			else {
				event.getChannel().sendMessage(Important.getInsult()+" is not true or false!").queue();
				return true;
			}
			ldata.autoGenPairs = valueB;
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueB+"`").queue();
			GlobalData.saveData();
			return true;
		case "auto-update-ranks":
			if (params[2].equals("true")) valueB = true;
			else if (params[2].equals("false")) valueB = false;
			else {
				event.getChannel().sendMessage(Important.getInsult()+" is not true or false!").queue();
				return true;
			}
			ldata.autoUpdateRanks = valueB;
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueB+"`").queue();
			GlobalData.saveData();
			return true;
		}
		event.getChannel().sendMessage(Important.getInsult()
				+" setting "+params[1]+" doesn't exist. Try `"+BotMain.PREFIX+"help`!").queue();
		return true;
	}
	
	private int parseInt(String param) {
		try {
			return Integer.parseInt(param);
		} catch (NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
	}
	
	private double parseDouble(String param) {
		try {
			return Double.parseDouble(param);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	private void notNumber(MessageReceivedEvent event) {
		event.getChannel().sendMessage(Important.getInsult()+" is not a number!").queue();
	}

}
