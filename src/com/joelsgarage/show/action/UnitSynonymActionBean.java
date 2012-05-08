/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayUnitSynonym;

/**
 * 
 * @author joel
 */
@UrlBinding(value = "/unit-synonym")
public class UnitSynonymActionBean extends ModelEntityActionBean {
	@Override
	protected String getJSP() {
		return "/unit-synonym.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayUnitSynonym(true));
	}
}
