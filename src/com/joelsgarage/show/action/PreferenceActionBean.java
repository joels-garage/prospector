/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayPreference;

/**
 * 
 * @author joel
 */
@UrlBinding(value = "/preference")
public class PreferenceActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/preference.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayPreference(true));
	}
}
