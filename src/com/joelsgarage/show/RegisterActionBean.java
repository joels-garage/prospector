/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.action.Wizard;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.fetchers.ByNameHibernateRecordFetcherFactory;
import com.joelsgarage.dataprocessing.writers.HibernateRecordWriterFactory;
import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.prospector.client.model.User;

/**
 * Mimics the example register bean.
 * 
 * @author joel
 * 
 */
@Wizard(startEvents = "start")
@UrlBinding(value = "/register")
public class RegisterActionBean extends ShowActionBean {
	@ValidateNestedProperties( { //
	@Validate(field = "name", required = true, minlength = 5, maxlength = 20), //
	@Validate(field = "password", required = true, minlength = 5, maxlength = 20), //
	@Validate(field = "realName", required = true, maxlength = 50), //
	@Validate(field = "emailAddress", required = true, maxlength = 50) //
	})
	private User user;

	/** Must be the same as the password in the user record */
	@Validate(required = true, minlength = 5, maxlength = 20, expression = "this == user.password")
	private String confirmPassword;

	/** Looks up User records by name */
	private RecordFetcher<String, ModelEntity> fetcher;
	/** Writes the User record */
	private RecordWriter<ModelEntity> writer;
	/** Whence we came and so shall return. */
	private String targetUrl;

	/**
	 * Default constructor uses Hibernate.
	 * 
	 * TODO: eliminate this dependency. How do you inject Hibernate into Stripes instantiation?
	 * Maybe use Guice.
	 */
	public RegisterActionBean() {
		this( //
			new ByNameHibernateRecordFetcherFactory(null).newInstance(new ReaderConstraint(
				User.class)), //
			new HibernateRecordWriterFactory(null).newInstance(new ReaderConstraint(User.class)));
	}

	/** Constructor for testing; pass a RecordReader for user lookups */
	public RegisterActionBean(RecordFetcher<String, ModelEntity> fetcher,
		RecordWriter<ModelEntity> writer) {
		setFetcher(fetcher);
		setWriter(writer);
	}

	/** Ensures that the specified user name is not already taken. */
	@ValidationMethod
	public void validate(ValidationErrors errors) {
		ModelEntity entity = getFetcher().get(getUser().getName());

		if (entity != null)
			errors.add("user.name", new LocalizableError("usernameTaken")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Resolution gotoStep2() throws Exception {
		return new ForwardResolution("/register2.jsp"); //$NON-NLS-1$
	}

	@DontValidate
	public Resolution start() {
		return new ForwardResolution("/register.jsp"); //$NON-NLS-1$
	}

	/**
	 * Registers a new user, logs them in, and redirects them to the browse page.
	 * 
	 * TODO: redirect the user to wherever they came from, i.e. keep that original referrer in the
	 * session state or the request state or whatever.
	 */
	@DefaultHandler
	public Resolution register() {
		getUser().setCreatorKey(getCreatorKey());
		getUser().setKey(
			new ExternalKey(SHOW_NAMESPACE, ExternalKey.USER_TYPE, getUser().getName()));
		getUser().setLastModified(getIso8601Date());

		getWriter().write(getUser());

		getContext().setUser(getUser());
		getContext().getMessages().add(
			new LocalizableMessage(getClass().getName() + ".successMessage", // //$NON-NLS-1$
				getUser().getRealName(), getUser().getName()));

		if (getTargetUrl() != null) {
			return new RedirectResolution(getTargetUrl(), false);
		}
		return new RedirectResolution("/browse.jsp"); //$NON-NLS-1$
	}

	//

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getConfirmPassword() {
		return this.confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public RecordWriter<ModelEntity> getWriter() {
		return this.writer;
	}

	public void setWriter(RecordWriter<ModelEntity> writer) {
		this.writer = writer;
	}

	public RecordFetcher<String, ModelEntity> getFetcher() {
		return this.fetcher;
	}

	public void setFetcher(RecordFetcher<String, ModelEntity> fetcher) {
		this.fetcher = fetcher;
	}

	public String getTargetUrl() {
		return this.targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
}
