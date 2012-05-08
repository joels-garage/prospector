/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.model.ClassAxiom;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/class-axiom-list")
public class ClassAxiomListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/class-axiom-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return ClassAxiom.class;
	}
}
