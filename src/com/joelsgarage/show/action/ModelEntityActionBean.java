/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.show.model.DisplayModelEntity;
import com.joelsgarage.show.model.Instance;

/**
 * Base Action bean to display a ModelEntity instance.
 * 
 * Subclasses know how to display more than this, e.g. more fields, other entity lists referring to
 * this entity, etc.
 * 
 * @author joel
 */
@UrlBinding(value = "/model-entity")
public class ModelEntityActionBean extends JSPActionBean {
	/** The key for this instance, which is specified by parameter. */
	private ExternalKey key;
	private Instance<DisplayModelEntity> instance;

	public ModelEntityActionBean() {
		super();
		this.instance = new Instance<DisplayModelEntity>();
		// Base class calls initialize(), which resolves to an overridden one.
		initialize();
	}

	/**
	 * Subclasses should override this to set their own entity types.
	 */
	public void initialize() {
		setInstance(new DisplayModelEntity(true));
	}

	@Override
	protected String getJSP() {
		return "/model-entity.jsp"; //$NON-NLS-1$
	}

	@DefaultHandler
	public Resolution fetch() {
		getInstance().fetch(getKey());
		return new ForwardResolution(getJSP());
	}

	public ExternalKey getKey() {
		return this.key;
	}

	public void setKey(ExternalKey key) {
		this.key = key;
	}

	public DisplayModelEntity getInstance() {
		return this.instance.getInstance();
	}

	public void setInstance(DisplayModelEntity instance) {
		this.instance.setInstance(instance);
	}
}
