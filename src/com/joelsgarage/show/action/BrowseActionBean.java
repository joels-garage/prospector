/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import java.util.Enumeration;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.StripesConstants;
import net.sourceforge.stripes.validation.Validate;

import org.apache.log4j.Logger;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.ShowActionBean;
import com.joelsgarage.show.model.DisplayModelEntity;
import com.joelsgarage.show.util.MultiList;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.ForeignKey;
import com.joelsgarage.util.NameUtil;

/**
 * An attempt to do all the browsing from a single action. A zillion little vacuous JSPs, and a
 * zillion little actionbean subclasses, are dumb; just make a map here.
 * 
 * The idea is to have URLs like:
 * 
 * <pre>
 * http://foo.com/browse?type=Individual == list of all Individuals in all namespaces
 * 
 * http://foo.com/browse?type=Individual&amp;namespace=foo&amp;key=bar == single Individual, with detail
 * 
 * http://foo.com/browse?type=Fact&amp;field=subject&amp;ftype=Individual&amp;fnamespace=foo&amp;fkey=bar
 *   == lists of Fact subtypes, each constrained by the foreign key (field, ftype etc).
 * </pre>
 * 
 * it would be associated with a single jsp, browse.jsp.
 * 
 * it would need a map of allowed types, in order to do polymorphic queries.
 * 
 * and it would need a map of allowed fields.
 * 
 * Note, the "type" is not "key.type". it's just "type", so the bean has to have such a field.
 * 
 * So *all* browse events must specify *some* type, since we don't allow root-level polymorphic
 * browsing.
 * 
 * So, since it's required, it may as well be first.
 * 
 * Since we're using a clean url without an event, there's just one handler, the default handler.
 * 
 * In general the externalkey serialization is {type}/{namespace}/{key}
 * 
 * so, use that. the thing is, though, if it's slots, then you get this:
 * 
 * fact///subject/foo/bar
 * 
 * which sucks.
 * 
 * so, could do it backwards:
 * 
 * individual/foo/bar/facts-with-that-individual-as-subject
 * 
 * that breaks the "first thing is the type" rule but maybe is more understandable, like you're
 * following a set-valued relation (as indeed you are, if you started with that individual).
 * 
 * so what's the term for 'facts about that individual'? facet?
 * 
 * individual/foo/bar/facets == facts (of all types) with that individual as subject e.g. "height=5"
 * is a facet of foo
 * 
 * how about facts with that individual as objects?
 * 
 * individual/foo/bar/referents e.g. "color=blue" is a referent of "blue"
 * 
 * how about facts of a specific property
 * 
 * property/foo/bar/baz/facts
 * 
 * individuals of a specific class
 * 
 * class/foo/bar/members
 * 
 * so maybe i can think of a word for each of these joins, each of which corresponds to a Hibernate
 * constraint, made up of a class and a field name (the foreign key field)
 * 
 * so actually the structure is
 * 
 * foreign-class, class, foreign-key-field
 * 
 * individual, class-member, individual (actually "individualKey" is the field)
 * 
 * individual, fact, subject (actually subjectKey)
 * 
 * note this data also constitutes the type-checking thing i've been wanting to do.
 * 
 * first field is part of the ExternalKey list (which I should put into an enum, i guess)
 * 
 * second field is too.
 * 
 * third field is in HibernateProperty.
 * 
 * so i could just use these words directly
 * 
 * individual/foo/bar/fact/subject
 * 
 * and i would just magically know what to do; it's not hierarchical anyway.
 * 
 * note there may be other constraints, like the namespace of the foreign class.
 * 
 * so, the dowser doesn't need to provide a full tree of allowed states, it just needs to validate
 * them.
 * 
 * actually it would be nice to know, when showing the "individual" page, that there are some joins
 * you can do.
 * 
 * so actually i do want the set of allowed joins for a given foreign class.
 * 
 * So how many modes exist for this bean?
 * 
 * 1. single record, i.e. specified type, ns, key. shows stub lists for each allowed join.
 * 
 * 2. multiple record, i.e. specified type and maybe ns
 * 
 * 3. paginated list, i.e. specified type,ns,key,join,field, and optionally join ns.
 * 
 * anything else is an error.
 * 
 * any list is actually a set of lists, one for each final type.
 * 
 * @author joel
 * 
 */
// These fields are URLENCODED and must be DECODED BEFORE USE
@UrlBinding(value = "/browse/{key.type}/{key.namespace}/{key.key}/{joinType}/{joinField}/{joinNamespace}")
public class BrowseActionBean extends ShowActionBean {
	public enum ViewType {
		/** Display a single entity, with stub join lists for all visible joins */
		ENTITY,
		/** Display a single list */
		LIST,
		/** Display a single entity, with one paginated list for the specified join */
		JOIN
	}

	/** The key of the instance, or foreign key of the list. */
	private ExternalKey key;
	/** For lists derived from joins, the class of the list items. might be abstract. */
	private String joinType;
	/** The foreign key field */
	private String joinField;
	/** An optional namespace constraint on the join class */
	private String joinNamespace;
	/**
	 * Optional search string, to find instances with a name "LIKE" this. Only applies for LIST
	 * type.
	 */
	private String nameQuery;

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

	private ViewType viewType;

	public BrowseActionBean() {
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

		String sourcePage = getContext().getRequest().getParameter(
			StripesConstants.URL_KEY_SOURCE_PAGE);
		Logger.getLogger(BrowseActionBean.class).info("SOURCEPAGE: " + sourcePage);
		Logger.getLogger(BrowseActionBean.class).info(
			"KEY: " + StripesConstants.URL_KEY_SOURCE_PAGE);
		Enumeration<?> enu = getContext().getRequest().getAttributeNames();
		while (enu.hasMoreElements()) {
			String s = (String) enu.nextElement();
			Logger.getLogger(BrowseActionBean.class).info("Request attribute name: " + s);
		}

		// String specifiedType = getKey().getType();
		String specifiedType = getDecodedType();

		Logger.getLogger(BrowseActionBean.class).info("got type " + specifiedType);
		Class<? extends ModelEntity> specifiedClass = getDowser().getAllowedTypes().get(
			specifiedType);

		// If type is not specified, or if it's not an allowed type, you get the base type.
		if (specifiedClass == null) {
			specifiedClass = ModelEntity.class;
			// setTypeAllowed(false); // tells the JSP to show the type list.
			// Logger.getLogger(BrowseActionBean.class).info("invalid type " + specifiedType);
			// return new ForwardResolution(getErrorJSP());
			// return new ForwardResolution(getJSP());
		}

		setTypeAllowed(true);
		Logger.getLogger(BrowseActionBean.class).info("valid type " + specifiedType);

		// If key is not specified, ignore the other arguments and produce a list.
		// String specifiedKey = getKey().getKey();
		String specifiedKey = getDecodedKey();

		if (specifiedKey == null || specifiedKey.length() == 0) {
			setViewType(ViewType.LIST);
			return makeList(specifiedClass);
		}

		// OK, there's a key, so fetch that instance.

		// First we need to check for a valid join key, and if found, pass it along.
		ForeignKey foreignKey = getForeignKey(specifiedClass);
		if (foreignKey == null)
			setViewType(ViewType.ENTITY);
		else
			setViewType(ViewType.JOIN);

		Logger.getLogger(BrowseActionBean.class).info("fetch instance for key " + getDecodedKey());

		// TODO: this "dereference everything" approach is pretty crazy when
		// it gets to be three deep. So instead, lazily dereference using active
		// accessors.
		setInstance(new DisplayModelEntity(specifiedClass, 3));
		// getInstance().fetch(getKey());
		getInstance().fetch(
			new ExternalKey(getDecodedNamespace(), getDecodedType(), getDecodedKey()));

		getInstance().dereference(foreignKey, getPage(), getPageSize());

		return new ForwardResolution(getJSP());
	}

	/** Extract a valid foreign key from the Url, if possible. Null otherwise */
	protected ForeignKey getForeignKey(Class<? extends ModelEntity> specifiedClass) {
		String specifiedJoinType = getJoinType();
		String specifiedJoinField = getJoinField();

		// If no join field is specified, then it's not a join.
		if (specifiedJoinType == null || specifiedJoinField == null
			|| specifiedJoinType.length() == 0 || specifiedJoinField.length() == 0) {
			Logger.getLogger(BrowseActionBean.class).info("No join field specified."); //$NON-NLS-1$
			return null;
		}

		// Validate the specified join
		Set<ForeignKey> foreignKeys = getDowser().getAllowedJoins().get(specifiedClass);
		if (foreignKeys == null) {
			Logger.getLogger(BrowseActionBean.class).info("Invalid join field specified."); //$NON-NLS-1$
			return null;
		}
		for (ForeignKey foreignKey : foreignKeys) {
			String keyJoinType = getDowser().getTypeNames().get(foreignKey.getClas());
			String keyJoinField = foreignKey.getLabel();
			if (keyJoinType.equals(specifiedJoinType) && keyJoinField.equals(specifiedJoinField)) {
				return foreignKey;
			}
		}
		return null;
	}

	/** Populate a single list of the specified entity type */
	protected Resolution makeList(Class<? extends ModelEntity> specifiedClass) {
		Logger.getLogger(BrowseActionBean.class).info(
			"make list for type " + specifiedClass.getName()); //$NON-NLS-1$
		// pass the page params through; they may be ignored.
		getMultiList().setPage(getPage());
		getMultiList().setPageSize(getPageSize());
		// namespace may be null; pass it anyway.
		// key is always null; we want a list.
		Logger.getLogger(BrowseActionBean.class).info("depth 1"); //$NON-NLS-1$
		getMultiList().setNameQuery(getNameQuery());
		getMultiList().populateList(specifiedClass, getDecodedNamespace(), null, null, 1, null);

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

	public String getJoinType() {
		return this.joinType;
	}

	public void setJoinType(String joinType) {
		this.joinType = joinType;
	}

	public String getJoinField() {
		return this.joinField;
	}

	public void setJoinField(String joinField) {
		this.joinField = joinField;
	}

	public String getJoinNamespace() {
		return this.joinNamespace;
	}

	public void setJoinNamespace(String joinNamespace) {
		this.joinNamespace = joinNamespace;
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

	public ViewType getViewType() {
		return this.viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}

	public DisplayModelEntity getInstance() {
		return this.instance;
	}

	public void setInstance(DisplayModelEntity instance) {
		this.instance = instance;
	}

	public String getNameQuery() {
		return this.nameQuery;
	}

	public void setNameQuery(String nameQuery) {
		this.nameQuery = nameQuery;
	}

	//

	public String getDecodedKey() {
		return NameUtil.decode(getKey().getKey());
	}

	public String getDecodedType() {
		return NameUtil.decode(getKey().getType());
	}

	public String getDecodedNamespace() {
		return NameUtil.decode(getKey().getNamespace());
	}
}
