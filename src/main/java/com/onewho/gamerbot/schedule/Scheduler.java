package com.onewho.gamerbot.schedule;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.onewho.gamerbot.data.GlobalData;

public class Scheduler {
	
	public static void init() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		//service.scheduleAtFixedRate(Scheduler::runWeekly, 0, 1, TimeUnit.of(ChronoUnit.WEEKS)); // doesn't work
		service.scheduleAtFixedRate(getDailyRun(), 0, 1, TimeUnit.DAYS);
	}
	
	/*private static void runWeekly() {
		Date date = new Date();
		System.out.println(new Date()+" running weekly tasks");
		GlobalData.genScheduledPairsForAllLeagues();
	}*/
	
	private static void runDaily() {
		System.out.println(new Date()+" running daily tasks");
		GlobalData.updateRanksForAllLeagues();
		GlobalData.genScheduledPairsForAllLeagues();
	}
	
	private static Runnable getDailyRun() {
		return new Runnable() {
			public void run() {
				runDaily();
			}
		};
	}
	
}
