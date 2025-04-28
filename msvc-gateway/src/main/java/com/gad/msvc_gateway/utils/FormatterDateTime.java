package com.gad.msvc_gateway.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatterDateTime {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private FormatterDateTime() {
    }

    public static String dateTimeNowFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
        return LocalDateTime.now().format(formatter);
    }
}
