/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.util.ClassUtil;
import com.joelsgarage.util.DateUtil;

/**
 * Base ActionBean implementation for ActionBeans in the Show application.
 * 
 * Stores an ActionBeanContext as a member field.
 * 
 * Yay implementation inheritance.
 * 
 * @author joel
 */
public abstract class ShowActionBean implements ActionBean {
	/** Default Namespace for stuff the show app writes */
	protected static final String SHOW_NAMESPACE = "show"; //$NON-NLS-1$
	private ShowAbstractActionBeanContext context;
	private String shortClassName;
	// TODO: remove this
	private String iso8601Date;
	private ExternalKey creatorKey;

	public ShowActionBean() {
		setShortClassName(ClassUtil.shortClassName(this.getClass()));
		// TODO: remove this
		setIso8601Date(DateUtil.formatDateToISO8601(DateUtil.now()));
		setCreatorKey(new ExternalKey(SHOW_NAMESPACE, ExternalKey.USER_TYPE, getShortClassName()));
	}

	public ShowAbstractActionBeanContext getContext() {
		return this.context;
	}

	public void setContext(ActionBeanContext context) {
		this.context = (ShowAbstractActionBeanContext) context;
	}

	public String getShortClassName() {
		return this.shortClassName;
	}

	public void setShortClassName(String shortClassName) {
		this.shortClassName = shortClassName;
	}

	public String getIso8601Date() {
		return this.iso8601Date;
	}

	public void setIso8601Date(String iso8601Date) {
		this.iso8601Date = iso8601Date;
	}

	public ExternalKey getCreatorKey() {
		return this.creatorKey;
	}

	public void setCreatorKey(ExternalKey creatorKey) {
		this.creatorKey = creatorKey;
	}
}
