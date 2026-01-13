package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.*;
import com.onewho.gamerbot.util.UtilCalendar;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.function.Consumer;

public class CreateQueue extends LeagueCommand {
	
	@Override
	public boolean getNeedsTO() {
		return true;
	}

	@Override
	public String getCommandString() {
		return "createqueue";
	}

	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}
	
	@Override
	public String getHelp() {
		return "`"+BotMain.PREFIX+getCommandString()+" [team_size] (end_time_day | dd-MM-yyyy) (end_time_time | HH-mm)`";
	}
	
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		if (!(params.length == 4 || params.length == 2)) {
			event.getChannel().sendMessage(Important.getError()+" DO: "+getHelp()).queue();
			return false;
		}
        int teamSize;
        try {
            teamSize = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(Important.getError()+" "+params[1]+" is not a number!").queue();
            return false;
        }
        String endTime = null;
        if (params.length == 4) {
            endTime = params[2] + " " + params[3];
            LocalDate date = UtilCalendar.getDate(endTime);
            if (date == null) {
                event.getChannel().sendMessage(Important.getError()+" "+endTime+" is not a valid time format!" +
                                " DO: `dd-MM-yyyy` `HH-mm`").queue();
                return false;
            }
        }
		return run(ldata, msg -> event.getChannel().sendMessage(msg).queue(), teamSize, endTime) != null;
	}

    public static QueueData run(LeagueData ldata, Consumer<String> debugConsumer,
                                int teamSize, @Nullable String endTime) {
        QueueData queue = ldata.createQueue(teamSize, endTime);
        GlobalData.saveData();
        String message = "Successfully created queue "+queue.getId()+"!";
        if (endTime != null) message += " Will close at "+endTime+"!";
        debugConsumer.accept(message);
        return queue;
    }

}
