/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.ForeignKey;

/**
 * Container for multiple lists, as from a polymorphic query.
 * 
 * Might be a singleton, in which case we expose the pagination.
 * 
 * Database-backed paginated list.
 * 
 * Each row has an entity and a name (the name field of the entity.
 * 
 * The idea is that this would be accessed directly by the JSP fragment.
 * 
 * @author joel
 * 
 */
public class MultiList {
	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int DEFAULT_PAGE = 0;
	/** How many records per page */
	private int pageSize = -1;
	/** Zero-based page index */
	private int page = -1;

	private Map<Class<? extends ModelEntity>, PaginatedList> paginatedLists;

	/** Provides the subclass map for polymorphic queries */
	private Dowser dowser;

	/** The type of join, e.g. individual joins to string_fact */
	private String joinType;
	/** The join field, e.g. subject */
	private String joinField;
	/** For join lists, what's the primary key? */
	private ExternalKey primary;
	/** Optional restrict on name field */
	private String nameQuery;

	public MultiList() {
		setDowser(DowserFactory.newDowser());
	}

	/**
	 * Fetch a list of lists, one for each final subclass of the specified class.
	 * 
	 * @param specifiedClass
	 *            the type of entity to fetch. May be abstract.
	 * @param namespace
	 *            optional constraint on the returned namespace.
	 * @param key
	 *            optional key constraint.
	 * @param property
	 *            if not null, the namespace and key constraint are applied to this field rather
	 *            than the 'key' field
	 * @param foreignKey
	 *            for join queries, i.e. where this "list" is really a singleton container, this is
	 *            the join to dereference, resulting in a paginated list. don't do any other
	 *            dereferencing. TODO: separate the 'real' lists from the singletons
	 */
	@SuppressWarnings("nls")
	public void populateList(Class<? extends ModelEntity> specifiedClass, String namespace,
		ExternalKey key, String property, int dereference, ForeignKey foreignKey) {

		Logger.getLogger(MultiList.class).info(
			"populating list for class: " + specifiedClass.getName() + " with dereference "
				+ String.valueOf(dereference));

		setPaginatedLists(new HashMap<Class<? extends ModelEntity>, PaginatedList>());

		// Verify the type is allowed.
		// TODO: use the Dowser to find the subclasses of disallowed types.
		Class<? extends ModelEntity> persistentClass = specifiedClass;
		if (Dowser.isAllowed(persistentClass)) {
			Logger.getLogger(MultiList.class).info(
				"single final list for type " + persistentClass.getName());
			// Just a single list type
			// For a single list, we respect the page parameters.
			// If the user has not supplied pagination parameters, set the defaults:

			if (getPageSize() < 0)
				setPageSize(DEFAULT_PAGE_SIZE);
			if (getPage() < 0)
				setPage(DEFAULT_PAGE);
			addPaginatedList(specifiedClass);
		} else {
			Logger.getLogger(MultiList.class).info(
				"not final list for type " + persistentClass.getName());
			// Multiple lists.
			// For multiple lists we ignore the page parameters.
			// setPage(DEFAULT_PAGE);
			// setPageSize(DEFAULT_PAGE_SIZE);
			Set<Class<? extends ModelEntity>> subclasses = getDowser().getAllowedSubtypes().get(
				persistentClass);
			if (subclasses == null) {
				Logger.getLogger(MultiList.class).info("Not final, but no subtypes!  Give up!");
				return;
			}
			for (Class<? extends ModelEntity> clas : subclasses) {
				Logger.getLogger(MultiList.class).info("one of those for type " + clas.getName());
				addPaginatedList(clas);
			}

			Logger.getLogger(MultiList.class).error(
				"Splitting polymorphic query for class: " + persistentClass.getName()); //$NON-NLS-1$
		}

		// iterate over the final classes, i.e. the paginatedlists we added above.
		for (Map.Entry<Class<? extends ModelEntity>, PaginatedList> listEntry : getPaginatedLists()
			.entrySet()) {

			Class<? extends ModelEntity> finalClass = listEntry.getKey();
			PaginatedList paginatedList = listEntry.getValue();
			
			Logger.getLogger(MultiList.class).info("working on type " + finalClass.getName());

			if (paginatedList == null) {
				Logger.getLogger(MultiList.class).error(
					"Weird null list for class: " + finalClass.getName()); //$NON-NLS-1$
				continue;
			}

			Logger.getLogger(MultiList.class).info(
				"page: " + getPage() + " pagesize: " + getPageSize());
			
			paginatedList.setNameQuery(getNameQuery());
			
			paginatedList.populateList(finalClass, namespace, key, property, dereference,
				foreignKey);
		}
	}

	/**
	 * Add a PaginatedList instance to the paginatedLists map for the specified class, using our
	 * page and pagesize. The new instance will be empty.
	 */
	protected void addPaginatedList(Class<? extends ModelEntity> clas) {
		Logger.getLogger(MultiList.class).info("adding list for type " + clas.getName()); //$NON-NLS-1$
		PaginatedList paginatedList = new PaginatedList();
		paginatedList.setPageSize(getPageSize());
		paginatedList.setPage(getPage());
		getPaginatedLists().put(clas, paginatedList);
	}
	
	//
	
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

	public Dowser getDowser() {
		return this.dowser;
	}

	public void setDowser(Dowser dowser) {
		this.dowser = dowser;
	}

	public Map<Class<? extends ModelEntity>, PaginatedList> getPaginatedLists() {
		return this.paginatedLists;
	}

	public void setPaginatedLists(Map<Class<? extends ModelEntity>, PaginatedList> paginatedLists) {
		this.paginatedLists = paginatedLists;
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

	public ExternalKey getPrimary() {
		return this.primary;
	}

	public void setPrimary(ExternalKey primary) {
		this.primary = primary;
	}

	public String getNameQuery() {
		return this.nameQuery;
	}

	public void setNameQuery(String nameQuery) {
		this.nameQuery = nameQuery;
	}
}
