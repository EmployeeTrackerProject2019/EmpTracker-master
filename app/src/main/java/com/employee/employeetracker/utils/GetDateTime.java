package com.employee.employeetracker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GetDateTime {
    public static String getFormattedDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DATE);

        switch (day % 10) {
            case 1:
                return new SimpleDateFormat("EEE MMMM d'st', yyyy '@' hh:mm aa", Locale.US).format(date);
            case 2:
                return new SimpleDateFormat("EEE MMMM d'nd', yyyy '@' hh:mm aa", Locale.US).format(date);
            case 3:
                return new SimpleDateFormat("EEE MMMM d'rd', yyyy '@' hh:mm aa", Locale.US).format(date);
            default:
                return new SimpleDateFormat("EEE MMMM d'th', yyyy '@' hh:mm aa", Locale.US).format(date);
        }

    }

}
