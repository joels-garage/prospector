/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util.classutil;

public class ClassWithConstructorArgs {
	private String foo;

	public ClassWithConstructorArgs(String foo) {
		setFoo(foo);
	}

	public String getFoo() {
		return this.foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}
}
