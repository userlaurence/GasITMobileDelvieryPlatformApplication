package com.example.gasitmobiledelvieryplatformapplication.util;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

public final class DateUtil {
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Manila");
    private static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;

    public static final String DISPLAY_DATE_AM_PM_PATTERN = "MMM d, yyyy hh:mm a";
    public static final String DISPLAY_DATE_TIME_PATTERN = "d/M/yyyy HH:mm";
    public static final String DISPLAY_DATE_PATTERN = "d/M/yyyy";

    private ZonedDateTime zonedDateTime;

    private DateUtil() {
        zonedDateTime = ZonedDateTime.now(DEFAULT_ZONE_ID);
    }

    private DateUtil(String dateStr, boolean isPartial) {
        DateTimeFormatter dateTimeFormatter;
        if (isPartial) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(DISPLAY_DATE_TIME_PATTERN);
            dateStr += " 00:00";
        } else {
            dateTimeFormatter = DEFAULT_DATE_TIME_FORMATTER;
        }
        zonedDateTime = ZonedDateTime.parse(dateStr, dateTimeFormatter.withZone(DEFAULT_ZONE_ID));
    }

    private DateUtil(long dateLong) {
        zonedDateTime = Instant.ofEpochMilli(dateLong).atZone(DEFAULT_ZONE_ID);
    }

    public static DateUtil now() {
        return new DateUtil();
    }

    public static DateUtil toDateTime(final String dateStr) {
        return new DateUtil(dateStr, false);
    }

    public static DateUtil toDateTime(final long dateLong) {
        return new DateUtil(dateLong);
    }

    public static DateUtil dateToDateTime(final String dateStr) {
        return new DateUtil(dateStr, true);
    }

    public void toStartOfDay() {
        zonedDateTime = zonedDateTime.with(LocalTime.MIN);
    }

    public void toEndOfDay() {
        zonedDateTime = zonedDateTime.with(LocalTime.MAX);
    }

    public void toPrevDay() {
        zonedDateTime = zonedDateTime.minusDays(1);
    }

    public void toNextDay() {
        zonedDateTime = zonedDateTime.plusDays(1);
    }

    public void toStartOfWeek() {
        zonedDateTime = zonedDateTime.with(LocalTime.MIN)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public void toEndOfWeek() {
        zonedDateTime = zonedDateTime.with(LocalTime.MAX)
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }

    public void toPrevWeek() {
        zonedDateTime = zonedDateTime.minusWeeks(1);
    }

    public void toNextWeek() {
        zonedDateTime = zonedDateTime.plusWeeks(1);
    }

    public void toStartOfMonth() {
        zonedDateTime = zonedDateTime.with(LocalTime.MIN)
                .with(TemporalAdjusters.firstDayOfMonth());
    }

    public void toEndOfMonth() {
        zonedDateTime = zonedDateTime.with(LocalTime.MAX)
                .with(TemporalAdjusters.lastDayOfMonth());
    }

    public void toPrevMonth() {
        zonedDateTime = zonedDateTime.minusMonths(1);
    }

    public void toNextMonth() {
        zonedDateTime = zonedDateTime.plusMonths(1);
    }

    public void toStartOfYear() {
        zonedDateTime = zonedDateTime.with(LocalTime.MIN)
                .with(TemporalAdjusters.firstDayOfYear());
    }

    public void toEndOfYear() {
        zonedDateTime = zonedDateTime.with(LocalTime.MAX)
                .with(TemporalAdjusters.lastDayOfYear());
    }

    public void toPrevYear() {
        zonedDateTime = zonedDateTime.minusYears(1);
    }

    public void toNextYear() {
        zonedDateTime = zonedDateTime.plusYears(1);
    }

    @NonNull
    public String toString() {
        return zonedDateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public String toString(final String pattern) {
        if (pattern == null) return toString();

        if (!(pattern.equals(DISPLAY_DATE_PATTERN) || pattern.equals(DISPLAY_DATE_AM_PM_PATTERN)
                || pattern.equals(DISPLAY_DATE_TIME_PATTERN)))
            throw new IllegalArgumentException("Must use DateUtil static patterns.");

        return zonedDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public long toMills() {
        return zonedDateTime.toInstant().toEpochMilli();
    }
}