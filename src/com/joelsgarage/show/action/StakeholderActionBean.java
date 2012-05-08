/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayStakeholder;

/**
 * 
 * @author joel
 */
@UrlBinding(value = "/stakeholder")
public class StakeholderActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/stakeholder.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayStakeholder(true));
	}
}
