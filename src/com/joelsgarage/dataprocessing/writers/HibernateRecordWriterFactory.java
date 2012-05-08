/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.RecordWriterFactory;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.HibernateUtil;

/**
 * This factory creates a new session for each writer, using the HibernateUtil session factory
 * cache.
 * 
 * @author joel
 * 
 */
public class HibernateRecordWriterFactory implements RecordWriterFactory<ModelEntity> {
	private String database;

	public HibernateRecordWriterFactory(String database) {
		setDatabase(database);
	}

	/**
	 * Create a new reader using a new session. For now we leave the sessions hanging, which is bad.
	 * We could do something to clean them up, I guess.
	 */
	@Override
	public RecordWriter<ModelEntity> newInstance(ReaderConstraint constraint) {
		// If the constraint is crap, bail.
		Class<? extends ModelEntity> constraintClass = constraint.getClassConstraint();
		if (!ModelEntity.class.isAssignableFrom(constraintClass))
			return null;

		// The SessionFactory is cached, don't worry about saving it.
		SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
			getDatabase()); // configured database
		if (factory == null)
			return null;

		Session session = factory.openSession();

		try {
			return new ModelEntityHibernateRecordWriter(session);
		} catch (InitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
