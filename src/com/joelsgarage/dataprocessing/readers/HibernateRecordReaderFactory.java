/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.HibernateUtil;

/**
 * This factory creates a new session for each reader, using the HibernateUtil session factory
 * cache.
 * 
 * @author joel
 * 
 */
public class HibernateRecordReaderFactory implements RecordReaderFactory<ModelEntity> {
    private String database;

    public HibernateRecordReaderFactory() {
        this(null);
    }

    public HibernateRecordReaderFactory(String database) {
        setDatabase(database);
    }

    /**
     * Create a new reader using a new session. Caller must remember to close the session.
     */
    @Override
    public RecordReader<ModelEntity> newInstance(ReaderConstraint constraint) {
        // Reject crap constraints.
        Class<? extends ModelEntity> constraintClass = constraint.getClassConstraint();
        if (!ModelEntity.class.isAssignableFrom(constraintClass))
            return null;

        // SessionFactory is cached, don't worry about saving it.
        SessionFactory factory = HibernateUtil.createSessionFactory(null, // default config
            getDatabase()); // configured database
        if (factory == null)
            return null;

        Session session = factory.openSession();
        ModelEntityHibernateRecordReader reader =
            new ModelEntityHibernateRecordReader(session, new ReaderConstraint(constraintClass,
                null));

        // TODO: remove this
        reader.setPersistentClass(constraintClass);
        return reader;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
