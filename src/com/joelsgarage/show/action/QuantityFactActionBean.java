/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayQuantityFact;

/**
 * You know, this doesn't need to match the hierarchy, everything can inherit from a single base.
 * 
 * @author joel
 */
@UrlBinding(value = "/quantity-fact")
public class QuantityFactActionBean extends FactActionBean {
	@Override
	protected String getJSP() {
		return "/quantity-fact.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayQuantityFact(true));
	}
}
