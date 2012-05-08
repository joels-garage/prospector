/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import org.hibernate.Session;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.util.HibernateProperty;

/**
 * Fetch interface to the DB, using String key lookups on the name field.
 * 
 * @author joel
 * 
 */
public class ByNameHibernateRecordFetcher extends HibernateRecordFetcherBase<String> {
	public ByNameHibernateRecordFetcher(Session session, ReaderConstraint constraint) {
		super(session, constraint);
	}

	@Override
	public String getMainFieldName() {
		return HibernateProperty.NAME;
	}
}
