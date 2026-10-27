package com.example.fitnesschallenge.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String getDateDaysFromNow(int days) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, days);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return sdf.format(cal.getTime());
    }

    public static String formatDisplayDate(String dateStr) {
        try {
            SimpleDateFormat input = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            Date d = input.parse(dateStr);
            if (d != null) {
                SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                return output.format(d);
            }
        } catch (Exception ignored) {
        }
        return dateStr;
    }
}