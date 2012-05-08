/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import org.apache.log4j.Logger;

/**
 * @author joel
 * 
 */

public class FooActionBean implements ActionBean {
	private ActionBeanContext context;

	@SuppressWarnings("nls")
	@DefaultHandler
	public Resolution view() {
		System.out.println("help me");
		Logger.getLogger(FooActionBean.class).info("FOO FOO FOO redirecting");
		System.exit(0);
		return new ForwardResolution("/show/internal/foo.jsp"); //$NON-NLS-1$
	}

	public ActionBeanContext getContext() {
		return this.context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}
}
