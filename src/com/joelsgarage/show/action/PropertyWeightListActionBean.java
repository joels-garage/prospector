/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.PropertyWeight;

@UrlBinding(value = "/property-weight-list")
public class PropertyWeightListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/property-weight-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return PropertyWeight.class;
	}
}
