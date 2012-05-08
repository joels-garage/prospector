/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayQuantityProperty;

/**
 * Action bean that displays a property record.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/quantity-property")
public class QuantityPropertyActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/quantity-property.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayQuantityProperty(true));
	}

}
