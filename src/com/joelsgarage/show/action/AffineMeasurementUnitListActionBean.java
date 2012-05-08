/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.AffineMeasurementUnit;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/affine-measurement-unit-list")
public class AffineMeasurementUnitListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/affine-measurement-unit-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return AffineMeasurementUnit.class;
	}
}
