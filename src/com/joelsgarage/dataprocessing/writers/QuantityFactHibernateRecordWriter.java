/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import org.hibernate.Session;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.model.QuantityFact;

/**
 * Thanks Java Generics for this awesome vacuous class.
 * 
 * @author joel
 * 
 */
public class QuantityFactHibernateRecordWriter extends HibernateRecordWriter<QuantityFact> {
	public QuantityFactHibernateRecordWriter(Session s) throws InitializationException {
		super(s);
	}
}
