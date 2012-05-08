/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.StringFact;

/**
 * @author joel
 */
@UrlBinding(value = "/string-fact-list")
public class StringFactListActionBean extends ModelEntityListActionBean {

	@Override
	protected String getJSP() {
		return "/string-fact-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return StringFact.class;
	}
}
