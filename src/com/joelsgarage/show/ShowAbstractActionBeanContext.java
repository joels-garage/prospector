/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import com.joelsgarage.prospector.client.model.User;

import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.controller.StripesConstants;

/**
 * Context for the show application, manages user context.
 * 
 * @author joel
 * 
 */
public abstract class ShowAbstractActionBeanContext extends ActionBeanContext {
	/** Gets the currently logged in user, or null if no-one is logged in. */
	public abstract User getUser();

	/** Sets the currently logged in user. */
	public abstract void setUser(User user);

	/** Logs the user out by invalidating the session. */
	public abstract void logout();

	/** See STS-555 */
	@Override
	public String getSourcePage() {
		try {
			return super.getSourcePage();
		} catch (java.lang.IllegalArgumentException ex) {
			return getRequest().getParameter(StripesConstants.URL_KEY_SOURCE_PAGE);
		}
	}

}
