/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.StandardMeasurementUnit;

@UrlBinding(value = "/standard-measurement-unit-list")
public class StandardMeasurementUnitListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/standard-measurement-unit-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return StandardMeasurementUnit.class;
	}
}
