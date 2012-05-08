/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

/**
 * Reads and writes from memory.
 * 
 * @author joel
 * 
 */
public class MockDataAccess implements DataAccess {
	private List<Element> elements;

	public MockDataAccess() {
		setElements(new ArrayList<Element>());
	}

	/**
	 * TODO: put namespace back
	 * 
	 * @see com.joelsgarage.skiploader.DataAccess#fetchElements(java.lang.String)
	 */
	@Override
	// public List<Element> fetchElements(String namespace) {
	public List<Element> fetchElements() {
		return getElements();
	}

	/**
	 * @see com.joelsgarage.skiploader.DataAccess#saveElement(org.dom4j.Element)
	 */
	@Override
	public void saveElement(Element element) {
		getElements().add(element);
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	public List<Element> getElements() {
		return this.elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void beginTransaction() {
		// TODO Auto-generated method stub

	}
}
