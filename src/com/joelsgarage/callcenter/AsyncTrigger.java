/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.model.ModelException;
import org.apache.log4j.Logger;

/**
 * Thread for "event" triggers into the FSM.
 * 
 * Copied from SCXML, where it is not public. Why not?
 * 
 * @author joel
 * 
 */
public class AsyncTrigger implements Runnable {
    /** The state machine executor. */
    private final SCXMLExecutor executor;
    /** The event(s) to be triggered. */
    private final TriggerEvent[] events;
    /** The log. */
    private final Log log = LogFactory.getLog(AsyncTrigger.class);
    private Thread thread;

    /**
     * Constructor.
     * 
     * @param executor
     *            The {@link SCXMLExecutor} to trigger the event on.
     * @param event
     *            The {@link TriggerEvent}.
     */
    public AsyncTrigger(final SCXMLExecutor executor, final TriggerEvent event) {
        Logger.getLogger(AsyncTrigger.class).info("ctor"); //$NON-NLS-1$
        this.executor = executor;
        this.events = new TriggerEvent[1];
        this.events[0] = event;
    }

    /**
     * Fire the trigger(s) asynchronously.
     */
    public void start() {
        Logger.getLogger(AsyncTrigger.class).info("start"); //$NON-NLS-1$
        setThread(new Thread(this));
        getThread().start();
    }

    /**
     * Fire the event(s).
     */
    public void run() {
        Logger.getLogger(AsyncTrigger.class).info("run"); //$NON-NLS-1$
        try {
            Logger.getLogger(AsyncTrigger.class).info("start"); //$NON-NLS-1$
            synchronized (this.executor) {
                Logger.getLogger(AsyncTrigger.class).info("synchronized"); //$NON-NLS-1$
                this.executor.triggerEvents(this.events);
            }
            Logger.getLogger(AsyncTrigger.class).info("done"); //$NON-NLS-1$
        } catch (ModelException me) {
            this.log.error(me.getMessage(), me);
        }
    }

    public Thread getThread() {
        return this.thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
