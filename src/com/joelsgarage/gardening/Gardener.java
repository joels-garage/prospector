/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.gardening;

import com.joelsgarage.util.FatalException;

/**
 * Mark gardener classes with this.
 * 
 * @author joel
 * 
 */
public interface Gardener {
	/** Hm, duplicates an unrelated "run()" in ProcessNode. Maybe that's ok. */
	/**
	 * @throws FatalException
	 *             if the run didn't complete
	 */
	public void run() throws FatalException;
}
