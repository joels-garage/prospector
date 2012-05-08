/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.Individual;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/individual-list")
public class IndividualListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/individual-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Individual.class;
	}
}
