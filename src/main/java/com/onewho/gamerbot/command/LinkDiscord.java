package com.onewho.gamerbot.command;

import com.google.gson.JsonObject;
import com.onewho.gamerbot.BotMain;
import com.onewho.gamerbot.data.GuildData;
import com.onewho.gamerbot.data.Important;
import com.onewho.gamerbot.data.LeagueData;
import com.onewho.gamerbot.data.UserData;
import com.onewho.gamerbot.util.UtilCalendar;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.security.SecureRandom;

public class LinkDiscord extends LeagueCommand {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public boolean runCommand(MessageReceivedEvent event, String[] params, GuildData gdata, LeagueData ldata) {
        long selfId = event.getAuthor().getIdLong();
        UserData user = ldata.getUserDataById(selfId);
        if (user == null) {
            event.getChannel().sendMessage(Important.getError()+" You are not in this league!").queue();
            return false;
        }
        JsonObject userData = user.getExtraData();
        String linkCodeCreateTime = UtilCalendar.getCurrentDateTimeString();
        String linkCode = genSixDigitCode();
        userData.addProperty("linkCodeCreateTime", linkCodeCreateTime);
        userData.addProperty("linkCode", linkCode);
        event.getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage(
                "Your private account link code:\n__**"+linkCode+"**__")).queue();
        event.getChannel().sendMessage("A private link code was DM'd to you. " +
                "You have 60 seconds to use this code in game to link you game account to your discord account.").queue();
        return true;
    }

    public static String genSixDigitCode() {
        int code = RANDOM.nextInt(1_000_000);
        return String.format("%06d", code);
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
