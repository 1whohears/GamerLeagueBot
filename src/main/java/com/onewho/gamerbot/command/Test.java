package com.onewho.gamerbot.command;

import com.onewho.gamerbot.util.UtilDebug;
import com.onewho.gamerbot.util.UtilKClosest;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Test extends AdminCommand {

	@Override
	public String getCommandString() {
		return "test";
	}

	@Override
	public String getHelp() {
		return "this is not for you";
	}

	@Override
	public boolean runCommand(MessageReceivedEvent event, String[] params) {
		if (!super.runCommand(event, params)) return false;
		int[] scores = new int[] {1010, 1010, 1010, 1009, 1000, 990, 990, 981};
		System.out.println("descending");
		for (int i = 0; i < scores.length; ++i) {
			UtilDebug.printIntArray(i+":"+scores[i],
					UtilKClosest.getKClosestIndexArray(scores, i, scores.length-1));
		}
		System.out.println("ascending");
		int[] scores2 = new int[] {981, 990, 990, 1000, 1009, 1010, 1010, 1010};
		for (int i = 0; i < scores2.length; ++i) {
			UtilDebug.printIntArray(i+":"+scores2[i],
					UtilKClosest.getKClosestIndexArray(scores2, i, scores2.length-1));
		}
		return true;
	}

}
