/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationError;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.ByNameHibernateRecordFetcherFactory;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.User;
import com.joelsgarage.show.action.BrowseActionBean;

/**
 * Mimics the example login bean.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/login")
public class LoginActionBean extends ShowActionBean {
	@Validate(required = true)
	private String username;

	@Validate(required = true)
	private String password;

	/** This is actually sourcePage. no no it's not, it's really url. */
	private String targetUrl;

	/** Looks up User records by name */
	private RecordFetcher<String, ModelEntity> fetcher;

	/** Default constructor uses the Hibernate */
	public LoginActionBean() {
		this(new ByNameHibernateRecordFetcherFactory(null).newInstance(new ReaderConstraint(
			User.class)));
	}

	/** Constructor for testing; pass a RecordFetcher for user lookups */
	public LoginActionBean(RecordFetcher<String, ModelEntity> fetcher) {
		setFetcher(fetcher);
	}

	/** You arrive here to get the blank form */
	@DontValidate
	public Resolution start() {
		return new ForwardResolution("/login.jsp"); //$NON-NLS-1$
	}

	@DefaultHandler
	public Resolution login() {
		ModelEntity entity = getFetcher().get(getUsername());
		if (entity == null) {
			// This user name is not present in the db
			ValidationError error = new LocalizableError("usernameDoesNotExist"); //$NON-NLS-1$
			getContext().getValidationErrors().add("username", error); //$NON-NLS-1$
			Logger.getLogger(LoginActionBean.class).info("username does not exist"); //$NON-NLS-1$
			return getContext().getSourcePageResolution(); // show the submit page again
		}
		if (!(entity instanceof User)) {
			// This should never happen
			Logger.getLogger(LoginActionBean.class).info(
				"invalid type " + entity.getClass().getName()); //$NON-NLS-1$
			Logger.getLogger(LoginActionBean.class).info(
				"invalid type: " + entity.getClass().getName()); //$NON-NLS-1$
			return new ForwardResolution("/error/error.jsp"); //$NON-NLS-1$
		}
		User user = (User) entity;
		if (!user.getPassword().equals(getPassword())) {
			// Wrong password
			ValidationError error = new LocalizableError("incorrectPassword"); //$NON-NLS-1$
			getContext().getValidationErrors().add("password", error); //$NON-NLS-1$
			Logger.getLogger(LoginActionBean.class).info("wrong password: " + getPassword()); //$NON-NLS-1$
			return getContext().getSourcePageResolution();
		}

		getContext().setUser(user);

		// This target url thing doesn't work, because it gives me the JSP url rather than the
		// actual URL the
		// user requested. As such, the flow is different -- it doesn't have a bean. There should be
		// a way
		// to get the real URL, but until I figure out what that is, I'll just use the default.

		if (getTargetUrl() != null) {
			// now target url really is the url
			return new RedirectResolution(getTargetUrl(), false);
		}
		return new RedirectResolution(BrowseActionBean.class);
	}

	//

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTargetUrl() {
		return this.targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public RecordFetcher<String, ModelEntity> getFetcher() {
		return this.fetcher;
	}

	public void setFetcher(RecordFetcher<String, ModelEntity> fetcher) {
		this.fetcher = fetcher;
	}
}
