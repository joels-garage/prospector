/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util.dowser;

import com.joelsgarage.util.VisibleType;

/**
 * This class has the annotation but is the wrong type (i.e. not a subclass of ModelEntity), so it
 * should not be found.
 * 
 * @author joel
 * 
 */
@VisibleType("foo")
public class BadScanInput {
	// foo

}
