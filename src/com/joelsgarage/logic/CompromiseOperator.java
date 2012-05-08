/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import com.joelsgarage.util.Membership;
import com.joelsgarage.util.Norm;

/**
 * A compromise operator is a function, f(x,y) of two fuzzy preferences, x and y, resulting in a
 * fuzzy preference representing the "compromise" between them.
 * 
 * It is used to determine the joint preference of two stakeholders, and also to combine preferences
 * on a single individual (from a single stakeholder).
 * 
 * It should satisfy these properties:
 * <ul>
 * <li>f(x, y) = f(y, x) (symmetric about one diagonal)
 * <li>f(1, y) = 1 (must-have persists)
 * <li>f(0, y) = 0 (must-not-have persists)
 * <li>f(0.5, y) = y (no-preference has no effect)
 * <li>f(1-x, 1-y) = 1-f(x,y) (antisymmetric about x+y=1, the other diagonal)
 * <li>f(1, 0) = 0.5 (compromise between extremes)
 * <li>f(x, 1-x) = 0.5 ("risk" neutral)
 * </ul>
 * 
 * 
 * 
 * A function that looks almost right is
 * 
 * f(x, y) = sqrt(x^(1/y)*y^(1/x)/(x*y))
 * 
 * but it doesn't have the second symmetry property above, nor does it have the neutral element 0.5
 * (!).
 * 
 * There's a little bit of literature on choosing these functions.
 * 
 * Rather than worry about the closed form, just interpolate on a table.
 * 
 * This is the quickest and dirtiest thing I can imagine.
 * 
 * TODO: incorporate optimism/pessimism measures, which change the shape of this function where the
 * arguments are different. That is, f(x, 1-x) > 0.5 for an optimist, and f(x, 1-x) < 0.5 for a
 * pessimist. The most optimistic function is f(x,y)=max(x,y), and the most pessimistic is
 * f(x,y)=min(x,y).
 * 
 * FIXME: Oh SHIT! Here's the right function:
 * 
 * f(x, y) = x * y / (x * y + (1 - x) * (1 - y))
 * 
 * From Gabbay, Metcalfe, Fuzzy Logics based on [0, 1)-continuous uninorms, Springer-Verlag 2006.
 * 
 * JESUS. So it's fixed now (jan 09).
 * 
 * @author joel
 */
public class CompromiseOperator implements Norm {
    private static final double EPSILON = 0.0001;

    public Membership f(Membership x, Membership y) {
        double xVal = x.getM().doubleValue();
        double yVal = y.getM().doubleValue();
        // Special cases for boundary conditions to avoid discontinuities.
        // The edges, outside the corners, are constant.
        if ((xVal < EPSILON && (1 - yVal) >= EPSILON) || (yVal < EPSILON && (1 - xVal) >= EPSILON))
            return Membership.newInstance(0.0);
        if (((1 - xVal) < EPSILON && yVal >= EPSILON) || ((1 - yVal) < EPSILON && xVal >= EPSILON))
            return Membership.newInstance(1.0);
        // If it's *really* deep in the corner, just return 0.5.
        if ((xVal < EPSILON && (1 - yVal) < EPSILON) || (yVal < EPSILON && (1 - xVal) < EPSILON))
            return Membership.newInstance(0.5);

        return Membership.newInstance(xVal * yVal / ((xVal * yVal) + (1 - xVal) * (1 - yVal)));
    }
}
