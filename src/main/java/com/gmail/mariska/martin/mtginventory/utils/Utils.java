package com.gmail.mariska.martin.mtginventory.utils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;

public final class Utils {
    private Utils() {
    }

    public static Date cutoffTime(Date in) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(in);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return new Date(cal.getTimeInMillis());
    }

    public static Date cutoffToMonday(Date in) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.setTime(in);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return new Date(cal.getTimeInMillis());
    }

    public static Date dayAdd(Date in, int addUnits) {
        Calendar cal = Calendar.getInstance(); // locale-specific
        cal.setTime(in);
        cal.add(Calendar.DATE, addUnits);
        return new Date(cal.getTimeInMillis());
    }

    public static String getDataDir(ServletContext ctx) {
        String data_dir = System.getenv("OPENSHIFT_DATA_DIR");
        if (data_dir == null) {
            data_dir = ctx.getRealPath("/WEB-INF/") + File.separator;
        }
        return data_dir;
    }

}
