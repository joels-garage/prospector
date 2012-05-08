/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.MaximizerQuantityPropertyUtility;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/maximizer-quantity-property-utility-list")
public class MaximizerQuantityPropertyUtilityListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/maximizer-quantity-property-utility-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return MaximizerQuantityPropertyUtility.class;
	}
}
