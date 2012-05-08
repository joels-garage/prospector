/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.IndividualPropertyUtility;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/individual-property-utility-list")
public class IndividualPropertyUtilityListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/individual-property-utility-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return IndividualPropertyUtility.class;
	}
}
