package com.gmail.mariska.martin.mtginventory.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public final class Utils {
    private static final DateTimeZone czechTZ = DateTimeZone.forID("Europe/Prague");

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
        String mtgi_data_dir = System.getenv("MTGI_DATA_DIR");
        if (data_dir == null && mtgi_data_dir == null) {
            data_dir = ctx.getRealPath("/WEB-INF/") + File.separator;
        } else {
            return mtgi_data_dir;
        }
        return data_dir; //is with slash
    }

    /**
     * dir ../WEB-INF/
     * @param ctx servlet context
     * @return dir with slashe
     */
    public static String getWebInfDir(ServletContext ctx) {
        return ctx.getRealPath("/WEB-INF/") + File.separator;
    }

    /**
     * Vyrobi aktualni datum v lokalni zone prahy
     * @return vraci datum ceske time zone
     */
    public static DateTime createCzechDateTimeNow() {
        return DateTime.now(czechTZ);
    }

    /**
     * Only if both object has same fields and equals values in it, return false. Otherwise nulls states and diference
     * returns true.
     * 
     * @param obj1 objekt
     * @param obj2 objekt
     * @param fields atributy
     * @return zda je zmena nebo ne
     */
    public static boolean hasChange(Object obj1, Object obj2, String... fields) {
        List<String> asList = Arrays.asList(fields);
        for (String fieldName : asList) {
            try {
                Field f1 = getField(obj1, fieldName);
                Field f2 = getField(obj2, fieldName);
                if (f1 == null || f2 == null) {
                    return true;
                } else if (f1.get(obj1) instanceof BigDecimal
                        && (((BigDecimal) f1.get(obj1)).compareTo((BigDecimal) f2.get(obj2)) != 0)) {
                    return true;
                } else if (!f1.get(obj1).equals(f2.get(obj2))) {
                    return true;
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {
                //nop
            }
        }
        return false;
    }

    private static Field getField(Object obj1, String fieldName) {
        Class<? extends Object> c = obj1.getClass();
        Field declaredField = null;
        while (declaredField == null && c.getSuperclass() != null) {
            try {
                declaredField = c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                //nop
            }
            c = c.getSuperclass();
        }
        if (declaredField != null) {
            declaredField.setAccessible(true); // for reflection purposes only
        }
        return declaredField;
    }

    public static boolean isOpenshift() {
        return System.getenv("OPENSHIFT_DATA_DIR") != null;
    }

}
