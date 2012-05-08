/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.joelsgarage.util.Base64;

/**
 * Represents a single restriction in JSON, e.g.
 * 
 * "someproperty" : "somevalue"
 * 
 * "someQuantityProperty>>" : null
 * 
 * etc.
 * 
 * @author joel
 * 
 */
public class PropertyRestriction {
    /** Prefix indicating a reversed individual property */
    public static final byte REVERSE_BYTE = '!';
    public static final String REVERSE = new String(new byte[] { REVERSE_BYTE });

    /** Magic property that means ClassMember. Cannot be reversed or used with an operator. */
    static final String CLASS_MEMBER_PROPERTY = "TYPE"; //$NON-NLS-1$

    /** Limit the number of results returned. Cannot be reversed or used with an operator. */
    static final String LIMIT = "limit"; //$NON-NLS-1$

    /** Pair-valued, indicates the normalization for the membership */
    static final String SCORE = "score"; //$NON-NLS-1$

    /** Query corresponding to the outer scan */
    static final String SCAN = "scan"; //$NON-NLS-1$

    /** Array of queries corresponding to the scoring filters */
    static final String FILTER = "filter"; //$NON-NLS-1$

    private static final OperatorLengthComparator comparator = new OperatorLengthComparator();

    /** stripped */
    public final String property;
    public final boolean reverse;
    public final Operator operator;

    /** Verify that the reverse symbol won't clash with key serialization */
    static {
        byte[] alphabet = Base64.getAlphabet(Base64.URL_SAFE);
        for (byte b : alphabet) {
            assert b != REVERSE_BYTE : "clash on " + new String(new byte[] { b }); //$NON-NLS-1$
        }
    }

    /** Compares lengths of the string operators; useful for matching them */
    public static class OperatorLengthComparator implements Comparator<Operator> {
        @Override
        public int compare(Operator o1, Operator o2) {
            String op1 = o1.op;
            String op2 = o2.op;
            return Integer.valueOf(op1.length()).compareTo(Integer.valueOf(op2.length()));
        }
    }

    private PropertyRestriction(String property, boolean reverse, Operator operator) {
        this.property = property;
        this.reverse = reverse;
        this.operator = operator;
    }

    /**
     * Parse the raw property and return a PropertyRestriction.
     * 
     * @throws QueryException
     */
    public static PropertyRestriction newInstance(String raw) throws QueryException {
        try {
            if (raw == null)
                throw new QueryException("null raw property"); //$NON-NLS-1$
            boolean lReverse = false;
            String trimmed = raw.trim();
            if (trimmed.startsWith(REVERSE)) {
                lReverse = true;
                trimmed = trimmed.substring(REVERSE.length());
            }
            List<Operator> allOperators = new ArrayList<Operator>(EnumSet.allOf(Operator.class));
            Collections.sort(allOperators, Collections.reverseOrder(comparator));

            for (Operator o : allOperators) {
                Logger.getLogger(PropertyRestriction.class).info("op: " + o.op); //$NON-NLS-1$
            }

            for (Operator op : allOperators) {
                if (trimmed.endsWith(op.op)) {
                    // if we find one, we know that there are no others.
                    String prop = trimmed.substring(0, trimmed.length() - op.op.length());
                    if (prop.length() == 0)
                        throw new QueryException("blank property"); //$NON-NLS-1$
                    return new PropertyRestriction(prop, lReverse, op);
                }
            }
            // The default is equality; this should be caught above though.
            return new PropertyRestriction(trimmed, lReverse, Operator.EQUALS);
        } catch (IndexOutOfBoundsException e) {
            throw new QueryException(e);
        }
    }
}