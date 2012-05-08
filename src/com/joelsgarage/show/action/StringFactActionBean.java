/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayQuantityFact;

/**
 * @author joel
 */
@UrlBinding(value = "/string-fact")
public class StringFactActionBean extends FactActionBean {
	@Override
	protected String getJSP() {
		return "/string-fact.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayQuantityFact(true));
	}
}
