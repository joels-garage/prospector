/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.QuantityFact;

/**
 * @author joel
 */
@UrlBinding(value = "/quantity-fact-list")
public class QuantityFactListActionBean extends ModelEntityListActionBean {

	@Override
	protected String getJSP() {
		return "/quantity-fact-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return QuantityFact.class;
	}
}
