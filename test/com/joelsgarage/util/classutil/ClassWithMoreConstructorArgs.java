/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util.classutil;

public class ClassWithMoreConstructorArgs {
	private String foo;
	private int bar;

	public ClassWithMoreConstructorArgs(String foo, int bar) {
		setFoo(foo);
		setBar(bar);
	}

	public String getFoo() {
		return this.foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	public int getBar() {
		return this.bar;
	}

	public void setBar(int bar) {
		this.bar = bar;
	}
}
