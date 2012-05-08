/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;


/**
 * Represents "fuzzy majority" of "most" of the inputs.
 * 
 * TODO: add a parameter for degree of "most"
 * 
 * @author joel
 */
public class MostOWAOperator extends FuzzyOWAOperator {
	/**
	 * The weights are as follows:
	 * <ul>
	 * <li>0 for j < 0.3
	 * <li>1 for j > 0.8,
	 * <li>inear in between.
	 * </ul>
	 */
	public MostOWAOperator() {
		super(0.3, 0.8);
	}
}
