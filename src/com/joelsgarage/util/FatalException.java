package com.joelsgarage.util;

/**
 * Can't continue after this.
 */
public class FatalException extends Exception {
	private static final long serialVersionUID = 1L;

	public FatalException(String s) {
		super(s);
	}

	public FatalException(Exception e) {
		super(e);
	}
}