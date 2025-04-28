package com.gad.msvc_customer.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class UtilsMethods {
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private UtilsMethods() {
    }

    public static String dateTimeNowFormatted() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN);
        return LocalDateTime.now().format(formatter);
    }

    public static UUID convertStringToUUID(String uuid) {
        return UUID.fromString(uuid);
    }
    public static String convertUUIDToString(UUID uuid) {
        return uuid.toString();
    }
}
