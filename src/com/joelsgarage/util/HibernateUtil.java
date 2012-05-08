package com.joelsgarage.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * This is a session factory factory. The idea is that the caller wants to manage its own
 * sessionfactory, so this util is called to configure and retrieve it.
 * 
 * @author joel
 * 
 */
public abstract class HibernateUtil {
    private static final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url"; //$NON-NLS-1$
    private static final String DEFAULT_CFG = "/hibernate.cfg.xml"; //$NON-NLS-1$
    // public static final String NAMESPACE = "namespace"; //$NON-NLS-1$
    private static final String CONNECTION_PREFIX = "jdbc:mysql://localhost/"; //$NON-NLS-1$

    /** Cache them so we don't keep reparsing the config over and over */
    public static final Map<String, SessionFactory> factoryCache =
        new HashMap<String, SessionFactory>();

    /**
     * Return a new (or cached if it already exists) sessionfactory as specified.
     * 
     * @param resource
     *            configuration file (e.g. hibernate.cfg.xml). null gets the default config
     * @param database
     *            name of the desired database. null gets the database specified in the config.
     * @return
     * @throws HibernateException
     */
    public static SessionFactory createSessionFactory(String resource, String database)
        throws HibernateException {
        Logger.getLogger(HibernateUtil.class).info("Trying config resource: " + resource); //$NON-NLS-1$

        String actualResource;
        if (resource != null && resource.length() > 0) {
            actualResource = resource;
        } else {
            actualResource = DEFAULT_CFG;
            Logger.getLogger(HibernateUtil.class).info("Using default config."); //$NON-NLS-1$
        }

        Logger.getLogger(HibernateUtil.class).info("Using config resource: " + actualResource); //$NON-NLS-1$

        // The String.valueOf() is in case database is null.
        String cacheKey = actualResource + String.valueOf(database);

        SessionFactory cachedFactory = getFactoryCache().get(cacheKey);
        if (cachedFactory != null)
            return cachedFactory;

        Logger.getLogger(HibernateUtil.class).info("Using config resource: " + actualResource); //$NON-NLS-1$

        Configuration baseConfig = new Configuration().configure(actualResource);

        if (database != null) {
            baseConfig.setProperty(HIBERNATE_CONNECTION_URL, CONNECTION_PREFIX + database);
        }

        SessionFactory sessionFactory = baseConfig.buildSessionFactory();

        getFactoryCache().put(cacheKey, sessionFactory);

        return sessionFactory;
    }

    /**
     * Verify that the specified class is known by Hibernate, assuming the static initializer has
     * done its thing.
     * 
     * @param entityClass
     * @return
     */
    public static boolean isValidEntityType(SessionFactory factory, Class<?> entityClass) {
        if (factory == null) {
            Logger.getLogger(HibernateUtil.class).error("Null factory."); //$NON-NLS-1$
            return false;
        }
        if (factory.getClassMetadata(entityClass) == null)
            return false;
        return true;
    }

    public static Map<String, SessionFactory> getFactoryCache() {
        return factoryCache;
    }
}
