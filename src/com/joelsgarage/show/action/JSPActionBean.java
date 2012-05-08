/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

import com.joelsgarage.show.ShowActionBean;

/**
 * A bean that forwards to a JSP. Base class for everything else.
 * 
 * @author joel
 */
public abstract class JSPActionBean extends ShowActionBean {

	public JSPActionBean() {
		// foo
	}

	/**
	 * The default handler forwards to the JSP specified by the subclass.
	 * 
	 * @return the ForwardResolution specified by getJSP().
	 */
	@DefaultHandler
	public Resolution view() {
		return new ForwardResolution(getJSP());
	}

	/**
	 * @return The JSP page associated with this ActionBean.
	 */
	protected abstract String getJSP();
}
