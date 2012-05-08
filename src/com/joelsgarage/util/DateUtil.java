/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This is just so I don't have to write the ISO 8601 format more than once.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public abstract class DateUtil {

    /** ISO 8601 version of the specified date */
    public static String formatDateToISO8601(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // ISO 8601
        return sdf.format(date);
    }

    /** Date corresponding to the current time */
    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    /** ISO 8601 version of the current time */
    public static String nowString() {
        return formatDateToISO8601(now());
    }

    // /** Set the last-modified field of the supplied entity to "now" */
    // public static void setLastModifiedNow(ModelEntity entity) {
    // entity.setLastModified(DateUtil.formatDateToISO8601(now()));
    // }

    /**
     * Milliseconds between the specified dates. Leap year, Daylight savings time, etc hoo-hah. We
     * throw an exception to allow the caller to decide how to interpret errors. Out of band
     * exception handling, ha!
     */
    public static long diff(String date1, String date2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // ISO 8601

        try {
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);

            return d1.getTime() - d2.getTime();
        } catch (ParseException exception) {
            Logger.getLogger(DateUtil.class).warn(
                "caught and threw parse exception for values: " + date1 + " and: " + date2);
            throw exception;
        }
    }
}
