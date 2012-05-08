/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.HibernateUtil;

/**
 * @author joel
 * 
 */
public class HibernateDataAccess implements DataAccess {
	/** We prepend this string to the XML name of the entity, to get the classname. */
	// private static final String MODEL_PACKAGE = "com.joelsgarage.model.";
	// //$NON-NLS-1$
	/** The namespace field of ModelEntity */

	private Logger logger = null;

	private SessionFactory factory;

	private Session xmlSession = null;
	/** A "secondary" session related to xmlSession. Needs no separate cleanup. */
	private Session dom4jSession = null;
	private Transaction xmlTransaction = null;

	/**
	 * 
	 * @param configResource
	 *            the hibernate.cfg.xml to use, null => default
	 * @param database
	 *            the database to use ("use <database>"), null => as specified in hibernate.cfg.xml
	 */
	public HibernateDataAccess(String configResource, String database) {
		setLogger(Logger.getLogger(HibernateDataAccess.class));
		setFactory(HibernateUtil.createSessionFactory(configResource, database));
	}

	/**
	 * Create the sessions and transactions
	 */
	public void setup() {
		// make the xml session

		setXmlSession(getFactory().openSession());
		if (getXmlSession() == null) {
			getLogger().info("null session"); //$NON-NLS-1$
			return;
		}
	}

	public void beginTransaction() {
		setXmlTransaction(getXmlSession().beginTransaction());

		if (getXmlTransaction() == null) {
			getLogger().info("null transaction"); //$NON-NLS-1$
			return;
		}
		setDom4jSession(getXmlSession().getSession(EntityMode.DOM4J));
	}

	public void commit() {
		getLogger().info("commit"); //$NON-NLS-1$
		if (getXmlTransaction() != null) {
			getXmlTransaction().commit();
			setXmlTransaction(null);
		}
	}

	public void cleanup() {
		getLogger().info("cleanup"); //$NON-NLS-1$
		cleanupTransactions();
		cleanupSessions();
	}

	protected void cleanupTransactions() {
		if (getXmlTransaction() != null) {
			getXmlTransaction().rollback();
		}
	}

	protected void cleanupSessions() {
		if (getXmlSession() != null) {
			getXmlSession().close();
		}
	}

	/**
	 * Use Hibernate to fetch the list.
	 * 
	 * Fetches the entire list at once, which is obviously a pretty bad dump strategy.
	 * 
	 * My dump ran out of memory, so I have to fix it now.
	 * 
	 * TODO: put the namespace param back in someday
	 * 
	 * @see com.joelsgarage.skiploader.DataAccess#fetchElements(java.lang.String)
	 */
	@Override
	// public List<Element> fetchElements(String namespace) throws FatalException {
	public List<Element> fetchElements() throws FatalException {

		List<Element> dbElements = new ArrayList<Element>();
		Criteria crit = getDom4jSession().createCriteria(ModelEntity.class);
		// if (namespace != null && namespace.length() > 0) {
		// crit.add(Property.forName(HibernateUtil.NAMESPACE).eq(namespace));
		// }
		try {
			// There must be a better way than this reference-copying.
			List<?> result = crit.list();
			for (Object o : result) {
				if (o instanceof Element) {
					dbElements.add((Element) o);
				} else {
					getLogger().info("got weird type " + o.getClass().getName()); //$NON-NLS-1$
				}
			}
			return dbElements;
		} catch (HibernateException e) {
			getLogger().error("fetch failed"); //$NON-NLS-1$
			throw new FatalException(e);
		}
	}

	/**
	 * Use Hibernate to save the element, based on its "id" field, assigning a new id if it's
	 * missing.
	 * 
	 * So, for this "save" operation what we really want is "insert if not already present," like a
	 * set.
	 * 
	 * So, gah, select to find it first. That ought to be a separate tiny transaction.
	 * 
	 * @see com.joelsgarage.skiploader.DataAccess#saveElement(org.dom4j.Element)
	 */
	@Override
	public void saveElement(Element element) throws FatalException {
		try {
//			getLogger().info("saveElement " + element.asXML()); //$NON-NLS-1$
//			getLogger().info("saveElement name " + element.getName()); //$NON-NLS-1$
//			getLogger().info("element type " + element.getClass().getName()); //$NON-NLS-1$

//			getLogger().info("entitymode" + getDom4jSession().getEntityMode().toString()); //$NON-NLS-1$
			ModelEntity entity = (ModelEntity) Translator.fromXML(element);
//			getLogger().info("key " + entity.getKey().toString()); //$NON-NLS-1$
			getXmlSession().replicate(entity, ReplicationMode.OVERWRITE);

		} catch (HibernateException e) {
			getLogger().error("save element failed: " + e.getMessage()); //$NON-NLS-1$
			cleanup();
			throw new FatalException(e);
		}
	}

	//

	public Logger getLogger() {
		return this.logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public Transaction getXmlTransaction() {
		return this.xmlTransaction;
	}

	public void setXmlTransaction(Transaction xmlTransaction) {
		this.xmlTransaction = xmlTransaction;
	}

	public Session getXmlSession() {
		return this.xmlSession;
	}

	public void setXmlSession(Session xmlSession) {
		this.xmlSession = xmlSession;
	}

	public Session getDom4jSession() {
		return this.dom4jSession;
	}

	public void setDom4jSession(Session dom4jSession) {
		this.dom4jSession = dom4jSession;
	}

	public SessionFactory getFactory() {
		return this.factory;
	}

	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}

}
