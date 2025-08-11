package dev.library.core.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Вспомогательный класс для работы с датой и временем
 */
public class DateUtil {
    private final static String DATE_TIME_PATTERN = "dd.MM.yyyy HH:mm";

    /**
     * Форматирование даты и времени
     * @param dateTime - объект типа {@link OffsetDateTime}
     */
    public static String formatDate(OffsetDateTime dateTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        return dateFormatter.format(dateTime);
    }
}
