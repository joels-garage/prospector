/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.ShowActionBean;

/**
 * Just a redirector.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/")
public class HomePageActionBean extends ShowActionBean {
	// The JSP corresponding to this bean.
	private static final String INDEX_JSP = "/home-page.jsp"; //$NON-NLS-1$

	@DefaultHandler
	public Resolution view() {
		return new ForwardResolution(INDEX_JSP);
	}
}
