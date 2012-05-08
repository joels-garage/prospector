/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.regex.Pattern;

import com.joelsgarage.model.ExternalKey;

/**
 * Simple container for a regex pattern and a Measurement Unit key.
 * 
 * @author joel
 * 
 */
public class QuantityPattern {
	private Pattern pattern;
	private ExternalKey measurementUnitKey;

	public QuantityPattern(Pattern pattern, ExternalKey measurementUnitKey) {
		super();
		this.pattern = pattern;
		this.measurementUnitKey = measurementUnitKey;
	}

	public Pattern getPattern() {
		return this.pattern;
	}

	public ExternalKey getMeasurementUnitKey() {
		return this.measurementUnitKey;
	}

}
