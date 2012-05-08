/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.QuantityProperty;

@UrlBinding(value = "/quantity-property-list")
public class QuantityPropertyListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/quantity-property-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return QuantityProperty.class;
	}
}
