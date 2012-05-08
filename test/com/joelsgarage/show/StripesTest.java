/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.sourceforge.stripes.controller.DispatcherServlet;
import net.sourceforge.stripes.controller.StripesFilter;
import net.sourceforge.stripes.mock.MockRoundtrip;
import net.sourceforge.stripes.mock.MockServletContext;

/**
 * @author joel
 * 
 */
public class StripesTest extends TestCase {

	private static MockServletContext context;

	@Override
	@SuppressWarnings("nls")
	protected void setUp() {
		context = new MockServletContext("test");

		// Add the Stripes Filter
		Map<String, String> filterParams = new HashMap<String, String>();
		// filterParams.put("ActionResolver.UrlFilters", "tests");
		filterParams.put("ActionResolver.Packages", "com.joelsgarage.show");
		context.addFilter(StripesFilter.class, "StripesFilter", filterParams);

		// Add the Stripes Dispatcher
		context.setServlet(DispatcherServlet.class, "StripesDispatcher", null);
	}

	public static MockServletContext getServletContext() {
		return StripesTest.context;
	}

	@SuppressWarnings("nls")
	public void testPositive() throws Exception {
		// Setup the servlet engine
		MockServletContext ctx = StripesTest.getServletContext();

		MockRoundtrip trip = new MockRoundtrip(ctx, CalculatorActionBean.class);
		trip.setParameter("numberOne", "2");
		trip.setParameter("numberTwo", "2");
		trip.execute();

		CalculatorActionBean bean = trip.getActionBean(CalculatorActionBean.class);
		Assert.assertEquals(bean.getResult(), 4.0, 0.001);
		Assert.assertEquals("/quickstart/index.jsp", trip.getDestination());
	}
}
