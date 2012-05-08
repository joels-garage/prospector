/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayDecision;

/**
 * Action bean that displays a decision.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/decision")
public class DecisionActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/decision.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayDecision(true));
	}
}
