package com.example.takunaka.taskapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by takunaka on 21.09.17.
 */

public class Utils {

    private final static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);

    /**
     * Метод преобразования полученной в long
     *
     * @param time входящий параметр вида dd.MM.yyyy
     * @return возвращает время в unixTimeStamp
     */
    public static long getUnixTime(String time) {
        Date formatDate = null;
        try {
            formatDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (formatDate != null) {
            return formatDate.getTime() / 1000;
        } else {
            return 0;
        }
    }

    /**
     * Обратный метод
     *
     * @param unixTime параметр времени в unix формате
     * @return возвращает строку вида dd.MM.yyyy
     */
    public static String getStringDate(int unixTime) {
        return sdf.format(new Date(unixTime * 1000L));
    }
}
