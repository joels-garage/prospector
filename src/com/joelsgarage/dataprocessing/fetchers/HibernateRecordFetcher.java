/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import org.hibernate.Session;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.util.HibernateProperty;

/**
 * Fetch interface to the DB.
 * 
 * @author joel
 * 
 */
public class HibernateRecordFetcher extends HibernateRecordFetcherBase<ExternalKey> {
	public HibernateRecordFetcher(Session session, ReaderConstraint constraint) {
		super(session, constraint);
	}

	@Override
	public String getMainFieldName() {
		return HibernateProperty.KEY;
	}
}
