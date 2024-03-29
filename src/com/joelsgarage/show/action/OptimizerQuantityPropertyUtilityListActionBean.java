/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.OptimizerQuantityPropertyUtility;

@UrlBinding(value = "/optimizer-quantity-property-utility-list")
public class OptimizerQuantityPropertyUtilityListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/optiimizer-quantity-property-utility-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return OptimizerQuantityPropertyUtility.class;
	}
}
