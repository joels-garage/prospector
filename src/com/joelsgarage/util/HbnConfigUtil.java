package com.joelsgarage.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public abstract class HbnConfigUtil {
	private static SessionFactory sessionFactory;
	/** configuration resource to use */
	private static String resource = "/hibernate.cfg.xml"; //$NON-NLS-1$

	// Initialize on startup to save some time on the first request.
	static {
		buildSessionFactory();
	}

	public static void setResource(String resource) {
		HbnConfigUtil.resource = resource;
	}

	/**
	 * Verify that the specified class is known by Hibernate, initializing the session if required.
	 * 
	 * @param entityClass
	 * @return
	 */
	public static boolean isValidEntityType(Class<?> entityClass) {
		if (HbnConfigUtil.getSessionFactory().getClassMetadata(entityClass) == null)
			return false;
		return true;
	}

	// If for some reason the session factory disappears, produce another one, and
	// log this weird event.
	static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			System.out.println("ERROR: session factory null, this shouldn't happen."); //$NON-NLS-1$
			return buildSessionFactory();
		}
		return sessionFactory;
	}

	/**
	 * Construct a new session factory using hibernate.cfg.xml, and return it. Throw an exception if
	 * this is not possible.
	 * 
	 * @return
	 */
	protected static SessionFactory buildSessionFactory() {
		try {
			sessionFactory = new Configuration().configure(HbnConfigUtil.resource)
				.buildSessionFactory();
			if (sessionFactory == null)
				throw new ExceptionInInitializerError("null session factory"); //$NON-NLS-1$
			return sessionFactory;
		} catch (Throwable ex) {
			System.err.println("SessionFactory creation failed."); //$NON-NLS-1$
			ex.printStackTrace();
			throw new ExceptionInInitializerError(ex);
		}
	}
}