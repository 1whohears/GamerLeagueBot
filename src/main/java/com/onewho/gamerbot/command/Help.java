package com.onewho.gamerbot.command;

import com.onewho.gamerbot.BotMain;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help implements ICommand {
	
	@Override
	public boolean getNeedsAdmin() {
		return false;
	}
	
	@Override
	public String getCommandString() {
		return "help";
	}
	
	@Override
	public String getRequiredChannelName() {
		return "bot-commands";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		boolean isAdmin = event.getMember().hasPermission(Permission.ADMINISTRATOR);
		event.getChannel().sendMessage("__**All Users Commands**__"
				+ "\n`"+BotMain.PREFIX+"help` does this"
				+ "\n`"+BotMain.PREFIX+"report [set id] [your score] [opponent score] [opponent ping]`"
					+ " report one of your sets"
			).queue();
		if (!isAdmin) return true;
		event.getChannel().sendMessage("__**Admin Users Commands**__"
				+ "\n`"+BotMain.PREFIX+"createleague [league name]` "
				+ "\n`"+BotMain.PREFIX+"setup` Sets up channels/roles for this server's leagues. "
					+ " Run this command if you accidentally delete a channel."
				+ "\n`"+BotMain.PREFIX+"config [setting] [value]`"
					+ " Settings: `max-sets-per-week`, `weeks-before-auto-inactive`, `weeks-before-set-expires`,"
					+ " `weeks-before-set-repeat`, `default-score`, `K` (elo K constant), `auto-gen-pairs`, `auto-update-ranks`"
				+ "\n`"+BotMain.PREFIX+"reportadmin [set id] [p1 ping] [p1 score] [p2 ping] [p2 score]`"
					+ " override the results for these user's sets"
				+ "\n`"+BotMain.PREFIX+"createset [p1 ping] [p2 ping]` create a set with these 2 users"
				+ "\n`"+BotMain.PREFIX+"genpairs` Creates sets for users who joined the league."
				+ "\n`"+BotMain.PREFIX+"updateranks` Updates users scores and ranks based on their reported sets."
				+ "\n`"+BotMain.PREFIX+"backup` Puts all user and set data in a backup file."
				+ "\n`"+BotMain.PREFIX+"readbackup` Upload a backup file with this command to restore old user/set data."
			).queue();
		return true;
	}

}
