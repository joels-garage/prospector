/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.RecordFetcherFactory;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.HibernateUtil;

/**
 * TODO: eliminate the "by name" distinction.
 * 
 * TODO: I think this produces a lot of sessions; clean them up.
 * 
 * @author joel
 */
public class ByNameHibernateRecordFetcherFactory implements
	RecordFetcherFactory<String, ModelEntity> {
	private String database;

	/**
	 * Construct a new record fetcher factory that produces record fetchers that do string lookups
	 * on the name field.
	 * 
	 * @param database
	 *            overrides the configured database name
	 */
	public ByNameHibernateRecordFetcherFactory(String database) {
		setDatabase(database);
	}

	/**
	 * Create a new reader using a new session. For now we leave the sessions hanging, which is bad.
	 * We could do something to clean them up, I guess.
	 */
	@Override
	public RecordFetcher<String, ModelEntity> newInstance(ReaderConstraint constraint) {
		Class<? extends ModelEntity> constraintClass = constraint.getClassConstraint();
		if (!ModelEntity.class.isAssignableFrom(constraintClass))
			return null;
		// This is cached, don't worry about saving it.
		SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
			getDatabase()); // configured database
		if (factory == null)
			return null;

		Session session = factory.openSession();
		ByNameHibernateRecordFetcher fetcher = new ByNameHibernateRecordFetcher(session, constraint);
		return fetcher;
	}

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
