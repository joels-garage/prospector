/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.WrongClassException;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Restrictions;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.HibernateUtil;

/**
 * Scans all records of type T from the database.
 * 
 * @author joel
 * 
 */
public abstract class HibernateRecordReader<T extends ModelEntity> extends
    ConstrainedRecordReader<T> {
    /**
     * The class of the Hibernate entity. Note hibernate makes a mess of polymorphic queries, so we
     * have to implement this as a set of queries on final classes.
     */
    private Class<? extends T> persistentClass;
    /** The list of classes we will query, one by one */
    private List<Class<? extends T>> classList;
    private ListIterator<Class<? extends T>> classIterator;
    private Class<? extends T> currentClass;
    /** The authority for final subclasses */
    private Dowser dowser;
    /** The current result batch */
    private List<?> list;
    /** The current iterator for this batch */
    private ListIterator<?> iterator;
    /** Results are fetched in batches this big */
    private int batchSize = 5000;
    /** Count batches */
    private int batchNumber = 0;
    /** The current Hibernate session */
    private Session session;
    /** Stop looking for more rows. */
    private boolean finished = false;
    /** Hibernate conjunction of specified constraints */
    private Conjunction conjunction;
    /** True if we should commit at the end */
    private boolean committable = true;

    /**
     * Construct a new reader.
     * 
     * @param readerConstraint
     *            optional restrictions
     * @throws InitializationException
     *             if the parameter type is not an entity type known by Hibernate -- in this case
     *             there is nothing to read.
     */
    public HibernateRecordReader(Session session, ReaderConstraint readerConstraint) {
        super(readerConstraint);
        setSession(session);
    }

    @SuppressWarnings( { "unchecked" })
    public void open() throws InitializationException {
        Logger.getLogger(HibernateRecordReader.class).info("open"); //$NON-NLS-1$

        if (getConstraint() != null) {
            setConjunction(Restrictions.conjunction());
            Map<String, Object> constraints = getConstraint().getConstraints();
            if (constraints != null) {
                for (Map.Entry<String, Object> entry : constraints.entrySet()) {
                    getConjunction().add(Restrictions.eq(entry.getKey(), entry.getValue()));
                }
            }
        }

        Class<T> parameterClass =
            (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];

        if (getPersistentClass() == null) {
            // The default class is the parameter, but the user could set it to something else,
            // as long as it's a subclass of the parameter.
            //
            // Also the ReaderConstraint can specify a child class of the parameter; if so, use
            // that.
            Class<? extends ModelEntity> classConstraint = getConstraint().getClassConstraint();
            if (classConstraint != null) {
                if (parameterClass.isAssignableFrom(classConstraint)) {
                    // FIXME:
                    // FIXME: I think this cast does the wrong thing.
                    // FIXME:
                    setPersistentClass(classConstraint.asSubclass(parameterClass));
                } else {
                    throw new InitializationException(
                        "Constraint  class: " + classConstraint.getName() //$NON-NLS-1$
                            + " not compatible with parameter: " + parameterClass.getName()); //$NON-NLS-1$

                }

            } else {
                setPersistentClass(parameterClass);
            }
        }
        if (!parameterClass.isAssignableFrom(getPersistentClass()))
            throw new InitializationException("Persistent class: " + getPersistentClass().getName() //$NON-NLS-1$
                + " not compatible with parameter: " + parameterClass.getName()); //$NON-NLS-1$

        setDowser(DowserFactory.newDowser());
        setClassList(new ArrayList<Class<? extends T>>());

        if (Modifier.isFinal(getPersistentClass().getModifiers())) {
            Logger.getLogger(HibernateRecordReader.class).info(
                "Using class" + getPersistentClass().getName()); //$NON-NLS-1$
            // The list is just one class
            getClassList().add(getPersistentClass());
        } else {
            Logger.getLogger(HibernateRecordReader.class).info("Several classes."); //$NON-NLS-1$
            // add the final subclasses.
            Set<Class<? extends ModelEntity>> subclasses =
                getDowser().getAllowedSubtypes().get(getPersistentClass());
            if (subclasses == null) {
                throw new InitializationException("Not final, but no subtypes!  Give up!"); //$NON-NLS-1$
            }
            for (Class<? extends ModelEntity> clas : subclasses) {
                Logger.getLogger(HibernateRecordReader.class).info(
                    "one of those for type " + clas.getName()); //$NON-NLS-1$
                if (Modifier.isFinal(clas.getModifiers())) {
                    // ONLY final types, to avoid the horror query.
                    getClassList().add((Class<? extends T>) clas);
                }
            }
        }

        // Check that all the final types are types that Hibernate knows about.
        for (Class<? extends T> clas : getClassList()) {
            if (!HibernateUtil.isValidEntityType(getSession().getSessionFactory(), clas)) {
                throw new InitializationException("No Hibernate Config for type " //$NON-NLS-1$
                    + getPersistentClass().getName());
            }
        }

        setClassIterator(getClassList().listIterator());
        setCurrentClass(getClassIterator().next());
        getSession().setFlushMode(FlushMode.MANUAL); // read-only, don't sync it
        getSession().beginTransaction();
    }

    /** fetch the batch indexed by page */
    protected void fetchBatch(int page) {
        Logger.getLogger(HibernateRecordReader.class).info(
            "fetching batch: " + String.valueOf(page)); //$NON-NLS-1$

        if (!getSession().isOpen()) {
            // something bad happened, the session is gone.
            Logger.getLogger(HibernateRecordReader.class).error("Session disappeared"); //$NON-NLS-1$
            setList(null);
            return;
        }

        if (!Modifier.isFinal(getCurrentClass().getModifiers())) {
            Logger.getLogger(HibernateRecordReader.class).error(
                "attempted suicidal query for class: " + getCurrentClass().getName()); //$NON-NLS-1$
            return;
        }

        Logger.getLogger(HibernateRecordReader.class).info(
            "class: " + String.valueOf(getCurrentClass().getName())); //$NON-NLS-1$

        Criteria crit = getSession().createCriteria(getCurrentClass());
        if (getConjunction() != null)
            crit.add(getConjunction());
        crit.setFirstResult(getBatchSize() * page);
        crit.setMaxResults(getBatchSize());
        crit.setFetchSize(getBatchSize());

        try {
            setList(crit.list());
            Logger.getLogger(HibernateRecordReader.class).info("list size: " + getList().size()); //$NON-NLS-1$
        } catch (WrongClassException ex) {
            // This may be ok; just continue.
            Logger.getLogger(HibernateRecordReader.class).error(
                "Continuing after caught WrongClassException: " + ex.getMessage()); //$NON-NLS-1$
            return;
        } catch (HibernateException ex) {
            // any other type, log it and rethrow it
            Logger.getLogger(HibernateRecordReader.class).error(
                "Unknown exception " + ex.getMessage()); //$NON-NLS-1$
            throw ex;
        } finally {
            renew();
        }
    }

    protected void renew() {
        getSession().flush();
        getSession().getTransaction().commit();
        getSession().clear(); // flush the cache too
        getSession().beginTransaction();
    }

    /** Close the reader, which commits the transaction but not the underlying session */
    public void close() {
        setFinished(true);
        Session theSession = getSession();
        if (theSession == null)
            return;
        if (!theSession.isOpen())
            return;
        Transaction transaction = theSession.getTransaction();
        if (transaction == null)
            return;
        if (!transaction.isActive())
            return;

        theSession.flush();

        if (isCommittable()) {
            transaction.commit();
        } else {
            transaction.rollback();
        }
        // session closing handled here until I rediscover why not to do that.
        theSession.close();
    }

    /**
     * @see com.joelsgarage.extractor.ExtractorReader#read()
     */
    @SuppressWarnings("unchecked")
    @Override
    public T read() {
        // if we're done, return null.
        if (isFinished())
            return null;

        // fill up the iterator if it's empty or null (first time through)
        if (getIterator() == null || !getIterator().hasNext()) {
            Logger.getLogger(HibernateRecordReader.class).info("fetch a batch"); //$NON-NLS-1$
            fetchBatch(getBatchNumber());
            setBatchNumber(getBatchNumber() + 1);
            // check if we have a full batch, if there are any records to be had.
            if (getList() == null || getList().size() == 0) {
                if (!getClassIterator().hasNext()) {
                    // there are no more types, so we're done
                    close();
                    Logger.getLogger(HibernateRecordReader.class).info("no more types"); //$NON-NLS-1$
                    return null;
                }
                // There are more types, so try again with the next one.\
                // Don't stop until all the classes have been tried.
                setCurrentClass(getClassIterator().next());
                Logger.getLogger(HibernateRecordReader.class).info(
                    "next class: " + getCurrentClass().getName()); //$NON-NLS-1$
                setBatchNumber(0);
                Logger.getLogger(HibernateRecordReader.class).info("return next type"); //$NON-NLS-1$
                return read();
            }
            // ok, we do have a batch. make an iterator for it
            setIterator(getList().listIterator());
            Logger.getLogger(HibernateRecordReader.class).info("list size:" + getList().size()); //$NON-NLS-1$

            if (getIterator() == null || !getIterator().hasNext()) {
                Logger.getLogger(HibernateRecordReader.class).info("weird failure"); //$NON-NLS-1$
                // the iterator we just made is empty. this shouldn't happen.
                close();
                return null;
            }
        }

        // here we are not finished, and we have a non-null, full iterator.
        T result = (T) getIterator().next();
        return result;
    }

    //
    //

    public List<?> getList() {
        return this.list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public ListIterator<?> getIterator() {
        return this.iterator;
    }

    public void setIterator(ListIterator<?> iterator) {
        this.iterator = iterator;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public Class<? extends T> getPersistentClass() {
        return this.persistentClass;
    }

    public void setPersistentClass(Class<? extends T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public int getBatchNumber() {
        return this.batchNumber;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isCommittable() {
        return this.committable;
    }

    public void setCommittable(boolean committable) {
        this.committable = committable;
    }

    public List<Class<? extends T>> getClassList() {
        return this.classList;
    }

    public void setClassList(List<Class<? extends T>> classList) {
        this.classList = classList;
    }

    public Dowser getDowser() {
        return this.dowser;
    }

    public void setDowser(Dowser dowser) {
        this.dowser = dowser;
    }

    public ListIterator<Class<? extends T>> getClassIterator() {
        return this.classIterator;
    }

    public void setClassIterator(ListIterator<Class<? extends T>> classIterator) {
        this.classIterator = classIterator;
    }

    public Class<? extends T> getCurrentClass() {
        return this.currentClass;
    }

    public void setCurrentClass(Class<? extends T> currentClass) {
        this.currentClass = currentClass;
    }

    public Conjunction getConjunction() {
        return this.conjunction;
    }

    public void setConjunction(Conjunction conjunction) {
        this.conjunction = conjunction;
    }
}
