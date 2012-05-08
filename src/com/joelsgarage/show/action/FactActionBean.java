/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayFact;

/**
 * Action bean that displays a Fact.
 * 
 * So, this thing needs to know that it should retrieve the following entities:
 * <ul>
 * <li> 1. the fact itself. Superclass does that.
 * <li> 2. the property corresponding, in order to display the name. So we should have a standalone
 * retriever class of some kind, to get single records.
 * <li> 3. the unit, if it's a measurement fact. again, use the standalone retriever.
 * </ul>
 * 
 * @author joel
 */
@UrlBinding(value = "/fact")
public class FactActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/fact.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayFact(true));
	}
}
