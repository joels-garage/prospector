/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

/**
 * Top-navigation link list, goes on the left side.
 * 
 * @author joel
 * 
 */
public class GBarActionBean implements ActionBean {
	private ActionBeanContext context;

	@SuppressWarnings("nls")
	private List<DisplayLink> links = new ArrayList<DisplayLink>() {
		private static final long serialVersionUID = 1L;
		{
			add(new DisplayLink("http://foo", "Link"));
			add(new DisplayLink("http://foo", "Linky"));
			add(new DisplayLink("http://foo", "Sprinky"));
			add(new DisplayLink("http://foo", "Clicky"));
			add(new DisplayLink("http://foo", "Licky"));
		}
	};

	public ActionBeanContext getContext() {
		return this.context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = context;
	}

	public List<DisplayLink> getLinks() {
		return this.links;
	}

	public void setLinks(List<DisplayLink> links) {
		this.links = links;
	}
}
