/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import net.sourceforge.stripes.action.UrlBinding;

import com.joelsgarage.show.model.DisplayIndividual;

/**
 * Action bean that displays an individual, and a paginated list of the facts whose subject are this
 * individual, looking up the property names for the facts it displays. I guess this tiny display
 * join isn't so bad.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/individual")
public class IndividualActionBean extends ModelEntityActionBean {

	@Override
	protected String getJSP() {
		return "/individual.jsp"; //$NON-NLS-1$
	}

	@Override
	public void initialize() {
		setInstance(new DisplayIndividual(true));
	}

	// private static final String CANONICAL_NAME = "canonicalName"; //$NON-NLS-1$
	// private static final String INDIVIDUAL_JSP = "/individual.jsp"; //$NON-NLS-1$
	// private static final String KEY_PROPERTY = "key"; //$NON-NLS-1$
	// private static final String SUBJECT_KEY_PROPERTY = "subjectKey"; //$NON-NLS-1$
	// private static final int DEFAULT_PAGE_SIZE = 10;
	// private static final int DEFAULT_PAGE = 0;
	//
	// private ActionBeanContext context;
	// /** give me the key */
	// private ExternalKey key;
	// /** i throw you the rope */
	// private Individual individual;
	// /** what i like about you */
	// // private List<StringFact> facts = new ArrayList<StringFact>();
	// private List<Fact> facts = new ArrayList<Fact>();
	//
	// /** ugh this is a parallel list */
	// // private List<StringProperty> properties = new ArrayList<StringProperty>();
	// private List<com.joelsgarage.prospector.client.model.Property> properties = new
	// ArrayList<com.joelsgarage.prospector.client.model.Property>();
	//
	// /** For Measurement Facts, if any appear in the list, the canonical synonym is stored here */
	// private List<UnitSynonym> units = new ArrayList<UnitSynonym>();
	// // Factor these out into some sort of "list display bean", or look at how DisplayTag handles
	// // them.
	// /** How many fact records per page */
	// @Validate(minvalue = 1)
	// private int pageSize = -1;
	// /** Zero-based fact page index */
	// @Validate(minvalue = 0)
	// private int page = -1;
	// /** True if there are more pages */
	// private boolean more;
	// /** Total results */
	// private int resultCount;
	//
	// @Before(stages = LifecycleStage.BindingAndValidation)
	// public void prepopulate() {
	// if (getPageSize() < 0)
	// setPageSize(DEFAULT_PAGE_SIZE);
	// if (getPage() < 0)
	// setPage(DEFAULT_PAGE);
	// }
	//
	// @DefaultHandler
	// public Resolution fetch() {
	// Session session = HbnSessionUtil.getCurrentSession();
	//
	// Criteria crit = session.createCriteria(Individual.class);
	// crit.add(Property.forName(KEY_PROPERTY).eq(this.key));
	// crit.setMaxResults(1); // really should be 1
	//
	// List<?> result = crit.list();
	// if (result.size() > 0) {
	// if (result.get(0) instanceof Individual) {
	// this.individual = (Individual) result.get(0);
	// }
	// }
	//
	// // it's ALL fact types together in one list.
	// // Criteria factCrit = session.createCriteria(StringFact.class);
	// Criteria factCrit = session.createCriteria(Fact.class);
	//
	// factCrit.add(Property.forName(SUBJECT_KEY_PROPERTY).eq(this.key));
	// factCrit.setFirstResult(getPageSize() * getPage());
	// // Plus one to see if there are more
	// factCrit.setMaxResults(getPageSize() + 1);
	// factCrit.setFetchSize(getPageSize() + 1);
	//
	// List<?> factResult = factCrit.list();
	//
	// setFacts(new ArrayList<Fact>());
	//
	// for (Object o : factResult) {
	// // if (o instanceof StringFact) {
	// // StringFact stringFact = (StringFact) o;
	// // this.facts.add(stringFact);
	// // // TODO: let hibernate do this join somehow?
	// // Criteria propertyCrit = session.createCriteria(StringProperty.class);
	// // propertyCrit.add(Property.forName(KEY_PROPERTY).eq((stringFact.getPropertyKey())));
	// // propertyCrit.setMaxResults(1);
	// // List<?> propertyResult = propertyCrit.list();
	// // if ((propertyResult.size() > 0)
	// // && (propertyResult.get(0) instanceof StringProperty)) {
	// // this.properties.add((StringProperty) propertyResult.get(0));
	// // } else {
	// // StringProperty foo = new StringProperty();
	// // foo.setName("foo"); //$NON-NLS-1$
	// // this.properties.add(foo);
	// // }
	// // }
	//
	// // For this display, all we need is the fact, of any type
	// if (o instanceof Fact) {
	// // StringFact stringFact = (StringFact) o;
	// Fact fact = (Fact) o;
	//
	// // this.facts.add(stringFact);
	// getFacts().add(fact);
	// System.out.println("FOO fact name: " + fact.getName()); //$NON-NLS-1$
	//
	// // TODO: let hibernate do this join somehow?
	// // Criteria propertyCrit = session.createCriteria(StringProperty.class);
	// Criteria propertyCrit = session
	// .createCriteria(com.joelsgarage.prospector.client.model.Property.class);
	//
	// // propertyCrit.add(Property.forName(KEY_PROPERTY).eq((stringFact.getPropertyKey())));
	// propertyCrit.add(Property.forName(KEY_PROPERTY).eq((fact.getPropertyKey())));
	// propertyCrit.setMaxResults(1);
	// List<?> propertyResult = propertyCrit.list();
	// if ((propertyResult.size() > 0)
	// // && (propertyResult.get(0) instanceof StringProperty)) {
	// && (propertyResult.get(0) instanceof com.joelsgarage.prospector.client.model.Property)) {
	//
	// // this.properties.add((StringProperty) propertyResult.get(0));
	// this.properties
	// .add((com.joelsgarage.prospector.client.model.Property) propertyResult
	// .get(0));
	// System.out
	// .println("FOO property name: " //$NON-NLS-1$
	// + ((com.joelsgarage.prospector.client.model.Property) propertyResult
	// .get(0)).getName());
	//
	// } else {
	// System.out.println("FOO nothing"); //$NON-NLS-1$
	// getProperties().add(null);
	// }
	// }
	// if (o instanceof MeasurementFact) {
	// // fill in the unit
	// MeasurementFact measurementFact = (MeasurementFact) o;
	// System.out.println("FOO measurement fact: " + measurementFact.getName()); //$NON-NLS-1$
	//
	// Criteria unitCrit = session.createCriteria(UnitSynonym.class);
	// unitCrit.add(Property.forName(KEY_PROPERTY).eq((measurementFact.getPropertyKey())));
	// unitCrit.add(Property.forName(CANONICAL_NAME).eq(Boolean.TRUE));
	// unitCrit.setMaxResults(1);
	// List<?> unitResult = unitCrit.list();
	// if ((unitResult.size() > 0) && (unitResult.get(0) instanceof UnitSynonym)) {
	// getUnits().add((UnitSynonym) unitResult.get(0));
	// System.out.println("FOO unit: " + ((UnitSynonym) unitResult.get(0)).getName()); //$NON-NLS-1$
	// } else {
	// System.out.println("FOO nothing2"); //$NON-NLS-1$
	// getUnits().add(null);
	// }
	//
	// }
	// }
	//
	// if (getFacts().size() > getPageSize()) {
	// // extra one says "more available" so set the bit and remove the instance
	// setMore(true);
	// getFacts().remove(getFacts().size() - 1);
	// } else {
	// // no more to be seen.
	// setMore(false);
	// }
	//
	// // Make new critiera without the firstresult constraint, in order to get the full count.
	// crit = session.createCriteria(StringFact.class);
	// factCrit.add(Property.forName(SUBJECT_KEY_PROPERTY).eq(this.key));
	//
	// Integer count = (Integer) crit.setProjection(Projections.rowCount()).uniqueResult();
	// if (count == null) {
	// // this is some kind of weird error.
	// setResultCount(0);
	// } else {
	// setResultCount(count.intValue());
	// }
	//
	// return new ForwardResolution(INDIVIDUAL_JSP);
	// }
	//
	// public Individual getIndividual() {
	// return this.individual;
	// }
	//
	// public void setIndividual(Individual individual) {
	// this.individual = individual;
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
	// public List<Fact> getFacts() {
	// return this.facts;
	// }
	//
	// public void setFacts(List<Fact> facts) {
	// this.facts = facts;
	// }
	//
	// public List<com.joelsgarage.prospector.client.model.Property> getProperties() {
	// return this.properties;
	// }
	//
	// public void setProperties(List<com.joelsgarage.prospector.client.model.Property> properties)
	// {
	// this.properties = properties;
	// }
	//
	// public int getPageSize() {
	// return this.pageSize;
	// }
	//
	// public void setPageSize(int pageSize) {
	// this.pageSize = pageSize;
	// }
	//
	// public int getPage() {
	// return this.page;
	// }
	//
	// public void setPage(int page) {
	// this.page = page;
	// }
	//
	// public boolean isMore() {
	// return this.more;
	// }
	//
	// public void setMore(boolean more) {
	// this.more = more;
	// }
	//
	// public int getResultCount() {
	// return this.resultCount;
	// }
	//
	// public void setResultCount(int resultCount) {
	// this.resultCount = resultCount;
	// }
	//
	// public List<UnitSynonym> getUnits() {
	// return this.units;
	// }
	//
	// public void setUnits(List<UnitSynonym> units) {
	// this.units = units;
	// }
}
