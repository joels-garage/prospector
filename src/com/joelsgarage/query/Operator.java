package com.joelsgarage.query;

import java.util.EnumSet;

import com.joelsgarage.util.Base64;

/**
 * An operator within a query; it's appended to the property name, e.g.:
 * 
 * "aProperty>" : 5
 * 
 * means "the individual is a member of the result set if there exists a fact for the specified
 * property with this individual as subject, and value strictly greater than the specified value
 * (5).
 * 
 * @author joel
 */
public enum Operator {
    /** default operator has no string: m=1 if equal, 0 otherwise */
    EQUALS(""), //$NON-NLS-1$
        /** m=1 if strictly below operand */
        BELOW("<"), //$NON-NLS-1$
        /** m=1 if strictly above operand */
        ABOVE(">"), //$NON-NLS-1$
        /** m=1 if at max, 0 if min, linear in between */
        MAXIMIZE(">>"), //$NON-NLS-1$
        /** m=1 if at min, 0 if max, linear in between */
        MINIMIZE("<<"), //$NON-NLS-1$
        /** m=1 if at operand, 0 if max or min, linear in between */
        OPTIMIZE("@@"); //$NON-NLS-1$

    /** The string representation of the operator */
    public final String op;

    /** Verify that operator symbols won't clash with key serialization */
    static {
        byte[] alphabet = Base64.getAlphabet(Base64.URL_SAFE);
        for (byte b : alphabet) {
            for (Operator o : EnumSet.allOf(Operator.class)) {
                for (byte ob : o.op.getBytes()) {
                    assert ob != b : "clash on " + o.toString(); //$NON-NLS-1$
                }
            }
        }
    }

    private Operator(String op) {
        this.op = op;
    }
}