/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.MeasurementQuantity;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/measurement-quantity-list")
public class MeasurementQuantityListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/measurement-quantity-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return MeasurementQuantity.class;
	}
}
