package com.joelsgarage.logic;

import junit.framework.TestCase;

/**
 * This test involves the specific values that CompromiseOperator is configured with; maybe kinda
 * dumb, need to keep the test up with any tweaks of those values.
 * 
 * @author joel
 * 
 */
public class WeightingOperatorTest extends TestCase {

	@SuppressWarnings("nls")
	public void testSimple() {
		WeightingOperator f = new WeightingOperator();

		/**
		 * From the class definition
		 * <li>f(0, x) = 0.5
		 * <li>f(1, x) = {0 if x < 0.5, 1 if x > 0.5, 0.5 if x == 0.5}
		 * <li>f(p, 0) = 0 (must-not-have preferences may not be changed)
		 * <li>f(p, 1) = 1 (must-have preferences may not be changed)
		 * <li>f(0, 0) = 0.25 (compromise)
		 * <li>f(0, 1) = 0.75 (compromise)
		 * <li>f(0.5, x) = x (middle weight => linear)
		 * <li>f(p, 0.5) = 0.5 (indifference may not be changed)
		 */

		// f(0,x) = 0.5
		assertEquals(0.5, f.f(0.0, 0.2), 0.001);
		for (double x = 0.05; x < 1.0; x += 0.05) {
			assertEquals("x: " + String.valueOf(x), 0.5, f.f(0.0, x), 0.001);
		}

		// f(1,x) = 0 or 1 (see above)
		assertEquals(0.0, f.f(1.0, 0.2), 0.001);
		for (double x = 0.05; x < 0.45; x += 0.05) {
			assertEquals("x: " + String.valueOf(x), 0.0, f.f(1.0, x), 0.001);
		}
		assertEquals(0.5, f.f(1.0, 0.5), 0.001);
		assertEquals(1.0, f.f(1.0, 0.8), 0.001);
		for (double x = 0.55; x < 0.95; x += 0.05) {
			assertEquals("x: " + String.valueOf(x), 1.0, f.f(1.0, x), 0.001);
		}

		// f(p,0) = 0
		assertEquals(0.0, f.f(0.3, 0.0), 0.001);
		for (double p = 0.05; p < 0.95; p += 0.05) {
			assertEquals("p: " + String.valueOf(p), 0.0, f.f(p, 0), 0.001);
		}

		// f(p,1) = 1
		assertEquals(1.0, f.f(0.3, 1.0), 0.001);
		for (double p = 0.05; p < 0.95; p += 0.05) {
			assertEquals("p: " + String.valueOf(p), 1.0, f.f(p, 1.0), 0.001);
		}

		assertEquals(0.25, f.f(0, 0), 0.001);
		assertEquals(0.75, f.f(0, 1), 0.001);

		// f(0.5, x) = x
		assertEquals(1.0, f.f(0.3, 1.0), 0.001);
		for (double x = 0.05; x < 0.95; x += 0.05) {
			assertEquals("x: " + String.valueOf(x), x, f.f(0.5, x), 0.001);
		}

		// f(p, 0.5) = 0.5
		assertEquals(0.5, f.f(0.3, 0.5), 0.001);
		for (double p = 0.05; p < 0.95; p += 0.05) {
			assertEquals(0.5, f.f(p, 0.5), 0.001);
		}

		// some random points, high weight
		assertEquals(0.95, f.f(0.8, 0.85), 0.01);
		assertEquals(0.25, f.f(0.8, 0.4), 0.01);

		// low weight
		assertEquals(0.685, f.f(0.2, 0.85), 0.01);
		assertEquals(0.465, f.f(0.2, 0.4), 0.01);

	}
}