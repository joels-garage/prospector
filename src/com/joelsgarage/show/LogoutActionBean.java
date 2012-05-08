/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.action.HomePageActionBean;

/**
 * Mimics the example logout bean.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/logout")
public class LogoutActionBean extends ShowActionBean {
	private String targetUrl;
	public Resolution logout() throws Exception {
		getContext().logout();
		if (getTargetUrl() != null) {
			return new RedirectResolution(getTargetUrl(), false);
		}
		return new RedirectResolution(HomePageActionBean.class);
	}
	public String getTargetUrl() {
		return this.targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
}
