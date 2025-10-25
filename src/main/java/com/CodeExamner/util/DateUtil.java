// util/DateUtil.java
package com.CodeExamner.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(formatter) : "";
    }

    public static LocalDateTime parse(String dateString) {
        return dateString != null ? LocalDateTime.parse(dateString, formatter) : null;
    }

    public static boolean isWithinRange(LocalDateTime time, LocalDateTime start, LocalDateTime end) {
        return time != null && start != null && end != null &&
                !time.isBefore(start) && !time.isAfter(end);
    }
}