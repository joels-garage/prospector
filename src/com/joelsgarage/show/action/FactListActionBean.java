/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.prospector.client.model.Fact;
import com.joelsgarage.prospector.client.model.ModelEntity;

/**
 * This thing has to somehow prepare the polymorphic set of fact data that we may want to display or
 * use to construct links in the list of facts.
 * 
 * So, rather than looking at the list directly, look at what I actually need:
 * <ul>
 * <li>the property name, e.g. "height"
 * <li>individualfact: object.name
 * <li>quantityfact: value
 * <li>measurementfact: value and unitsynonym
 * <li>stringfact: value
 * </ul>
 * 
 * So really, the thing that most closely matches the JSP needs would be four parallel lists:
 * <ul>
 * <li> name
 * <li> type
 * <li> value
 * <li> unit (opt)
 * </ul>
 * 
 * But it would also be OK to just split up the subtypes into four type-specific lists, and then the
 * JSP could iterate over some variable, or map, or whatever, and get the display instance from the
 * type-specific list. that seems way better than this stupid string binding or whatever.
 * 
 * But that seems pretty dumb too to be frank.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/fact-list")
public class FactListActionBean extends ModelEntityListActionBean {

	@Override
	protected String getJSP() {
		return "/fact-list.jsp"; //$NON-NLS-1$
	}

	@Override
	public Class<? extends ModelEntity> getPersistentClass() {
		return Fact.class;
	}
}
