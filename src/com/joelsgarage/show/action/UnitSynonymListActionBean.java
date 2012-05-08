/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.UnitSynonym;

@UrlBinding(value = "/unit-synonym-list")
public class UnitSynonymListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/unit-synonym-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return UnitSynonym.class;
	}
}
