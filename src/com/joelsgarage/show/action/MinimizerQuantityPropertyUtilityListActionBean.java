/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.MinimizerQuantityPropertyUtility;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/minimizer-quantity-property-utility-list")
public class MinimizerQuantityPropertyUtilityListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/minimizer-quantity-property-utility-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return MinimizerQuantityPropertyUtility.class;
	}
}
