/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayMeasurementUnit;

/**
 * @author joel
 */
@UrlBinding(value = "/measurement-unit")
public class MeasurementUnitActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/measurement-unit.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayMeasurementUnit(true));
	}
}
