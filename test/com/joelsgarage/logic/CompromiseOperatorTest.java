package com.joelsgarage.logic;

import junit.framework.TestCase;

import com.joelsgarage.util.Membership;

/**
 * This test involves the specific values that CompromiseOperator is configured with; maybe kinda
 * dumb, need to keep the test up with any tweaks of those values.
 * 
 * @author joel
 * 
 */
public class CompromiseOperatorTest extends TestCase {

    @SuppressWarnings("nls")
    public void testSimple() {
        final CompromiseOperator f = new CompromiseOperator();

        /**
         * From the class definition
         * 
         * <li>f(x, y) = f(y, x) (symmetric about one diagonal)
         * <li>f(1, y) = 1 (must-have persists)
         * <li>f(0, y) = 0 (must-not-have persists)
         * <li>f(0.5, y) = y (no-preference has no effect)
         * <li>f(1-x, 1-y) = 1-f(x,y) (antisymmetric about x+y=1, the other diagonal) *
         * <li>f(x, 1-x) = 0.5 ("risk" neutral)
         */

        // f(x,y) = f(y,x)
        assertEquals(f.f(Membership.newInstance(0.2), Membership.newInstance(0.8)).getM()
            .doubleValue(), f.f(Membership.newInstance(0.8), Membership.newInstance(0.2)).getM()
            .doubleValue(), 0.001);
        for (double x = 0.0; x < 1.0; x += 0.05) {
            for (double y = 0.0; y < 1.0; y += 0.05) {
                assertEquals("x: " + String.valueOf(x) + " y: " + String.valueOf(y), f.f(
                    Membership.newInstance(x), Membership.newInstance(y)).getM().doubleValue(), f
                    .f(Membership.newInstance(y), Membership.newInstance(x)).getM().doubleValue(),
                    0.001);
            }
        }

        // f(1, y) = 1
        assertEquals(1.0, f.f(Membership.newInstance(1.0), Membership.newInstance(0.1)).getM()
            .doubleValue(), 0.001);
        assertEquals(1.0, f.f(Membership.newInstance(1.0), Membership.newInstance(0.5)).getM()
            .doubleValue(), 0.001);
        assertEquals(1.0, f.f(Membership.newInstance(1.0), Membership.newInstance(0.9)).getM()
            .doubleValue(), 0.001);
        for (double y = 0.1; y < 1.0; y += 0.05) {
            assertEquals("y: " + String.valueOf(y), 1.0, f.f(Membership.newInstance(1),
                Membership.newInstance(y)).getM().doubleValue(), 0.001);
        }

        // f(0, y) = 0
        assertEquals(0.0, f.f(Membership.newInstance(0.0), Membership.newInstance(0.1)).getM()
            .doubleValue(), 0.001);
        assertEquals(0.0, f.f(Membership.newInstance(0.0), Membership.newInstance(0.5)).getM()
            .doubleValue(), 0.001);
        assertEquals(0.0, f.f(Membership.newInstance(0.0), Membership.newInstance(0.9)).getM()
            .doubleValue(), 0.001);
        for (double y = 0.0; y < 1.0; y += 0.05) {
            assertEquals("y: " + String.valueOf(y), 0.0, f.f(Membership.newInstance(0),
                Membership.newInstance(y)).getM().doubleValue(), 0.001);
        }

        // f(0.5,y) = y
        assertEquals(0.45, f.f(Membership.newInstance(0.5), Membership.newInstance(0.45)).getM()
            .doubleValue(), 0.001);
        for (double y = 0.0; y < 1.0; y += 0.05) {
            assertEquals("y: " + String.valueOf(y), y, f.f(Membership.newInstance(0.5),
                Membership.newInstance(y)).getM().doubleValue(), 0.001);
        }

        // f(x, 0.5) = x (symmetry)
        assertEquals(0.45, f.f(Membership.newInstance(0.45), Membership.newInstance(0.5)).getM()
            .doubleValue(), 0.001);

        // f(1, 0) = 0.5
        assertEquals(0.5, f.f(Membership.newInstance(1), Membership.newInstance(0)).getM()
            .doubleValue(), 0.001);
        assertEquals(0.5, f.f(Membership.newInstance(0), Membership.newInstance(1)).getM()
            .doubleValue(), 0.001);

        // f(x, 1-x) = 0.5
        assertEquals(0.5, f.f(Membership.newInstance(0.25), Membership.newInstance(0.75)).getM()
            .doubleValue(), 0.001);
        // funny bounds here to escape the weirdness at the corners.
        for (double x = 0.15; x < 0.85; x += 0.05) {
            assertEquals("x: " + String.valueOf(x), 0.5, f.f(Membership.newInstance(x),
                Membership.newInstance(1 - x)).getM().doubleValue(), 0.001);
        }

        // some random points
        assertEquals(0.0220, f.f(Membership.newInstance(0.05), Membership.newInstance(0.30)).getM()
            .doubleValue(), 0.001);
        assertEquals(0.2247, f.f(Membership.newInstance(0.35), Membership.newInstance(0.35)).getM()
            .doubleValue(), 0.001);
        assertEquals(0.9496, f.f(Membership.newInstance(0.72), Membership.newInstance(0.88)).getM()
            .doubleValue(), 0.001);
    }
}