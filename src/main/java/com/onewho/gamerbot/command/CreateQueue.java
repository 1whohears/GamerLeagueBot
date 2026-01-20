package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GlobalData;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.QueueData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
		return "`"+BotMain.PREFIX+getCommandString()+"`";
	}
	
	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
		return run(ldata, msg -> event.getChannel().sendMessage(msg).queue()) != null;
	}

    public static QueueData run(LeagueData ldata, Consumer<String> debugConsumer) {
        QueueData queue = ldata.createQueue();
        GlobalData.markReadyToSave();
        String message = "Successfully created queue "+queue.getId()+"!";
        debugConsumer.accept(message);
        return queue;
    }

}
