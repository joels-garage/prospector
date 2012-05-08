/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

/**
 * TNorm combines Memberships.
 * 
 * @author joel
 * 
 */
public abstract class TNorm implements Norm {
    
    public Membership f(Membership x, Membership y) {
        return t(x, y);
    }

    /** Return the t-norm of a and b */
    public abstract Membership t(Membership a, Membership b);

    /** Return the s-norm, AKA t-conorm, of a and b, using DeMorgan's law */
    public Membership s(Membership a, Membership b) {
        return t(a.negation(), b.negation()).negation();
    }
}
