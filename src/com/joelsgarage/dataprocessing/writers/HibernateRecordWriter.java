/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.FlushMode;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.HibernateUtil;
import com.joelsgarage.util.KeyUtil;

/**
 * Writes records of type T to the database. Takes a session as a parameter.
 * 
 * @author joel
 * 
 */
public abstract class HibernateRecordWriter<T> implements RecordWriter<T> {
    /** The class of the Hibernate entity */
    private Class<T> persistentClass;
    /** The current Hibernate session */
    private Session session;
    /** Results are committed in batches this big */
    private int batchSize = 10;
    /** Count records in this batch */
    private int recordCount = 0;
    /** True if we should commit at the end */
    private boolean committable = true;
    // /** Populates the hash key */
    // private IdGenerator idGenerator;
    /** What's this? */
    private List<T> list;
    /**
     * The id's for this batch, to avoid duplication. Uses the base64 version of the id so that i
     * can use it as a hash key; otherwise the byte[] is hashed incorrectly. i don't like this; i
     * could wrap the byte[] in a class providing equals() and be happy, but this is easier.
     */
    private Set<String> batchIds;

    /** Without an argument, you get a new (probably cached) session factory and a new session */
    public HibernateRecordWriter() throws InitializationException {
        this(HibernateUtil.createSessionFactory(null, null).openSession());
    }

    @SuppressWarnings("unchecked")
    public HibernateRecordWriter(Session session) throws InitializationException {
        setPersistentClass((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0]);
        setList(new ArrayList<T>());
        setSession(session);
        // setIdGenerator(new IdGenerator());
        setBatchIds(new HashSet<String>());

        // Check that the parameter is a type that Hibernate knows about.
        if (!HibernateUtil
            .isValidEntityType(getSession().getSessionFactory(), getPersistentClass())) {
            throw new InitializationException("No Hibernate Config for type " //$NON-NLS-1$
                + getPersistentClass().getName());
        }
    }

    public void open() {
        getSession().setFlushMode(FlushMode.MANUAL); // read-only, don't sync it
        getSession().beginTransaction();
    }

    /**
     * @see com.joelsgarage.extractor.RecordWriter#write(java.lang.String)
     */
    public void write(T payload) {
        if (!getSession().isOpen()) {
            // something bad happened, the session is gone.
            Logger.getLogger(HibernateRecordWriter.class).error("Session disappeared"); //$NON-NLS-1$
            return;
        }
        // Logger.getLogger(HibernateRecordWriter.class).error("Writing record"); //$NON-NLS-1$

        // MUST set the "id" field before replicating.
        // Don't want to put it in the ModelEntity class because it's a client class.
        // Which is pretty annoying.

        // OK, now the id is prepopulated, so i don't need to do this anymore.

        if (payload instanceof ModelEntity) {
            ModelEntity entity = (ModelEntity) payload;
            byte[] id = entity.getId();
            // String id = getIdGenerator().generate(null, entity);
            if (id != null) {
                String base64Id = KeyUtil.encode(id);
                if (getBatchIds().contains(base64Id)) {
                    // oops, we will fail if we include this in the batch, so just ignore it.
                    Logger.getLogger(HibernateRecordWriter.class).info(
                        "Ignoring duplicate record: " + base64Id); //$NON-NLS-1$
                    return;
                }
                Logger.getLogger(HibernateRecordWriter.class).debug(
                    "Replicating record: " + KeyUtil.encode(id)); //$NON-NLS-1$
                // entity.setId(id);
                getBatchIds().add(base64Id);
            } else {
                // this is bad.
                Logger.getLogger(HibernateRecordWriter.class).error("Null id"); //$NON-NLS-1$
                return;
            }
        }

        // This is kind of a hack. Because the id field must be unique per batch, we keep a list of
        // them.

        getSession().replicate(payload, ReplicationMode.OVERWRITE);
        setRecordCount(getRecordCount() + 1);
        if (getRecordCount() >= getBatchSize()) {
            // If we've written a batch, commit it and start another transaction.
            // check to see if commit is the right thing, maybe rollback is
            getSession().flush();
            getSession().getTransaction().commit();
            getSession().clear(); // flush the cache
            getBatchIds().clear();
            getSession().beginTransaction();
            setRecordCount(0);
        }
    }

    public void close() {
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
        // session closing is handled by the caller.
        // theSession.close();
    }

    // Accessors

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Class<T> getPersistentClass() {
        return this.persistentClass;
    }

    public void setPersistentClass(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    public Session getSession() {
        return this.session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getRecordCount() {
        return this.recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public boolean isCommittable() {
        return this.committable;
    }

    public void setCommittable(boolean committable) {
        this.committable = committable;
    }

    //
    // public IdGenerator getIdGenerator() {
    // return this.idGenerator;
    // }
    //
    // public void setIdGenerator(IdGenerator idGenerator) {
    // this.idGenerator = idGenerator;
    // }

    public Set<String> getBatchIds() {
        return this.batchIds;
    }

    public void setBatchIds(Set<String> batchIds) {
        this.batchIds = batchIds;
    }
}
