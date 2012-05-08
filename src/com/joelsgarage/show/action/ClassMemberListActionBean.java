/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.model.ClassMember;
import com.joelsgarage.prospector.client.model.ModelEntity;

@UrlBinding(value = "/class-member-list")
public class ClassMemberListActionBean extends ModelEntityListActionBean {
	@Override
	protected String getJSP() {
		return "/class-member-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return ClassMember.class;
	}
}
