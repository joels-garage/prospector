/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.util.List;

import org.dom4j.Element;

import com.joelsgarage.util.FatalException;

/**
 * Very simple data access used by Skiploader.
 * 
 * @author joel
 * 
 */
public interface DataAccess {
	/**
	 * Do stuff like making database connections.
	 */
	public void setup();

	/**
	 * Clean up stuff like database connections.
	 */
	public void cleanup();

	/**
	 * Begin the contained transaction.
	 */
	public void beginTransaction();

	/**
	 * If you're happy with the result, commit it.
	 */
	public void commit();

	/**
	 * Fetch a (maybe very large) list of elements from the database, optionally restricted by
	 * namespace.
	 * 
	 * TODO: put namespace back in someday.
	 * 
	 * @param namespace
	 *            restriction on the ModelEntity.namespace field. Use null (or blank) to get
	 *            everything.
	 * @return the list
	 */
	public List<Element> fetchElements() throws FatalException;

	// public List<Element> fetchElements(String namespace) throws FatalException;

	/**
	 * Persist an element into the db.
	 * 
	 * @param element
	 */
	public void saveElement(Element element) throws FatalException;
}
