package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class FuzzyOWAOperatorTest extends TestCase {

	// make some test accessors for protected methods
	class TestFuzzyOWAOperator extends FuzzyOWAOperator {
		public TestFuzzyOWAOperator(double a, double b) {
			super(a, b);
		}

		public double[] testW(int size) {
			return W(size);
		}

		public double testQ(double r) {
			return Q(r);
		}
	}

	public void testQ() {
		TestFuzzyOWAOperator o;
		o = new TestFuzzyOWAOperator(0.25, 0.75);

		assertEquals(0.0, o.testQ(0.0), 0.001);
		assertEquals(0.0, o.testQ(0.1), 0.001);
		assertEquals(0.0, o.testQ(0.2), 0.001);
		assertEquals(0.1, o.testQ(0.3), 0.001);
		assertEquals(0.3, o.testQ(0.4), 0.001);
		assertEquals(0.5, o.testQ(0.5), 0.001);
		assertEquals(0.7, o.testQ(0.6), 0.001);
		assertEquals(0.9, o.testQ(0.7), 0.001);
		assertEquals(1.0, o.testQ(0.8), 0.001);
		assertEquals(1.0, o.testQ(0.9), 0.001);
		assertEquals(1.0, o.testQ(1.0), 0.001);
	}

	public void testW() {
		TestFuzzyOWAOperator o;
		o = new TestFuzzyOWAOperator(0.25, 0.75);

		// I have no idea why these weights are as specified here.
		// TODO: figure it out.
		double[] weights = o.testW(5);
		assertEquals(5, weights.length);
		assertEquals(0.0, weights[0], 0.001);
		assertEquals(0.3, weights[1], 0.001);
		assertEquals(0.4, weights[2], 0.001);
		assertEquals(0.3, weights[3], 0.001);
		assertEquals(0.0, weights[4], 0.001);
	}

	public void testF0() {
		TestFuzzyOWAOperator o;
		o = new TestFuzzyOWAOperator(0.25, 0.75);

		List<Double> x = new ArrayList<Double>();
		x.add(new Double(0.1));
		x.add(new Double(0.3));
		x.add(new Double(0.5));
		x.add(new Double(0.7));
		x.add(new Double(0.9));

		assertEquals(0.5, o.F(x).doubleValue(), 0.00001);
	}
	
	public void testF1() {
		TestFuzzyOWAOperator o;
		o = new TestFuzzyOWAOperator(0.25, 0.75);

		List<Double> x = new ArrayList<Double>();
		x.add(new Double(0.9));
		x.add(new Double(0.7));
		x.add(new Double(0.5));
		x.add(new Double(0.3));
		x.add(new Double(0.1));

		assertEquals(0.5, o.F(x).doubleValue(), 0.00001);
	}

	public void testF2() {
		TestFuzzyOWAOperator o;
		o = new TestFuzzyOWAOperator(0.25, 0.75);

		List<Double> x = new ArrayList<Double>();
		x.add(new Double(0.1));
		x.add(new Double(0.3));
		x.add(new Double(0.5));
		x.add(new Double(0.7));
		x.add(new Double(0.9));
		x.add(new Double(0.9));
		x.add(new Double(0.9));
		x.add(new Double(0.9));

		assertEquals(0.75, o.F(x).doubleValue(), 0.00001);
	}
	
	public void testF3() {
		TestFuzzyOWAOperator o;
		o = new TestFuzzyOWAOperator(0.25, 0.75);

		List<Double> x = new ArrayList<Double>();
		x.add(new Double(0.9));
		x.add(new Double(0.7));
		x.add(new Double(0.5));
		x.add(new Double(0.3));
		x.add(new Double(0.1));
		x.add(new Double(0.1));
		x.add(new Double(0.1));
		x.add(new Double(0.1));

		assertEquals(0.25, o.F(x).doubleValue(), 0.00001);
	}
}