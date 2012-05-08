/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayUser;

/**
 * 
 * @author joel
 */
@UrlBinding(value = "/user")
public class UserActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/user.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayUser(true));
	}

}
