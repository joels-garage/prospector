/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.IndividualProperty;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/individual-property-list")
public class IndividualPropertyListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/individual-property-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualProperty.class;
	}
}
