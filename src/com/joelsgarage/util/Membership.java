/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

/**
 * Represents fuzzy set membership.
 * 
 * @author joel
 * 
 */
public class Membership implements Comparable<Membership> {
    public static final Membership TRUE = new Membership(1.0);
    public static final Membership FALSE = new Membership(0.0);
    private final Double m;

    private Membership(double m) {
        this.m = Double.valueOf(m);
    }

    public static Membership newInstance(double m) {
        if (m < 0 || m > 1)
            return null;
        return new Membership(m);
    }

    public int compareTo(Membership other) {
        return getM().compareTo(other.getM());
    }

    public boolean isTrue() {
        if (compareTo(TRUE) < 0)
            return false;
        return true;
    }

    public boolean isFalse() {
        if (compareTo(FALSE) > 0)
            return false;
        return true;
    }

    public Membership negation() {
        return Membership.newInstance(1 - getM().doubleValue());
    }

    //

    public Double getM() {
        return this.m;
    }
}
