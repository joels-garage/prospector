/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

/**
 * T(x,y) = x * y;
 * 
 * @author joel
 */
public class ProductTNorm extends TNorm {
    /**
     * Return the product tnorm (i.e. product of the memberships).
     * 
     * @see com.joelsgarage.util.TNorm#t(com.joelsgarage.util.Membership,
     *      com.joelsgarage.util.Membership)
     */
    @Override
    public Membership t(Membership a, Membership b) {
        return Membership.newInstance(a.getM().doubleValue() * b.getM().doubleValue());
    }
}
