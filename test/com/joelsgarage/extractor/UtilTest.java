/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.extractor;

import junit.framework.TestCase;

import com.joelsgarage.extractor.Util.FatalException;

/**
 * Tests part of the Util class.
 * 
 * @author joel
 * 
 */
public class UtilTest extends TestCase {
	/**
	 * Verify that we can read a simple testdata file.
	 */
	public void testLoader() {
		try {
			String fileContents = Util.readFileToString("extractor/loader-test.html"); //$NON-NLS-1$
			assertEquals("this is a test of a test.", fileContents); //$NON-NLS-1$
		} catch (FatalException e) {
			e.printStackTrace();
			fail();
		}
	}
}
