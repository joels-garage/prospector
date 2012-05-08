/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.w3c.dom.Node;

/**
 * This is the interface through which events are fired into the FSM engine.
 * 
 * For example, the chat facilitator fires "utterances" through this.
 * 
 * The Bridge implements it, and just delegates to EngineManager.fireEvent().
 * 
 * I could have just used that, I guess, so that all the event sources have a reference to the
 * engine itself, i.e. the bridge doesn't participate other than to "wire things up" on startup.
 * 
 * TODO: think more about that.
 * 
 * @author joel
 */
public interface EventListener {

	/**
	 * Queues an event to be fired into the FSM, after it's done with whatever it's doing now.
	 * 
	 * Returns immediately after creating the pending event (in its own thread).
	 * 
	 * @param eventName
	 *            the event to fire.
	 * @param payload
	 *            the DOM payload.
	 */
	public void handleMessage(String eventName, Node payload);

}
