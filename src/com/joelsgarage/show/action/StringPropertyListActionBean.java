/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.StringProperty;

@UrlBinding(value = "/string-property-list")
public class StringPropertyListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/string-property-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return StringProperty.class;
	}
}
