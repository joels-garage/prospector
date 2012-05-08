/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.show.model;

/**
 * Contains one instance of T.
 * 
 * This exists to enable dynamically typed fields in Java, without using a collection.
 * 
 * @author joel
 */
public class Instance<T> {
	private T instance;

	public Instance() {
		// foo
	}

	public Instance(T instance) {
		setInstance(instance);
	}

	public T getInstance() {
		return this.instance;
	}

	public void setInstance(T instance) {
		this.instance = instance;
	}
}
