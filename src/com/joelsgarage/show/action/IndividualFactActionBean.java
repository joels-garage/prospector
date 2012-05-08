/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayIndividualFact;

/**
 * @author joel
 */
@UrlBinding(value = "/individual-fact")
public class IndividualFactActionBean extends FactActionBean {
	@Override
	protected String getJSP() {
		return "/individual-fact.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayIndividualFact(true));
	}
}
