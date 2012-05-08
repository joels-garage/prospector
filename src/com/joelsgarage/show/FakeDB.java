/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import com.joelsgarage.prospector.client.model.User;

/**
 * For testing.
 * 
 * @author joel
 * 
 */
public class FakeDB {
	@SuppressWarnings("nls")
	public User validate(String username, String password) {
		if (username.equals("shaggy") && password.equals("shaggy")) {
			User user = new User();
			user.setName("Shaggy");
			user.setRealName("Rogers");
			return user;
		}
		return null;
	}
}
