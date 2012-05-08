/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

/**
 * This is one of the things that can be "send"ed from the state machine, thus received by the
 * EventDispatcher.
 * 
 * @author joel
 */
@Deprecated
public class WatchAction {
	private String action;

	public WatchAction() {
		// foo
	}

	@Deprecated
	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}
