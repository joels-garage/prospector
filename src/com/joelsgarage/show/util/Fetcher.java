/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.util;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.action.ModelEntityActionBean;
import com.joelsgarage.util.HbnSessionUtil;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;

/**
 * Gets a single record from the DB.
 * 
 * @author joel
 */
public abstract class Fetcher<T extends ModelEntity> {
	private Class<T> persistentClass;
	/** Should we do any work? True if the type parameter is valid */
	private boolean valid = true;
	/** The instance itself. */
	private T instance;
	/** Also dereference the keys? */
	private boolean dereference = true;

	@SuppressWarnings("unchecked")
	public Fetcher(boolean dereference) {
		setDereference(dereference);
		setPersistentClass((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0]);

		if (!HibernateUtil.isValidEntityType(
			HbnSessionUtil.getCurrentSession().getSessionFactory(), getPersistentClass())) {
			Logger.getLogger(ModelEntityActionBean.class).error("No Hibernate Config for type " //$NON-NLS-1$
				+ getPersistentClass().getName());
			setValid(false);
		}
	}

	/** Fetch the instance of type T whose key is as specified */
	public void fetch(ExternalKey key) {
		fetch(Property.forName(HibernateProperty.KEY).eq(key));
	}

	/**
	 * Fetch the instance of type T whose specified property has the specified key value. If
	 * multiple match, we just take the first one. See ListFetcher if you want more.
	 */
	public void fetch(String property, ExternalKey foreignKey) {
		fetch(Property.forName(property).eq((foreignKey)));
	}

	/**
	 * Fetch the instance of type T given the criterion list specified.
	 * 
	 * @param criterion
	 */
	public void fetch(Criterion... criterion) {
		setInstance(null);
		if (!isValid())
			return;
		Session session = HbnSessionUtil.getCurrentSession();
		Criteria crit = session.createCriteria(getPersistentClass());

		for (Criterion c : criterion) {
			crit.add(c);
		}

		// We only get one record. If you want more records, use ListFetcher.
		crit.setFirstResult(0);
		crit.setMaxResults(1);
		crit.setFetchSize(1);

		List<?> result = crit.list();
		if (result.size() > 0) {
			Object o = result.get(0);
			if (getPersistentClass().isInstance(o)) {
				setInstance(getPersistentClass().cast(o));
			}
		}
	}

	public Class<T> getPersistentClass() {
		return this.persistentClass;
	}

	public void setPersistentClass(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public T getInstance() {
		return this.instance;
	}

	public void setInstance(T instance) {
		this.instance = instance;
	}

	public boolean isDereference() {
		return this.dereference;
	}

	public void setDereference(boolean dereference) {
		this.dereference = dereference;
	}
}
