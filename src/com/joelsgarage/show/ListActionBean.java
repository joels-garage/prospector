/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

/**
 * trying the UrlBinding thing for clean URLs.
 * 
 * @author joel
 * 
 */
public class ListActionBean implements ActionBean {
	private ActionBeanContext context;

	//
	public ActionBeanContext getContext() {
		return this.context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}

}
