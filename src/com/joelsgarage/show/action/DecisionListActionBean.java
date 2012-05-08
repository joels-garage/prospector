/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.Decision;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/decision-list")
public class DecisionListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/decision-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Decision.class;
	}
}
