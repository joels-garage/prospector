/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import junit.framework.TestCase;
import net.sourceforge.stripes.action.ActionBeanContext;

/**
 * @author joel
 * 
 */
public class CalculatorActionBeanTest extends TestCase {

	public void testCalculator() throws Exception {
		CalculatorActionBean bean = new CalculatorActionBean();
		bean.setContext(new ActionBeanContext());
		bean.setNumberOne(2);
		bean.setNumberTwo(2);
		bean.addition();
		assertEquals("Oh man, our math must suck!", bean.getResult(), 4, 0.001); //$NON-NLS-1$
	}
}
