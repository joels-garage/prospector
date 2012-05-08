/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

/**
 * @author joel
 * 
 */
public class Min implements Norm {
    /*
     * @see com.joelsgarage.util.Norm#f(com.joelsgarage.util.Membership,
     *      com.joelsgarage.util.Membership)
     */
    @Override
    public Membership f(Membership x, Membership y) {
        return Membership.newInstance(Math.min(x.getM().doubleValue(), y.getM().doubleValue()));
    }
}
