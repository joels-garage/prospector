/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

/**
 * Represents a problem with initializing some object; generally thrown by the constructor.
 * 
 * @author joel
 * 
 */
public class InitializationException extends Exception {
	private static final long serialVersionUID = 1L;

	public InitializationException(String s) {
		super(s);
	}

	public InitializationException(Exception e) {
		super(e);
	}

}
