package com.onewho.gamerbot.schedule;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.onewho.gamerbot.data.GlobalData;

public class Scheduler {
	
	public static void init() {
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		scheduleDaily(service);
        scheduleOften(service);
	}

    private static void runDaily() {
        try {
            System.out.println("RUNNING DAILY TASKS " + ZonedDateTime.now(ZoneId.of("America/Chicago")));
            GlobalData.updateRanksForAllLeagues();
            GlobalData.genScheduledPairsForAllLeagues();
            GlobalData.markReadyToSave();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runOften() {
        try {
            GlobalData.updateQueuesForAllLeagues();
            GlobalData.saveData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private static void scheduleDaily(ScheduledExecutorService service) {
		ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Chicago"));
		ZonedDateTime next = now.withHour(6).withMinute(42).withSecond(0);
		if (now.compareTo(next) > 0) next = next.plusDays(1);
		Duration duration = Duration.between(now, next);
		long delay = duration.getSeconds();
		service.scheduleAtFixedRate(Scheduler::runDaily, delay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	}

    private static void scheduleOften(ScheduledExecutorService service) {
        service.scheduleAtFixedRate(Scheduler::runOften, 0L, 15L, TimeUnit.SECONDS);
    }
	
}
