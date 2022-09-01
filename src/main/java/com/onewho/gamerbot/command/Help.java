package com.onewho.gamerbot.command;

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
		/*event.getChannel().sendMessage("hmmmm....").queue();
		event.getChannel().sendMessage("after some serious consideration...").queueAfter(2, TimeUnit.SECONDS);
		event.getChannel().sendMessage("I think your problem is...").queueAfter(5, TimeUnit.SECONDS);
		event.getChannel().sendMessage("I forgot").queueAfter(10, TimeUnit.SECONDS);
		event.getChannel().sendMessage("sorry!").queueAfter(11, TimeUnit.SECONDS);*/
		boolean isAdmin = event.getMember().hasPermission(Permission.ADMINISTRATOR);
		event.getChannel().sendMessage("__**All Users Commands**__"
				+ "\n`~help` does this"
				+ "\n`~report [set id] [your score] [opponent score] [opponent ping]`"
					+ " report one of your sets"
			).queue();
		if (!isAdmin) return true;
		event.getChannel().sendMessage("__**Admin Users Commands**__"
				+ "\n`~createleague [league name]` "
				+ "\n`~setup` Sets up channels/roles for this server's leagues. "
					+ " Run this command if you accidentally delete a channel."
				+ "\n`~config [setting] [value]`"
					+ " Settings: max-sets-per-week, weeks-before-auto-inactive, weeks-before-set-expires,"
					+ " weeks-before-set-repeat, default-score, K (elo K constant)"
				+ "\n`~reportadmin [set id] [p1 ping] [p1 score] [p2 ping] [p2 score]`"
					+ " override the results for these user's sets"
				+ "\n`~createset [p1 ping] [p2 ping]` create a set with these 2 users"
				+ "\n`~genpairs` Creates sets for users who joined the league."
				+ "\n`~updateranks` Updates users scores and ranks based on their reported sets."
				+ "\n`~backup` Puts all user and set data in a backup file."
				+ "\n`~readbackup` Upload a backup file with this command to restore old user/set data."
			).queue();
		return true;
	}

}
