/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.IndividualFact;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * @author joel
 */
@UrlBinding(value = "/individual-fact-list")
public class IndividualFactListActionBean extends ModelEntityListActionBean {

	@Override
	protected String getJSP() {
		return "/individual-fact-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualFact.class;
	}
}
