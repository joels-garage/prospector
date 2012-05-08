/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayIndividualProperty;

/**
 * @author joel
 */
@UrlBinding(value = "/individual-property")
public class IndividualPropertyActionBean extends ModelEntityActionBean {

	@Override
	protected String getJSP() {
		return "/individual-property.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayIndividualProperty(true));
	}
	// private static final String PROPERTY_JSP = "/property.jsp"; //$NON-NLS-1$
	// private static final String KEY_PROPERTY = "key"; //$NON-NLS-1$
	// // private static final String PROPERTY_KEY_PROPERTY = "propertyKey"; //$NON-NLS-1$
	// private ActionBeanContext context;
	// /** give me the key */
	// private ExternalKey key;
	// /** i throw you the rope */
	// private StringProperty property;
	//
	// /** the extant distinct values for this property? */
	// private List<String> values = new ArrayList<String>();
	//
	// @SuppressWarnings("nls")
	// public Resolution fetch() {
	// Session session = HbnSessionUtil.getCurrentSession();
	// Criteria crit = session.createCriteria(StringProperty.class);
	// crit.add(Property.forName(KEY_PROPERTY).eq(this.key));
	// crit.setMaxResults(10); // really should be 1
	//
	// List<?> result = crit.list();
	// if (result.size() > 0) {
	// if (result.get(0) instanceof StringProperty) {
	// this.property = (StringProperty) result.get(0);
	// }
	// }
	//
	// // get facts for this property
	// // select distinct value from StringFact where propertyKey = this.key
	//
	// // Hokum.
	// Query query = session.createQuery(//
	// "select distinct object.name from IndividualFact fact, " //
	// + " Individual object " //
	// + " where fact.propertyKey = :key" //
	// + " and object.key = fact.objectKey");
	//
	// query.setParameter("key", this.key);
	// query.setMaxResults(100); // TODO: make this bigger
	//
	// List<?> results = query.list();
	//
	// // Criteria factCrit = session.createCriteria(StringFact.class);
	// // factCrit.add(Property.forName(PROPERTY_KEY_PROPERTY).eq(this.key));
	// // factCrit.setMaxResults(100);
	//
	// // List<?> factResult = factCrit.list();
	// for (Object o : results) {
	// if (o instanceof String) {
	// String stringResult = (String) o;
	// this.values.add(stringResult);
	// }
	// }
	//
	// return new ForwardResolution(PROPERTY_JSP);
	// }
	//
	// public ActionBeanContext getContext() {
	// return this.context;
	// }
	//
	// public void setContext(ActionBeanContext context) {
	// this.context = context;
	// }
	//
	// public ExternalKey getKey() {
	// return this.key;
	// }
	//
	// public void setKey(ExternalKey key) {
	// this.key = key;
	// }
	//
	// public StringProperty getProperty() {
	// return this.property;
	// }
	//
	// public void setProperty(StringProperty property) {
	// this.property = property;
	// }
	//
	// public List<String> getValues() {
	// return this.values;
	// }
	//
	// public void setValues(List<String> values) {
	// this.values = values;
	// }

}
