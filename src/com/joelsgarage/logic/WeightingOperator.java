/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.util.Membership;

/**
 * A weighting operator is function f(p, x) of one parameter, p, and one fuzzy preference, x,
 * resulting in a fuzzy preference representing a greater- or lesser-value of preference, depending
 * on the parameter.
 * 
 * It is used to change the importance of a fuzzy preference, before being involved in a compromise.
 * 
 * The preference domain is [0,1], zero representing no importance, and one representing infinitely
 * important, "must-have" weight.
 * 
 * It should satisfy these properties:
 * <ul>
 * <li>f(0, x) = 0.5
 * <li>f(1, x) = {0 if x < 0.5, 1 if x > 0.5, 0.5 if x == 0.5}
 * <li>f(p, 0) = 0 (must-not-have preferences may not be changed)
 * <li>f(p, 1) = 1 (must-have preferences may not be changed)
 * <li>f(0, 0) = 0.25 (compromise)
 * <li>f(0, 1) = 0.75 (compromise)
 * <li>f(0.5, x) = x (middle weight => linear)
 * <li>f(p, 0.5) = 0.5 (indifference may not be changed)
 * </ul>
 * 
 * Rather than write a closed-form version of this function, I'll just interpolate on a table.
 * 
 * This function can be expressed in terms of the compromise operator, by converting the weight into
 * a preference to be compromised with. See below.
 * 
 * @author joel
 * 
 */
public class WeightingOperator {
    private CompromiseOperator f;
    private static final double EPSILON = 0.0001;

    // private static final double CELL = 0.1;

    public WeightingOperator() {
        this.f = new CompromiseOperator();
    }

    /**
     * 
     * @param p
     *            the weight [0,1]. 0.5 => no effect
     * @param x
     *            the preference value [0,1] 0.5 => indifferent
     * @return
     */
    double f(double p, double x) {
        if ((p < 0) || (x < 0) || (p > 1) || (x > 1)) {
            // throw something
            return 0.0;
        }
        if (((1 - p) < EPSILON) && (0.5 - EPSILON < x) && (x < 0.5 + EPSILON)) {
            return 0.5;
        }
        if (p < EPSILON && x < EPSILON)
            return 0.25;
        if (p < EPSILON && x > 1 - EPSILON)
            return 0.75;

        double result = 0.0;
        double compromiseX = 2 * (x - 0.5);
        if (compromiseX >= 0) {
            double compromiseF =
                this.f.f(Membership.newInstance(p), Membership.newInstance(compromiseX)).getM()
                    .doubleValue();
            result = (compromiseF / 2.0) + 0.5;
        } else {
            double compromiseF =
                this.f.f(Membership.newInstance(p), Membership.newInstance(-1 * compromiseX))
                    .getM().doubleValue();
            result = 0.5 - (compromiseF / 2.0);
        }
        return result;
    }
}
