package com.stelmah.steamanalysis.exchangerate.service.util;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    public static LocalDateTime toLocalDateTime(long unixTimestamp) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(unixTimestamp),
                ZoneId.systemDefault()
        );
    }

    public static LocalDateTime max(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null) {
            return dateTime2;
        }
        if (dateTime2 == null) {
            return dateTime1;
        }
        return dateTime1.isAfter(dateTime2) ? dateTime1 : dateTime2;
    }
}
