/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.action;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.Validate;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.model.DisplayModelEntity;
import com.joelsgarage.util.HbnSessionUtil;
import com.joelsgarage.util.HibernateProperty;

/**
 * Action bean base class to display a list of any type record.
 * 
 * I could break the contained list into its own type (as seems to be a common pattern) but I don't
 * see why; this is already enough of a container for the list.
 * 
 * @author joel
 * 
 */
@UrlBinding(value = "/model-entity-list")
public abstract class ModelEntityListActionBean extends JSPActionBean {
	private static final int DEFAULT_PAGE_SIZE = 10;
	private static final int DEFAULT_PAGE = 0;

	/**
	 * A page of results.
	 * 
	 * OK, the problem with this is that the JSTL forEach operator thinks that every instance is the
	 * specified parameter class, DisplayModelEntity, even when it's a subclass.
	 * 
	 * I guess it would be dangerous to do otherwise.
	 * 
	 * So, to allow polymorphic collections, I need the ActionBean to anticipate what the JSP is
	 * going to want, so that the JSP is actually accessing something other than this polymorphic
	 * collection.
	 * 
	 * Maybe I could figure out a way around this, but I'm not seeing it. Nobody ever mentions
	 * polymorphic collections; it's just a template language after all.
	 * 
	 * I think this comment is incorrect.
	 */
	private List<DisplayModelEntity> list;

	/** How many records per page */
	@Validate(minvalue = 1)
	private int pageSize = -1;
	/** Zero-based page index */
	@Validate(minvalue = 0)
	private int page = -1;
	/** True if there are more pages */
	private boolean more;
	/** Total results */
	private int resultCount;

	public ModelEntityListActionBean() {
		super();
		this.list = new ArrayList<DisplayModelEntity>();
	}

	@Override
	protected String getJSP() {
		return "/model-entity-list.jsp"; //$NON-NLS-1$
	}

	/**
	 * Sets the default page and pagesize.
	 */
	@Before(stages = LifecycleStage.BindingAndValidation)
	public void setDefaultPage() {
		if (getPageSize() < 0)
			setPageSize(DEFAULT_PAGE_SIZE);
		if (getPage() < 0)
			setPage(DEFAULT_PAGE);
	}

	/**
	 * The default handler retrieves the entire list of the parameter type, and forwards to the JSP
	 * specified by the subclass.
	 * 
	 * TODO: filtering the list
	 * 
	 * @return
	 */
	@DefaultHandler
	public Resolution fetch() {
		populateList();
		return new ForwardResolution(getJSP());
	}

	/** Select the type displayed */
	public Class<? extends ModelEntity> getPersistentClass() {
		return ModelEntity.class;
	}

	/**
	 * Use Hibernate to populate the list member. For each item, create a Display version and
	 * dereference its keys.
	 * 
	 * OK, the problem here for polymorphic queries is that the SQL is like this:
	 * 
	 * <pre>
	 * select * from (
	 *   select * from subclass1
	 *   union
	 *   select * from subclass2
	 *   ) limit N
	 * </pre>
	 * 
	 * I think the reason it does that is because it needs to provide an ordering for the result
	 * set, through the union. If I used joined-subclass, it would have an ordering on the base
	 * class that it joins to, but, jeez, I don't want to go back to that, I like the independent
	 * tables.
	 * 
	 * So the question is, what ordering do I really expect anyway?
	 * 
	 * The trouble is that I want alphabetical. So it's a merge sort at best.
	 * 
	 * Maybe the sort isn't that bad, if the maximum item is like 1000.
	 * 
	 * OK, the big story is that calling populateList() with getPersistentClass() as some superclass
	 * is suicidal. And there's no way to tell.
	 * 
	 * So, do two things.
	 * 
	 * First, make getPersistentClass() return an array.
	 * 
	 * Second, check the values to make sure they are final, i.e. could not possibly result in a
	 * polymorphic union query of death.
	 */
	protected void populateList() {
		Class<? extends ModelEntity> persistentClass = getPersistentClass();
		if (!Modifier.isFinal(persistentClass.getModifiers())) {
			Logger.getLogger(ModelEntityListActionBean.class).error(
				"Suicidal polymorphic query for class: " + persistentClass.getName()); //$NON-NLS-1$
			return;
		}

		// TODO: remove HbnSession here?
		Session session = HbnSessionUtil.getCurrentSession();
		Criteria crit = session.createCriteria(getPersistentClass());
		// every ModelEntity has a name, and it would make sense to a user to do it, so order by
		// that.
		crit.addOrder(Order.asc(HibernateProperty.NAME));
		crit.setFirstResult(getPageSize() * getPage());

		crit.setMaxResults(getPageSize() + 1);
		crit.setFetchSize(getPageSize() + 1);

		List<?> result = crit.list();

		setList(new ArrayList<DisplayModelEntity>());

		for (Object item : result) {
			if (getPersistentClass().isInstance(item)) {
				DisplayModelEntity displayEntity = getPersistentClass().cast(item)
					.newDisplayEntity();
				displayEntity.dereference();
				getList().add(displayEntity);
			}
		}

		if (getList().size() > getPageSize()) {
			// extra one says "more available" so set the bit and remove the instance
			setMore(true);
			getList().remove(getList().size() - 1);
		} else {
			// no more to be seen.
			setMore(false);
		}

		// Make new critiera without the firstresult constraint, in order to get the full count.
		crit = session.createCriteria(getPersistentClass());

		Integer count = (Integer) crit.setProjection(Projections.rowCount()).uniqueResult();
		if (count == null) {
			// this is some kind of weird error.
			setResultCount(0);
		} else {
			setResultCount(count.intValue());
		}
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

	public boolean isMore() {
		return this.more;
	}

	public void setMore(boolean more) {
		this.more = more;
	}

	public int getResultCount() {
		return this.resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}

	public List<DisplayModelEntity> getList() {
		return this.list;
	}

	public void setList(List<DisplayModelEntity> list) {
		this.list = list;
	}

	/** Total pages. I would think JSP could do this, but it doesn't seem to be rounding correctly */
	public int getTotalPages() {
		int pages = 1 + ((getResultCount() - 1) / getPageSize());
		return pages;
	}

}
