/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.apache.commons.scxml.SCXMLListener;
import org.apache.commons.scxml.model.Transition;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;

/**
 * An SCXML listener that just logs transitions.
 * 
 * @author joel
 */
public class TransitionLogger implements SCXMLListener {
    private Logger logger;

    public TransitionLogger() {
        setLogger(Logger.getLogger(TransitionLogger.class));
    }

    @Override
    public void onEntry(TransitionTarget state) {
        String stateId = state.getId();
        if (stateId != null) {
            getLogger().info("entered state " + stateId); //$NON-NLS-1$
        }
    }

    @Override
    public void onExit(TransitionTarget state) {
        String stateId = state.getId();
        if (stateId != null) {
            getLogger().info("exited state " + stateId); //$NON-NLS-1$
        }
    }

    @Override
    public void onTransition(TransitionTarget from, TransitionTarget to, Transition transition) {
        String fromId = from.getId();
        String toId = to.getId();
        if (fromId != null && toId != null) {
            getLogger().info("transition from  " + fromId + " to " + toId); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
