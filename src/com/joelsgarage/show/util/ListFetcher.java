/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.util;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Property;

import com.joelsgarage.prospector.client.model.ExternalKey;
import com.joelsgarage.prospector.client.model.ModelEntity;
import com.joelsgarage.show.action.ModelEntityActionBean;
import com.joelsgarage.util.HbnSessionUtil;
import com.joelsgarage.util.HibernateProperty;
import com.joelsgarage.util.HibernateUtil;

/**
 * Gets a list from the DB.
 * 
 * @author joel
 */
public class ListFetcher<T extends ModelEntity> {
	private Class<T> persistentClass;
	/** Should we do any work? True if the type parameter is valid */
	private boolean valid = true;
	/** The instance itself. */
	private T instance;

	@SuppressWarnings("unchecked")
	public ListFetcher() {
		setPersistentClass((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0]);

		if (!HibernateUtil.isValidEntityType(
			HbnSessionUtil.getCurrentSession().getSessionFactory(), getPersistentClass())) {
			Logger.getLogger(ModelEntityActionBean.class).error("No Hibernate Config for type " //$NON-NLS-1$
				+ getPersistentClass().getName());
			setValid(false);
		}
	}

	/** Fetch the instance whose key is as specified */
	public void fetch(ExternalKey key) {
		setInstance(null);
		if (!isValid())
			return;
		Session session = HbnSessionUtil.getCurrentSession();
		Criteria crit = session.createCriteria(getPersistentClass());
		crit.add(Property.forName(HibernateProperty.KEY).eq(key));
		crit.setFirstResult(0);
		// the key should be unique.
		// TODO: select based on timestamp?
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

	// /**
	// * Fetch the instance whose specified property is the specified key. Obviously this is only
	// * useful for many-to-one relations.
	// *
	// * TODO: handle lists too?
	// */
	// public void fetch(String property, ExternalKey foreignKey) {
	//
	//	}

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

}
