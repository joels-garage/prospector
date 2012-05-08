/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Vector;

import org.w3c.dom.Node;

/**
 * Listens to events inbound to the FSM and records them.
 * 
 * @author joel
 * 
 */
public class FakeEventListener implements EventListener {
    private Vector<String> events;
    private Vector<Node> payloads;

    public FakeEventListener() {
        setEvents(new Vector<String>());
        setPayloads(new Vector<Node>());
    }

    @Override
    public void handleMessage(String event, Node payload) {
        getEvents().add(event);
        getPayloads().add(payload);
    }

    //

    public Vector<String> getEvents() {
        return this.events;
    }

    public void setEvents(Vector<String> events) {
        this.events = events;
    }

    public Vector<Node> getPayloads() {
        return this.payloads;
    }

    public void setPayloads(Vector<Node> payloads) {
        this.payloads = payloads;
    }
}
