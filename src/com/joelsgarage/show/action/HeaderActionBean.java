/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import java.util.ArrayList;
import java.util.List;

import com.joelsgarage.show.ShowActionBean;

/**
 * @author joel
 * 
 */
public class HeaderActionBean extends ShowActionBean {
	@SuppressWarnings("nls")
	private List<String> things = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add("hi");
			add("bye");
		}
	};

	public List<String> getThings() {
		return this.things;
	}

	public void setThings(List<String> things) {
		this.things = things;
	}
}
