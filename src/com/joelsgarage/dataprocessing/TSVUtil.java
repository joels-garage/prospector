/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.util.ClassUtil;

/**
 * TSV operations.
 * 
 * @author joel
 */
public abstract class TSVUtil {
    public static final String TAB = "\t"; //$NON-NLS-1$
    public static final String ESCAPED_TAB = "\\t"; //$NON-NLS-1$
    public static final String NEWLINE = "\n"; //$NON-NLS-1$
    public static final String ESCAPED_NEWLINE = "\\n"; //$NON-NLS-1$
    private static final String FILENAME_DELIMITER = "."; //$NON-NLS-1$

    /**
     * Escapes tabs and newlines and joins the fields in the row with tab separating them.
     */
    public static String toTSV(List<String> fields) {
        if (fields == null)
            return null;
        if (fields.size() == 0)
            return null;
        if (fields.size() == 1)
            return escape(fields.get(0));
        StringBuilder builder = new StringBuilder();
        builder.append(escape(fields.get(0)));
        for (int index = 1; index < fields.size(); ++index) {
            builder.append(TAB);
            builder.append(escape(fields.get(index)));
        }
        return builder.toString();
    }

    public static String escape(String input) {
        return input.replace(TAB, ESCAPED_TAB).replace(NEWLINE, ESCAPED_NEWLINE);
    }

    public static String unescape(String input) {
        return input.replace(ESCAPED_TAB, TAB).replace(ESCAPED_NEWLINE, NEWLINE);
    }

    /** Separates the row at tabs, and escapes tabs and newlines within each field */
    public static List<String> fromTSV(String row) {
        String[] fields = row.split(TAB);
        List<String> result = new ArrayList<String>();
        for (int index = 0; index < fields.length; ++index) {
            result.add(unescape(fields[index]));
        }
        return result;
    }

    public static String makeFileName(String basename, Class<?> payloadClass) {
        String className = ClassUtil.shortClassName(payloadClass);
        return basename + FILENAME_DELIMITER + className;
    }
}
