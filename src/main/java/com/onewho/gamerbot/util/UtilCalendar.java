package com.onewho.gamerbot.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Nullable;

public class UtilCalendar {

    public static final DateTimeFormatter TIME_DAY_SEC_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ssZ");
    public static final DateTimeFormatter TIME_DAY_SEC_NO_TZ_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("America/Chicago");

    public static int getCurrentWeek() {
        ZonedDateTime date = ZonedDateTime.now(DEFAULT_ZONE);
        return date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    public static int getCurrentYear() {
        ZonedDateTime date = ZonedDateTime.now(DEFAULT_ZONE);
        return date.getYear();
    }

    public static LocalDate getCurrentDate() {
        return ZonedDateTime.now(DEFAULT_ZONE).toLocalDate();
    }

    public static String getCurrentDateString() {
        return new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }

    public static String getCurrentDateTimeString() {
        return ZonedDateTime.now(DEFAULT_ZONE).format(TIME_DAY_SEC_FORMATTER);
    }

    public static int getWeek(int day, int month, int year) {
        ZonedDateTime date = ZonedDateTime.of(year, month, day, 0, 0, 0, 0, DEFAULT_ZONE);
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
            //System.out.println("ERROR: COULD NOT PARSE DATE "+dateString);
            return null;
        }
    }

    public static int getWeeksInYear(int year) {
        ZonedDateTime date = ZonedDateTime.of(year, 1, 1, 0, 0, 0, 0, DEFAULT_ZONE);
        return (int)IsoFields.WEEK_OF_WEEK_BASED_YEAR.rangeRefinedBy(date).getMaximum();
    }

    public static int getWeekDiff(String start, String end) {
        return getWeekDiff(getDate(start), getDate(end));
    }

    public static int getWeekDiff(LocalDate start, LocalDate end) {
        return (int)ChronoUnit.WEEKS.between(start, end);
    }

    public static int getWeekDiff(int w1, int y1, int w2, int y2) {
        ZonedDateTime startDate = weekYearToZonedDate(w1, y1);
        ZonedDateTime endDate = weekYearToZonedDate(w2, y2);
        return (int)ChronoUnit.WEEKS.between(startDate, endDate);
    }

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

    public static ZonedDateTime weekYearToZonedDate(int week, int year, int dayOfWeek) {
        if (dayOfWeek < 1) dayOfWeek = 1;
        else if (dayOfWeek > 7) dayOfWeek = 7;
        WeekFields wf = WeekFields.of(Locale.getDefault());
        ZonedDateTime zdt = ZonedDateTime.now(DEFAULT_ZONE)
                .withYear(year)
                .with(wf.weekOfYear(), week)
                .with(wf.dayOfWeek(), dayOfWeek);
        return zdt;
    }

    public static ZonedDateTime toDayInWeek(LocalDate date, int dayOfWeek) {
        return weekYearToZonedDate(getWeek(date), date.getYear(), dayOfWeek);
    }

    public static ZonedDateTime weekYearToZonedDate(int week, int year) {
        return weekYearToZonedDate(week, year, 1);
    }

    public static boolean isNewer(LocalDate d1, LocalDate d2) {
        return d1.isAfter(d2);
    }

    public static boolean isNewer(String d1, String d2) {
        return isNewer(getDate(d1), getDate(d2));
    }

    public static boolean isOlder(LocalDate d1, LocalDate d2) {
        return d1.isBefore(d2);
    }

    public static boolean isOlder(String d1, String d2) {
        return isOlder(getDate(d1), getDate(d2));
    }

    private static OffsetDateTime parseTime(String time) {
        try {
            return OffsetDateTime.parse(time, TIME_DAY_SEC_FORMATTER);
        } catch (DateTimeParseException e) {
            LocalDateTime ldt = LocalDateTime.parse(time, TIME_DAY_SEC_NO_TZ_FORMATTER);
            return ldt.atZone(DEFAULT_ZONE).toOffsetDateTime();
        }
    }

    public static boolean isWithin60Seconds(String createdTime) {
        OffsetDateTime created = parseTime(createdTime);
        Instant now = Instant.now();
        long secondsBetween = Math.abs(Duration.between(created.toInstant(), now).getSeconds());
        return secondsBetween <= 60;
    }

    public static boolean isAfterSeconds(String startTime, int seconds) {
        OffsetDateTime created = parseTime(startTime);
        Instant now = Instant.now();
        long secondsBetween = Math.abs(Duration.between(created.toInstant(), now).getSeconds());
        return secondsBetween > seconds;
    }

    public static boolean isOlderTime(String time1, String time2) {
        OffsetDateTime odt1 = parseTime(time1);
        OffsetDateTime odt2 = parseTime(time2);
        return odt1.isBefore(odt2);
    }

    public static String addSeconds(String time, int seconds) {
        OffsetDateTime start = parseTime(time);
        OffsetDateTime end = start.plusSeconds(seconds);
        return end.format(TIME_DAY_SEC_FORMATTER);
    }

    public static String toDiscordRelativeTime(String time) {
        if (time.isBlank()) return "";
        OffsetDateTime odt = parseTime(time);
        return "<t:"+odt.toEpochSecond()+":R>";
    }

    public static String toDiscordTime(String time) {
        if (time.isBlank()) return "";
        OffsetDateTime odt = parseTime(time);
        return "<t:"+odt.toEpochSecond()+":S>";
    }
}