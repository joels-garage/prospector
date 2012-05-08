/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Property;

import com.joelsgarage.dataprocessing.services.ModelAccess;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.Dowser;
import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.HibernateUtil;

/**
 * Hibernate data access. Called by ModelAccessImpl and HibernateAlternativeStore.
 * 
 * @author joel
 * 
 */
public class HibernateModelAccess implements ModelAccess {
    private static final String NAME = "name"; //$NON-NLS-1$
    /** The property (see ModelEntity.hbm.xml) name for the key (composite-id) */
    // private static final String KEY_PROPERTY = "key"; //$NON-NLS-1$
    /** The primary key is now actually this id field */
    private static final String ID_PROPERTY = "id"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;
    private Logger logger = null;

    private SessionFactory factory;

    // /** Populates the hash key */
    // private IdGenerator idGenerator;

    private Dowser dowser;

    public HibernateModelAccess(String configResource) {
        setFactory(HibernateUtil.createSessionFactory(configResource, null));

        setLogger(Logger.getLogger(HibernateModelAccess.class));

        // getLogger().info("new id generator..."); //$NON-NLS-1$
        // setIdGenerator(new IdGenerator());

        getLogger().info("new dowser..."); //$NON-NLS-1$
        setDowser(DowserFactory.newDowser());

        getLogger().info("All done"); //$NON-NLS-1$
    }

    @SuppressWarnings("nls")
    @Override
    public ModelEntity persist(ModelEntity instance) {
        Transaction transaction = null;
        Session session = null;
        getLogger().info("Persisting instance: " + instance.toString());
        try {
            getLogger().info("create"); //$NON-NLS-1$

            // session = HibernateUtil.getSessionFactory().openSession();

            session = getFactory().openSession();

            if (session == null) {
                getLogger().info("null session in persist()"); //$NON-NLS-1$
                return null;
            }

            transaction = session.beginTransaction();
            if (transaction == null) {
                getLogger().info("null transaction in persist()"); //$NON-NLS-1$
                return null;
            }

            // if ((instance.getId() == null) || (instance.getId().longValue() == 0l)) {
            if (instance.makeKey() == null) {
                // this should never happen
                getLogger().error("instance key is null"); //$NON-NLS-1$
                // TODO: throw an exception
            } else {
                getLogger().info("update id1: " + instance.makeKey().toString()); //$NON-NLS-1$
                // now the id is prepopulated, so we don't need to do this.
                // String id = getIdGenerator().generate(null, instance);
                // instance.setId(id);
                session.saveOrUpdate(instance);
                getLogger().info("update id2: " + instance.makeKey().toString()); //$NON-NLS-1$
            }
            transaction.commit();
            return instance;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            getLogger().error("HibernateException " + e.getLocalizedMessage()); //$NON-NLS-1$
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(ModelEntity instance) {
        Transaction transaction = null;
        Session session = null;

        try {
            getLogger().info("delete"); //$NON-NLS-1$
            session = getFactory().openSession();

            if (session == null) {
                getLogger().info("null session in delete()"); //$NON-NLS-1$
                return;
            }

            transaction = session.beginTransaction();
            if (transaction == null) {
                getLogger().info("null transaction in delete()"); //$NON-NLS-1$
                return;
            }

            // String id = getIdGenerator().generate(null, instance);
            // instance.setId(id);

            session.delete(instance);

            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            getLogger().error("HibernateException " + e.getLocalizedMessage()); //$NON-NLS-1$
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.joelsgarage.prospector.client.services.ModelAccess#findByName(java.lang.String,
     *      java.lang.String, int, int)
     */
    @Override
    public List<ModelEntity> findByName(String name, String className, int page, int pageSize) {
        try {
            Class<?> desiredClass = Class.forName(className);
            if (desiredClass == null) {
                getLogger().error(
                    "Invalid class specified, bailing out: " + String.valueOf(className)); //$NON-NLS-1$
                return new ArrayList<ModelEntity>();
            }

            if (!Dowser.isAllowed(desiredClass)) {
                getLogger().error(
                    "Suicidal class specified, bailing out: " + String.valueOf(className)); //$NON-NLS-1$
                return new ArrayList<ModelEntity>();
            }
            List<ModelEntity> results;

            if ((name == null) || (name.length() == 0)) {
                getLogger().info("findbyname for null name"); //$NON-NLS-1$
                results = findByCriteria(className, page, pageSize);
            } else {
                getLogger().info("findbyname for name " + name + " and class " + className); //$NON-NLS-1$ //$NON-NLS-2$
                Criterion[] criterionList = new Criterion[1];
                criterionList[0] = Property.forName(NAME).eq(name);
                results = findByCriteria(className, page, pageSize, criterionList);
            }
            return results;
        } catch (ClassNotFoundException e) {
            getLogger().error("Invalid class specified, bailing out: " + String.valueOf(className)); //$NON-NLS-1$
            return new ArrayList<ModelEntity>();
        }
    }

    @Override
    public Map<String, List<ModelEntity>> findByNameMultiType(String name, String className,
        int page, int pageSize) {
        try {
            Class<?> clas = Class.forName(className);
            if (clas == null) {
                Logger.getLogger(HibernateModelAccess.class).info("null class for: " + className); //$NON-NLS-1$
                return new HashMap<String, List<ModelEntity>>();
            }

            Class<? extends ModelEntity> modelEntityClass = clas.asSubclass(ModelEntity.class);
            if (modelEntityClass == null) {
                Logger.getLogger(HibernateModelAccess.class).info(
                    "null modelEntityClass for: " + className); //$NON-NLS-1$
                return new HashMap<String, List<ModelEntity>>();
            }

            Set<Class<? extends ModelEntity>> subclasses =
                getDowser().getAllowedSubtypes().get(modelEntityClass);
            if (subclasses == null) {
                Logger.getLogger(HibernateModelAccess.class).info(
                    "null subclasses for: " + className); //$NON-NLS-1$
                return new HashMap<String, List<ModelEntity>>();
            }

            Map<String, List<ModelEntity>> results = new HashMap<String, List<ModelEntity>>();
            for (Class<? extends ModelEntity> subclas : subclasses) {
                if (!Dowser.isAllowed(subclas)) {
                    Logger.getLogger(HibernateModelAccess.class).info(
                        "skip disallowed class: " + subclas.getName()); //$NON-NLS-1$
                    continue;
                }
                Logger.getLogger(HibernateModelAccess.class).info(
                    "one of those for type " + clas.getName()); //$NON-NLS-1$
                results.put(subclas.getName(), findByName(name, subclas.getName(), page, pageSize));
            }
            return results;
        } catch (ClassNotFoundException e) {
            getLogger().error("Invalid class specified, bailing out: " + String.valueOf(className)); //$NON-NLS-1$
            return new HashMap<String, List<ModelEntity>>();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.joelsgarage.prospector.client.services.ModelAccess#findByKey(com.joelsgarage.model.ExternalKey,
     *      java.lang.String, int, int)
     */
    @Override
    public List<ModelEntity> findByKey(ExternalKey key, String className, int page, int pageSize) {
        try {
            if (!Dowser.isAllowed(Class.forName(className))) {
                getLogger().info(
                    "Suicidal class specified, bailing out: " + String.valueOf(className)); //$NON-NLS-1$
                return new ArrayList<ModelEntity>();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            getLogger().info("Invalid class specified, bailing out: " + String.valueOf(className)); //$NON-NLS-1$
            return new ArrayList<ModelEntity>();
        }
        List<ModelEntity> results;
        if (key == null) { // fetch all
            getLogger().info("findbykey for null key"); //$NON-NLS-1$
            results = findByCriteria(className, page, pageSize);
        } else {
            getLogger().info("find key " + key.toString() + " class " + className); //$NON-NLS-1$ //$NON-NLS-2$
            Criterion[] criterionList = new Criterion[1];
            // criterionList[0] = Property.forName(KEY_PROPERTY).eq(key);
            // Look up by key is slow, because it's not indexed. Instead, look up by id.
            //
            // now the lookup key is part of the key.
            byte[] id = key.getKey();
            // ModelEntity entity = new MeasurementQuantity(); // a class I can instantiate
            // entity.setKey(key);
            // String id = getIdGenerator().generate(null, entity);
            criterionList[0] = Property.forName(ID_PROPERTY).eq(id);

            results = findByCriteria(className, page, pageSize, criterionList);
        }
        return results;
    }

    /**
     * Just returns the first item from findByKey(). No more get(), when fetching by key.
     */
    @Override
    public ModelEntity read(ExternalKey key, String className) {
        Logger.getLogger(HibernateModelAccess.class).info("read key: " + key.toString() //$NON-NLS-1$
            + " className: " + className); //$NON-NLS-1$
        List<ModelEntity> list = findByKey(key, className, 0, 1);
        if (list == null)
            return null;
        if (list.size() == 0)
            return null;
        return list.get(0);
    }

    /**
     * Fetches some client-side model entity according to some criteria.
     * 
     * @param className
     *            fully qualified class of the entity type you want, e.g.
     *            com.joelsgarage.prospector.client.model.ModelEntity
     * @param page
     * @param pageSize
     * @param criterion
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List<ModelEntity> findByCriteria(String className, int page, int pageSize,
        Criterion... criterion) {
        Transaction transaction = null;
        Session session = null;

        try {
            getLogger().info("findByCriteria"); //$NON-NLS-1$
            session = getFactory().openSession();

            if (session == null) {
                getLogger().info("null session in find()"); //$NON-NLS-1$
                return null;
            }

            transaction = session.beginTransaction();
            if (transaction == null) {
                getLogger().info("null transaction in find()"); //$NON-NLS-1$
                return null;
            }

            List<ModelEntity> result = null;
            Criteria crit = null;

            if (className == null) {
                getLogger().info("no class restriction"); //$NON-NLS-1$
                // use the default class
                crit = session.createCriteria(ModelEntity.class);
            } else {
                getLogger().info("using class restriction on " + className); //$NON-NLS-1$
                crit = session.createCriteria(className);
            }

            for (Criterion c : criterion) {
                crit.add(c);
            }

            crit.setFirstResult(pageSize * page);
            // The extra one is to see if there are subsequent pages
            crit.setMaxResults(pageSize + 1);
            crit.setFetchSize(pageSize + 1);

            result = crit.list();

            getLogger().info("committing..."); //$NON-NLS-1$
            transaction.commit();
            getLogger().info("returning result with this many rows: " + result.size()); //$NON-NLS-1$
            // GWT RPC freaks out on empty lists.
            if (result.size() == 0) {
                result = null;
            }

            return result;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            getLogger().error("HibernateException " + e.getLocalizedMessage()); //$NON-NLS-1$
            return null;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public SessionFactory getFactory() {
        return this.factory;
    }

    public void setFactory(SessionFactory factory) {
        this.factory = factory;
    }

    // public IdGenerator getIdGenerator() {
    // return this.idGenerator;
    // }
    //
    // public void setIdGenerator(IdGenerator idGenerator) {
    // this.idGenerator = idGenerator;
    // }

    public Dowser getDowser() {
        return this.dowser;
    }

    public void setDowser(Dowser dowser) {
        this.dowser = dowser;
    }
}
