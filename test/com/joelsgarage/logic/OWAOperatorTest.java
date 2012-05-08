package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class OWAOperatorTest extends TestCase {
	public void testSimple() {
		List<Double> a = new ArrayList<Double>();
		a.add(new Double(0.0));
		a.add(new Double(0.0));
		a.add(new Double(10.0));
		a.add(new Double(10.0));

		OWAOperator o = new AverageOWAOperator();
		assertEquals(5.0, o.F(a).doubleValue(), 0.00001);

		o = new MinOWAOperator();
		assertEquals(0.0, o.F(a).doubleValue(), 0.00001);

		o = new MaxOWAOperator();
		assertEquals(10.0, o.F(a).doubleValue(), 0.00001);
	}
}