package com.onewho.gamerbot.command;

import com.onewho.gamerbot.data.GuildData;
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
			event.getChannel().sendMessage(Report.getInsult()
					+" do: `~config [setting] [value]`").queue();
			return true;
		}
		Guild guild = event.getGuild();
		GuildData gdata = LeagueData.getGuildDataById(guild.getIdLong());
		int value;
		double valueD;
		switch (params[1]) {
		case "max-sets-per-week":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			gdata.setMaxSetsPerWeek(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`");
			LeagueData.saveData();
			return true;
		case "weeks-before-auto-inactive":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			gdata.setWeeksBeforeAutoInactive(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`");
			LeagueData.saveData();
			return true;
		case "weeks-before-set-expires":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			gdata.setWeeksBeforeSetExpires(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`");
			LeagueData.saveData();
			return true;
		case "weeks-before-set-repeat":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			gdata.setWeeksBeforeSetRepeat(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`");
			LeagueData.saveData();
			return true;
		case "default-score":
			value = parseInt(params[2]);
			if (value == Integer.MIN_VALUE) {
				notNumber(event);
				return true;
			}
			gdata.setDefaultScore(value);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+value+"`");
			LeagueData.saveData();
			return true;
		case "K":
			valueD = parseDouble(params[2]);
			if (valueD < 0) {
				notNumber(event);
				return true;
			}
			gdata.setK(valueD);
			event.getChannel().sendMessage("`"+params[1]+"` set to `"+valueD+"`");
			LeagueData.saveData();
			return true;
		}
		event.getChannel().sendMessage(Report.getInsult()
				+" setting "+params[1]+" doesn't exist. Try `~help`!").queue();
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
		event.getChannel().sendMessage(Report.getInsult()+" is not a number!").queue();
	}

}
