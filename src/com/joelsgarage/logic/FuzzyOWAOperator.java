/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Parameterized OWA operator
 * 
 * Implements the following:
 * 
 * <pre>
 *        { 0           if r &lt; a,
 * Q(r) = { (r-a)/(b-a) if a &lt;= r &lt; b
 *        { 1           if r &gt;= b
 *        
 * </pre>
 * 
 * Per Yager, calculate weights as follows:
 * 
 * <pre>
 * w_i = Q(i/n) - Q((i-1)/n), i=1,...,n
 * </pre>
 * 
 * @author joel
 */
public class FuzzyOWAOperator extends OWAOperator {
	private Logger logger = null;
	private double a;
	private double b;

	/**
	 * @param a
	 *            maximum zero
	 * @param b
	 *            minimum one
	 */
	public FuzzyOWAOperator(double a, double b) {
		setLogger(Logger.getLogger(FuzzyOWAOperator.class));
		if (a > b) {
			// TODO: complain
		}
		setA(a);
		setB(b);
	}

	@SuppressWarnings("nls")
	@Override
	protected double[] W(int size) {
		double[] result = new double[size];
		Arrays.fill(result, 0.0);
		for (int index = 0; index < size; ++index) {
			// this i matches the expression above
			double i = index + 1;
			double Q1 = Q(i / size);
			double Q2 = Q((i - 1) / size);
			result[index] = Q1 - Q2;
			getLogger().info("i: " + String.valueOf(i) + " w: " + String.valueOf(result[index]));
		}
		return result;
	}

	@SuppressWarnings("nls")
	protected double Q(double r) {
		double result = 0.0;
		if (r < this.a) {
			result = 0.0;
		} else if ((r >= this.a) && (r < this.b)) {
			result = (r - this.a) / (this.b - this.a);
		} else if (r >= this.b) {
			result = 1.0;
		}
		getLogger().info("q of : " + String.valueOf(r) + " is: " + String.valueOf(result));
		return result;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public double getA() {
		return this.a;
	}

	public void setA(double a) {
		this.a = a;
	}

	public double getB() {
		return this.b;
	}

	public void setB(double b) {
		this.b = b;
	}
}
