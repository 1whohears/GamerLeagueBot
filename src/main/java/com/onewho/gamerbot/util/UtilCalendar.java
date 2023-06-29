package com.onewho.gamerbot.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class UtilCalendar {
	
	public static int getCurrentWeek() {
		LocalDate date = LocalDate.now();
		return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	public static int getCurrentYear() {
		LocalDate date = LocalDate.now();
		return date.getYear();
	}
	
	public static LocalDate getCurrentDate() {
		return LocalDate.now();
	}
	
	public static String getCurrentDateString() {
		return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	}
	
	public static String getCurrentDateTimeString() {
		return new SimpleDateFormat("yyyy-MM-dd HH-mm-ssZ").format(new Date());
	}
	
	public static int getWeek(int day, int month, int year) {
		LocalDate date = LocalDate.of(year, month, day);
		return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	/**
	 * @param dateString format dd-MM-yyyy
	 */
	public static int getWeek(String dateString) {
		return getDate(dateString).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	public static int getWeek(LocalDate date) {
		return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}
	
	/**
	 * @param dateString format dd-MM-yyyy
	 */
	public static int getYear(String dateString) {
		return getDate(dateString).getYear();
	}
	
	/**
	 * @param dateString format dd-MM-yyyy
	 */
	@Nullable
	public static LocalDate getDate(String dateString) {
		try {
			return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		} catch (DateTimeParseException e) {
			return null;
		}
	}
	
	public static int getWeeksInYear(int year) {
		LocalDate date = LocalDate.of(year, 1, 1);
		return (int)IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(date).getMaximum();
	}
	
	public static int getWeekDiff(String start, String end) {
		return getWeekDiff(getDate(start), getDate(end));
	}
	
	public static int getWeekDiff(LocalDate start, LocalDate end) {
		return (int)ChronoUnit.WEEKS.between(start, end);
	}
	
	public static int getWeekDiff(int w1, int y1, int w2, int y2) {
		LocalDateTime startDate = weekYearToLocalDate(w1,y1);
		LocalDateTime endDate = weekYearToLocalDate(w2,y2);
		return (int)ChronoUnit.WEEKS.between(startDate, endDate);
	}
	
	/**
	 * @param start
	 * @param end
	 * @param dayOfWeek
	 * @return
	 */
	public static int getWeekDiffByWeekDay(LocalDate start, LocalDate end, int dayOfWeek) {
		if (dayOfWeek < 1) dayOfWeek = 1;
		else if (dayOfWeek > 7) dayOfWeek = 7;
		return (int)ChronoUnit.WEEKS.between(
				toDayInWeek(start, dayOfWeek), 
				toDayInWeek(end, dayOfWeek));
	}
	
	public static int getWeekDiffByWeekDay(String start, String end, int dayOfWeek) {
		return getWeekDiffByWeekDay(getDate(start), getDate(end), dayOfWeek);
	}
	
	public static int getWeekDiffByWeekDayFromNow(String start, int dayOfWeek) {
		return getWeekDiffByWeekDay(getDate(start), getCurrentDate(), dayOfWeek);
	}
	
	/**
	 * @param week
	 * @param year
	 * @param dayOfWeek
	 * @return
	 */
	public static LocalDateTime weekYearToLocalDate(int week, int year, int dayOfWeek) {
		if (dayOfWeek < 1) dayOfWeek = 1;
		else if (dayOfWeek > 7) dayOfWeek = 7;
		WeekFields wf = WeekFields.of(Locale.getDefault());
		LocalDateTime ldt = LocalDateTime.now()
                .withYear(year)
                .with(wf.weekOfYear(), week)
                .with(wf.dayOfWeek(), dayOfWeek);
		return ldt;
	}
	
	/**
	 * @param date
	 * @param dayOfWeek
	 * @return
	 */
	public static LocalDateTime toDayInWeek(LocalDate date, int dayOfWeek) {
		return weekYearToLocalDate(getWeek(date), date.getYear(), dayOfWeek);
	}
	
	/**
	 * @param week
	 * @param year
	 * @return
	 */
	public static LocalDateTime weekYearToLocalDate(int week, int year) {
		return weekYearToLocalDate(week, year, 1);
	}
	
	public static boolean isNewer(LocalDate d1, LocalDate d2) {
		return d1.isAfter(d2);
	}
	
	/**
	 * @param dateString format dd-MM-yyyy
	 */
	public static boolean isNewer(String d1, String d2) {
		return isNewer(getDate(d1), getDate(d2));
	}
	
}
