/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.dataprocessing.fetchers.HibernateRecordFetcher;
import com.joelsgarage.dataprocessing.readers.ModelEntityHibernateRecordReader;
import com.joelsgarage.dataprocessing.writers.ModelEntityHibernateRecordWriter;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.HibernateUtil;

/**
 * Static utilities for CallCenter.
 * 
 * @author joel
 * 
 */
public abstract class Util {
    /** For keys we create */
    public final static String CALLCENTER_NAMESPACE = "callcenter"; //$NON-NLS-1$

    /** Fetch by a nonprimary field. Value can be anything (String, ExternalKey) */
    public static <T extends ModelEntity> T getByField(Class<T> entityClass, String fieldName,
        Object value) {
        Map<String, Object> queryTerms = new HashMap<String, Object>();
        queryTerms.put(fieldName, value);
        return getCompound(entityClass, queryTerms);
    }

    protected static Class<? extends ModelEntity> classFromType(String type) {
        Dowser dowser = DowserFactory.newDowser();
        Class<? extends ModelEntity> entityClass = dowser.getAllowedTypes().get(type);
        if (entityClass == null) {
            Logger.getLogger(Util.class).error("disallowed type: " + type); //$NON-NLS-1$
            return null;
        }
        if (!(Modifier.isFinal(entityClass.getModifiers()))) {
            // TODO: loop through the final decendants.
            // for now, just bail
            Logger.getLogger(Util.class).error("suicidal polymorphism: " + entityClass.getName()); //$NON-NLS-1$
            return null;
        }
        return entityClass;
    }

    /** Fetch by a nonprimary field; looks up the type */
    public static ModelEntity getByFieldAndType(String type, String fieldName, Object value) {
        Class<? extends ModelEntity> entityClass = classFromType(type);
        if (entityClass == null) {
            Logger.getLogger(Util.class).error("disallowed type: " + type); //$NON-NLS-1$
            return null;
        }
        return getByField(entityClass, fieldName, value);
    }

    public static ModelEntity getCompoundByType(String type, Map<String, Object> queryTerms) {
        Class<? extends ModelEntity> entityClass = classFromType(type);
        if (entityClass == null) {
            Logger.getLogger(Util.class).error("disallowed type: " + type); //$NON-NLS-1$
            return null;
        }
        return getCompound(entityClass, queryTerms);
    }

    /**
     * Wraps RecordFetcher.getCompound(). Returns a single instance of the specified class, subject
     * to the specified query constraints, or null, if it doesn't exist, or if somehow the wrong
     * type is returned.
     */
    public static <T extends ModelEntity> T getCompound(Class<T> entityClass,
        Map<String, Object> queryTerms) {
        Logger.getLogger(Util.class).info("getCompound()"); //$NON-NLS-1$
        if (!(Modifier.isFinal(entityClass.getModifiers()))) {
            Logger.getLogger(Util.class).error("suicidal polymorphism: " + entityClass.getName()); //$NON-NLS-1$
            return null;
        }
        if (queryTerms == null)
            return null;

        Session session = HibernateUtil.createSessionFactory(null, null).openSession();
        ReaderConstraint readerConstraint = new ReaderConstraint(entityClass);
        RecordFetcher<ExternalKey, ModelEntity> recordFetcher =
            new HibernateRecordFetcher(session, readerConstraint);
        T modelEntity = entityClass.cast(recordFetcher.getCompound(queryTerms));
        session.close();
        if (modelEntity == null) {
            Logger.getLogger(Util.class).info("Nothing found"); //$NON-NLS-1$
            return null;
        }
        if (!(entityClass.isInstance(modelEntity))) {
            Logger.getLogger(Util.class).info("Weird type: " + modelEntity.getClass().getName()); //$NON-NLS-1$
            return null;
        }
        return modelEntity;
    }

    /** This relies on the type specified in the key to restrict the query */
    public static ModelEntity getPrimary(ExternalKey key) {
        Dowser dowser = DowserFactory.newDowser();
        String type = key.getType();
        Class<? extends ModelEntity> entityClass = dowser.getAllowedTypes().get(type);
        if (entityClass == null) {
            Logger.getLogger(Util.class).error("disallowed type: " + type); //$NON-NLS-1$
            return null;
        }
        if (!(Modifier.isFinal(entityClass.getModifiers()))) {
            // TODO: loop through the final decendants.
            // for now, just bail
            Logger.getLogger(Util.class).error("suicidal polymorphism: " + entityClass.getName()); //$NON-NLS-1$
            return null;
        }
        return getPrimary(entityClass, key);
    }

    /** Fetch by primary key; avoid suicidal classes */
    public static <T extends ModelEntity> T getPrimary(Class<T> entityClass, ExternalKey primaryKey) {
        Logger.getLogger(Util.class).info("getPrimary()"); //$NON-NLS-1$
        if (!(Modifier.isFinal(entityClass.getModifiers()))) {
            Logger.getLogger(Util.class).error("suicidal polymorphism: " + entityClass.getName()); //$NON-NLS-1$
            return null;
        }
        Session session = HibernateUtil.createSessionFactory(null, null).openSession();
        ReaderConstraint readerConstraint = new ReaderConstraint(entityClass);
        RecordFetcher<ExternalKey, ModelEntity> recordFetcher =
            new HibernateRecordFetcher(session, readerConstraint);
        T modelEntity = entityClass.cast(recordFetcher.get(primaryKey));
        session.close();

        if (modelEntity == null) {
            Logger.getLogger(Util.class).info("Nothing found"); //$NON-NLS-1$
            return null;
        }

        if (!(entityClass.isInstance(modelEntity))) {
            Logger.getLogger(Util.class).info("Weird type: " + modelEntity.getClass().getName()); //$NON-NLS-1$
            return null;
        }
        return modelEntity;
    }

    /**
     * Fill the supplied collection with instances of the specified class, subject to the specified
     * constraints.
     * 
     * TODO: break up polymorphic queries into final decendants
     */

    // TODO: copy the code here into ReaderConstraint -- make the record reader use this.
    public static <T extends ModelEntity> void fill(Class<T> collectionClass, // hack!
        ReaderConstraint readerConstraint, Collection<T> collection) {
        Class<? extends ModelEntity> entityClass = readerConstraint.getClassConstraint();
        if (entityClass == null)
            return;

        Logger.getLogger(Util.class).info("fill()"); //$NON-NLS-1$
        if (!(Modifier.isFinal(entityClass.getModifiers()))) {
            Logger.getLogger(Util.class).error("suicidal polymorphism: " + entityClass.getName()); //$NON-NLS-1$
            return;
        }
        SessionFactory factory = HibernateUtil.createSessionFactory(null, null);
        Session session = factory.openSession();

        ModelEntityHibernateRecordReader recordReader =
            new ModelEntityHibernateRecordReader(session, readerConstraint);
        recordReader.setPersistentClass(entityClass);

        try {
            recordReader.open();
            recordReader.readAll(collectionClass, collection);
            recordReader.close();
        } catch (InitializationException e) {
            Logger.getLogger(Util.class).error("fill failed: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    /** Write the array of entities to the db. */
    public static void write(ModelEntity[] entities) throws InitializationException {
        ModelEntityHibernateRecordWriter writer = new ModelEntityHibernateRecordWriter();
        writer.open();
        for (ModelEntity modelEntity : entities) {
            writer.write(modelEntity);
        }
        writer.close();
        writer.getSession().close();
    }

    /** Write the single entity to the db */
    public static void write(ModelEntity entity) throws InitializationException {
        write(new ModelEntity[] { entity });
    }
}
