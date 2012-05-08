/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.Message;

import com.joelsgarage.prospector.client.model.User;

/**
 * @author joel
 * 
 */
public class MyMockActionBeanContext extends ShowAbstractActionBeanContext {
	private Map<String, Object> fakeSession = new HashMap<String, Object>();
	private List<Message> messageList = new ArrayList<Message>();

	@Override
	public void setUser(User user) {
		this.fakeSession.put("user", user); //$NON-NLS-1$
	}

	@Override
	public User getUser() {
		return (User) this.fakeSession.get("user"); //$NON-NLS-1$
	}

	@Override
	public void logout() {
		this.fakeSession.clear();
	}

	/** This is used in getSourcePageResolution, which we use */
	@Override
	public String getSourcePage() {
		return "foo"; //$NON-NLS-1$
	}

	@Override
	public List<Message> getMessages() {
		return this.messageList;
	}
}