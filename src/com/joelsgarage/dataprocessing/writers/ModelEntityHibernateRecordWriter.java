/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import org.hibernate.Session;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.model.ModelEntity;

/**
 * Thanks Java Generics for this awesome vacuous class.
 * 
 * @author joel
 * 
 */
public class ModelEntityHibernateRecordWriter extends HibernateRecordWriter<ModelEntity> {
    public ModelEntityHibernateRecordWriter(Session s) throws InitializationException {
        super(s);
    }

    public ModelEntityHibernateRecordWriter() throws InitializationException {
        super();
    }
}
