/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Manages Hibernate sessions and transaction.
 * 
 * @author joel
 * 
 */
public class HbnSessionUtil {
	/** The session this thread is using */
	// ok, fuck, a single thread may want more than one session, e.g. one for reading, one for
	// writing.
	private static final ThreadLocal<Session> registry;
	/** Should this session be rolled back? */
	private static final ThreadLocal<Boolean> rollbackReg;

	static {
		registry = new ThreadLocal<Session>();
		rollbackReg = new ThreadLocal<Boolean>();
	}

	private HbnSessionUtil() {
		// foo
	}

	public static void beginTransaction() {
		System.out.println("begin transaction!"); //$NON-NLS-1$
		Session ses = getCurrentSession();
		if (ses == null) {
			ses = makeSession();
		}
		ses.beginTransaction();
	}

	public static Session makeSession() {
		System.out.println("make new session!"); //$NON-NLS-1$
		registry.set(HbnConfigUtil.getSessionFactory().openSession());
		rollbackReg.set(Boolean.FALSE);
		return getCurrentSession();

	}

	public static void rollbackOnly() {
		rollbackReg.set(Boolean.TRUE);
		Session session = getCurrentSession();
		if (session == null)
			return;
		session.clear();
	}

	public static Session getCurrentSession() {
		return registry.get();
	}

	public static void evict(Object entity) {
		getCurrentSession().evict(entity);
	}

	public static void evict(List<Object> entities) {
		for (Object o : entities)
			evict(o);
	}

	private static void commitTransaction() {
		Session ses = getCurrentSession();
		if (ses == null)
			return;
		Transaction trn = ses.getTransaction();
		if (trn == null)
			return;
		trn.commit();
	}

	private static void rollbackTransaction() {
		System.out.println("Rolling back transaction!"); //$NON-NLS-1$
		Session session = getCurrentSession();
		if (session == null)
			return;
		Transaction transaction = session.getTransaction();
		if (transaction == null)
			return;
		transaction.rollback();
	}

	/** Roll back if rollbackReg is set, otherwise commit */
	public static void resolveTransaction() {
		Boolean rollback = rollbackReg.get();
		if (rollback == null)
			return;
		if (rollback.booleanValue())
			rollbackTransaction();
		else
			commitTransaction();
	}

	public static void closeSession() {
		Session session = getCurrentSession();
		if (session == null)
			return;
		session.close();
		registry.set(null);
		rollbackReg.set(null);
	}
}