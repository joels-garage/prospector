/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordFetcher;
import com.joelsgarage.model.ModelEntity;

/**
 * Base fetcher implementation. Implements Hibernate fetch access for any key type.
 * 
 * It closes its session when you call close();
 * 
 * @author joel
 * 
 */
public abstract class HibernateRecordFetcherBase<T> implements RecordFetcher<T, ModelEntity> {
    /** Hibernate session for this fetcher. */
    private Session session;
    /** Read constraint (e.g. persistent class) for this fetcher. */
    private ReaderConstraint constraint;

    public HibernateRecordFetcherBase(Session session, ReaderConstraint constraint) {
        setSession(session);
        setConstraint(constraint);
    }

    /** The subclass needs to specify the desired field. */
    @Override
    public ModelEntity get(T key) {
        return getByCriteria(Property.forName(getMainFieldName()).eq((key)));
    }

    /** What field to use for the simple get() accessor */
    public abstract String getMainFieldName();

    @Override
    public ModelEntity get(String fieldName, T key) {
        return getByCriteria(Property.forName(fieldName).eq((key)));
    }

    /** Clean up the session */
    public void close() {
        if (getSession() == null)
            return;
        if (getSession().isOpen())
            getSession().close();
    }

    @Override
    public <S extends ModelEntity> S getCompound(Class<S> constraintClass,
        Map<String, Object> queryTerms) {
        Map<String, Object> properties = new HashMap<String, Object>();

        if (constraintClass == null) {
            Logger.getLogger(HibernateRecordFetcherBase.class).error("no constraint class!"); //$NON-NLS-1$
            return null;
        }
        if (!(Modifier.isFinal(constraintClass.getModifiers()))) {
            Logger.getLogger(HibernateRecordFetcherBase.class).error(
                "suicidal polymorphism: " + constraintClass.getName()); //$NON-NLS-1$
            return null;
        }

        // First get (all) the stakeholders corresponding to this user
        String queryString = "from " + constraintClass.getName() //$NON-NLS-1$
            + " as c"; //$NON-NLS-1$

        boolean first = true;
        if (queryTerms != null) {
            for (Map.Entry<String, Object> entry : queryTerms.entrySet()) {
                if (first) {
                    queryString += " where"; //$NON-NLS-1$
                    first = false;
                } else {
                    queryString += " and"; //$NON-NLS-1$
                }
                queryString += " c." + entry.getKey()// //$NON-NLS-1$
                    + " = :" + entry.getKey(); //$NON-NLS-1$
                properties.put(entry.getKey(), entry.getValue());
            }
        }

        getSession().setFlushMode(FlushMode.MANUAL); // read-only, don't sync it
        getSession().beginTransaction();
        Query query = getSession().createQuery(queryString);

        query.setProperties(properties);
        query.setReadOnly(true);
        query.setFirstResult(0);
        query.setMaxResults(1000);
        query.setFetchSize(1000);

        Iterator<?> iterator = query.iterate();
        if (!iterator.hasNext()) {
            return null;
        }
        Object result = iterator.next();
        if (!constraintClass.isInstance(result)) {
            Logger.getLogger(HibernateRecordFetcherBase.class).error(
                "got weird type: " + result.getClass().getName()); //$NON-NLS-1$
        }
        return constraintClass.cast(result);
    }

    public ModelEntity getCompound(Map<String, Object> queryTerms) {
        Class<? extends ModelEntity> constraintClass = getConstraint().getClassConstraint();
        return getCompound(constraintClass, queryTerms);
    }

    protected ModelEntity getByCriteria(Criterion... criterion) {
        getSession().setFlushMode(FlushMode.MANUAL); // read-only, don't sync it
        getSession().beginTransaction();
        Logger.getLogger(HibernateRecordFetcherBase.class).info(
            "constraint class " + getConstraint().getClassConstraint().getName()); //$NON-NLS-1$
        Criteria crit = getSession().createCriteria(getConstraint().getClassConstraint());

        for (Criterion c : criterion) {
            crit.add(c);
        }
        crit.setFirstResult(0);
        crit.setMaxResults(1);
        crit.setFetchSize(1);

        List<?> list = crit.list();

        Logger.getLogger(HibernateRecordFetcherBase.class).info("Got rows: " + list.size()); //$NON-NLS-1$

        getSession().flush(); // i don't think this does anything
        getSession().getTransaction().commit();
        getSession().clear(); // flush the cache; there's another one downstream.
        if (list.size() == 0) {
            Logger.getLogger(HibernateRecordFetcherBase.class).error("No rows"); //$NON-NLS-1$
            return null;
        }
        Object item = list.get(0);
        if (item == null) {
            Logger.getLogger(HibernateRecordFetcherBase.class).error("Null returned"); //$NON-NLS-1$
        }
        if (item instanceof ModelEntity)
            return (ModelEntity) item;
        Logger.getLogger(HibernateRecordFetcherBase.class).error("Bad type"); //$NON-NLS-1$
        return null;
    }

    protected Session getSession() {
        return this.session;
    }

    protected void setSession(Session session) {
        this.session = session;
    }

    protected ReaderConstraint getConstraint() {
        return this.constraint;
    }

    protected void setConstraint(ReaderConstraint constraint) {
        this.constraint = constraint;
    }
}
