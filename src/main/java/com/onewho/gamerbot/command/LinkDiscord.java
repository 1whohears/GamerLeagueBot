package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class LinkDiscord extends LeagueCommand {

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        long selfId = event.getAuthor().getIdLong();
        UserData user = ldata.getUserDataById(selfId);
        if (user == null) {
            event.getChannel().sendMessage(Important.getError()+" You are not in this league!").queue();
            return false;
        }

        return true;
    }

    @Override
    public String getRequiredChannelName() {
        return "bot-commands";
    }

    @Override
    public boolean getNeedsTO() {
        return false;
    }

    @Override
    public String getCommandString() {
        return "linkdiscord";
    }

    @Override
    public String getHelp() {
        return "`"+BotMain.PREFIX+getCommandString()+"`";
    }
}
