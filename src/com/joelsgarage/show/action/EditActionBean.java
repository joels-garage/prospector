/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.log4j.Logger;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.ShowActionBean;
import com.joelsgarage.show.model.DisplayModelEntity;
import com.joelsgarage.show.util.MultiList;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;

/**
 * Edit, i.e. update, an existing instance, showing its fields in a form. For foreign keys, uh,
 * crap, I have no idea. The range of most of the FK fields is big, i.e. requires pagination, which
 * is why the GWT thing seemed like such a good idea.
 * 
 * So maybe this would be a good Wizard application, i.e. each page is an FK field, i.e. a search
 * result, and you keep state across them.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/edit/{key.type}/{key.namespace}/{key.key}")
public class EditActionBean extends ShowActionBean {
	/** The key of the instance, or foreign key of the list. */
	private ExternalKey key;

	private Dowser dowser;

	/**
	 * Set true if the specified type is allowed. since it's an error, and we render a different
	 * jsp, maybe this isn't required.
	 */
	private boolean typeAllowed = false;

	/** How many records per page */
	@Validate(minvalue = 1)
	private int pageSize = -1;
	/** Zero-based page index */
	@Validate(minvalue = 0)
	private int page = -1;

	/**
	 * For LIST queries, the list.
	 */
	private MultiList multiList;

	/** For ENTITY and JOIN queries, the instance */
	private DisplayModelEntity instance;

	public EditActionBean() {
		setKey(new ExternalKey());
		setDowser(DowserFactory.newDowser());
		setMultiList(new MultiList());
	}

	protected String getJSP() {
		return "/browse.jsp"; //$NON-NLS-1$
	}

	protected String getErrorJSP() {
		return "/error/error.jsp"; //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	@DefaultHandler
	public Resolution view() {
		String specifiedType = getKey().getType();
		Logger.getLogger(BrowseActionBean.class).info("got type " + specifiedType);
		Class<? extends ModelEntity> specifiedClass = getDowser().getAllowedTypes().get(
			specifiedType);

		// If type is not specified, or if it's not an allowed type, you get the base type.
		if (specifiedClass == null) {
			specifiedClass = ModelEntity.class;
		}

		setTypeAllowed(true);
		Logger.getLogger(BrowseActionBean.class).info("valid type " + specifiedType);

		// OK, there's a key, so fetch that instance.

		// First we need to check for a valid join key, and if found, pass it along.

		Logger.getLogger(BrowseActionBean.class)
			.info("fetch instance for key " + getKey().getKey());

		// TODO: this "dereference everything" approach is pretty crazy when
		// it gets to be three deep. So instead, lazily dereference using active
		// accessors.
		setInstance(new DisplayModelEntity(specifiedClass, 3));
		getInstance().fetch(getKey());

		return new ForwardResolution(getJSP());
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public boolean isTypeAllowed() {
		return this.typeAllowed;
	}

	public void setTypeAllowed(boolean typeAllowed) {
		this.typeAllowed = typeAllowed;
	}

	public ExternalKey getKey() {
		return this.key;
	}

	public void setKey(ExternalKey key) {
		this.key = key;
	}

	public Dowser getDowser() {
		return this.dowser;
	}

	public void setDowser(Dowser dowser) {
		this.dowser = dowser;
	}

	public MultiList getMultiList() {
		return this.multiList;
	}

	public void setMultiList(MultiList multiList) {
		this.multiList = multiList;
	}

	public DisplayModelEntity getInstance() {
		return this.instance;
	}

	public void setInstance(DisplayModelEntity instance) {
		this.instance = instance;
	}
}
