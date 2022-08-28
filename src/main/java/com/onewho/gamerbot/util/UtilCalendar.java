package com.onewho.gamerbot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Locale;

public class UtilCalendar {
	
	public static int getCurrentWeek() {
		LocalDate date = LocalDate.now();
		return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	public static int getCurrentYear() {
		LocalDate date = LocalDate.now();
		return date.getYear();
	}
	
	public static int getWeek(int day, int month, int year) {
		LocalDate date = LocalDate.of(year, month, day);
		return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	/**
	 * @param dateString format dd-MM-yyyy
	 */
	public static int getWeek(String dateString) {
		LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	/**
	 * @param dateString format dd-MM-yyyy
	 */
	public static int getYear(String dateString) {
		LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		return date.getYear();
	}
	
	public static int getWeeksInYear(int year) {
		LocalDate date = LocalDate.of(year, 1, 1);
		return (int)IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(date).getMaximum();
	}
	
	public static int getWeekDiff(int w1, int y1, int w2, int y2) {
		LocalDateTime startDate = getLDTofWY(w1,y1);
		LocalDateTime endDate = getLDTofWY(w2,y2);
		return (int)ChronoUnit.WEEKS.between(startDate, endDate);
	}
	
	private static LocalDateTime getLDTofWY(int week, int year) {
		WeekFields wf = WeekFields.of(Locale.getDefault());
		LocalDateTime ldt = LocalDateTime.now()
                .withYear(year)
                .with(wf.weekOfYear(), week)
                .with(wf.dayOfWeek(), 1);
		return ldt;
	}
	
}
