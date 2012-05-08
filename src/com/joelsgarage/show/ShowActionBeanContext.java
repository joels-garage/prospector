/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import com.joelsgarage.prospector.client.model.User;

/**
 * Context implementation that uses the request HttpSession as the repository.
 * 
 * @author joel
 * 
 */

public class ShowActionBeanContext extends ShowAbstractActionBeanContext {
	@Override
	public User getUser() {
		return (User) getRequest().getSession().getAttribute("user"); //$NON-NLS-1$
	}

	@Override
	public void setUser(User user) {
		getRequest().getSession().setAttribute("user", user); //$NON-NLS-1$
	}

	@Override
	public void logout() {
		getRequest().getSession().invalidate();
	}
}