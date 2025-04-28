package com.gad.msvc_oauth.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UtilsMethods {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private UtilsMethods() {
    }

    public static String dateTimeNowFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
        return LocalDateTime.now().format(formatter);
    }
}
