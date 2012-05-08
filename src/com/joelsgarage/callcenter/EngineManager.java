/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.env.SimpleErrorReporter;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;

import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Manages communication with the FSM engine, and holds the instance.
 * 
 * It's just here to make the bridge smaller.
 * 
 * So, to avoid deadlock, I think this class should be in its own thread.
 * 
 * @author joel
 * 
 */
public class EngineManager implements EventDispatcher {
    /** Handles "send" events sourced from the FSM. */
    public static interface Listener {
        public void handleSend(String target, String targetType, Map<String, Node> data);
    }

    /** The XML element containing the name of the interpreted event */
    public static final String EVENT = "event"; //$NON-NLS-1$
    /** The location of the SCXML configuration file */
    private static final String MAIN_SC_XML = "/main.sc.xml"; //$NON-NLS-1$

    /** The one and only listener for outbound "send" events -- this is expected to be the "Bridge" */
    private Listener listener;
    private SCXML stateMachine;
    private SCXMLExecutor engine;

    /** timer used to periodically probe the current FSM states */
    private Timer logTimer;

    public EngineManager(Listener listener) throws InitializationException {
        this(listener, MAIN_SC_XML);
    }

    public EngineManager(Listener listener, String configResource) throws InitializationException {
        Logger.getLogger(EngineManager.class).info("ctor"); //$NON-NLS-1$
        setListener(listener);
        setUpEngine(configResource);
    }

    /**
     * Setup the engine and start it.
     * 
     * @param configResource
     *            the name of the main SCXML document to work with *
     * @throws InitializationException
     *             if there's a problem, e.g. can't read the file.
     */

    protected void setUpEngine(String configResource) throws InitializationException {
        Logger.getLogger(EngineManager.class).info(
            "setUpEngine with configResource: " + String.valueOf(configResource)); //$NON-NLS-1$

        URL config = this.getClass().getResource(configResource);
        Logger.getLogger(EngineManager.class).info("url: " + config.getFile()); //$NON-NLS-1$

        List<CustomAction> customActions = null;
        ErrorHandler errHandler = new SimpleErrorHandler();

        Logger.getLogger(EngineManager.class).info("parsing SCXML config..."); //$NON-NLS-1$

        try {
            setStateMachine(SCXMLParser.parse(config, errHandler, customActions));
        } catch (Exception e) {
            Logger.getLogger(EngineManager.class).info("crap xml"); //$NON-NLS-1$
            throw new InitializationException("problem with " + configResource + e.getMessage()); //$NON-NLS-1$
        }
        Logger.getLogger(EngineManager.class).info("...done"); //$NON-NLS-1$

        Evaluator evaluator = new JexlEvaluator();
        ErrorReporter errorReporter = new SimpleErrorReporter();

        Logger.getLogger(EngineManager.class).info("creating SCXMLExecutor..."); //$NON-NLS-1$
        setEngine(new SCXMLExecutor(evaluator, this, errorReporter));
        Logger.getLogger(EngineManager.class).info("...done"); //$NON-NLS-1$

        getEngine().setStateMachine(getStateMachine());
        getEngine().setSuperStep(true);
        getEngine().setRootContext(new JexlContext());

        getEngine().addListener(getStateMachine(), new TransitionLogger());

        Logger.getLogger(EngineManager.class).info("about to go"); //$NON-NLS-1$

        try {
            // This runs until the engine is quiescent.
            getEngine().go();
        } catch (ModelException me) {
            Logger.getLogger(EngineManager.class).error(me.getMessage());
        }

        this.logTimer = new Timer();
        this.logTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                EngineManager.this.logState();
            }
        }, 3000, 3000);
    }

    protected void logState() {
        Logger.getLogger(EngineManager.class).info("logState"); //$NON-NLS-1$
        Set<?> states = getEngine().getCurrentStatus().getAllStates();
        for (Object state : states) {
            if (state instanceof TransitionTarget) {
                TransitionTarget tt = (TransitionTarget) state;
                Logger.getLogger(EngineManager.class).info("state: " + tt.getId()); //$NON-NLS-1$
            } else {
                Logger.getLogger(EngineManager.class).error(
                    "weird type: " + state.getClass().getName()); //$NON-NLS-1$
            }
        }
    }

    /**
     * Fire an external event into the engine.
     * 
     * See http://www.mail-archive.com/commons-user@jakarta.apache.org/msg17806.html
     * 
     * All events are fired "asynchronously".
     * 
     * @param eventName
     *            the event name to trigger
     * @param payload
     *            a Node that becomes _eventdata
     */
    public void fireEvent(final String eventName, final Node payload) {
        Logger.getLogger(EngineManager.class).info("firing event name: " + eventName); //$NON-NLS-1$
        Logger.getLogger(EngineManager.class)
            .info("payload follows:\n" + XMLUtil.writeXML(payload)); //$NON-NLS-1$

        // Asynchronous trigger briefly synchronizes the executor to fire the trigger.
        TriggerEvent triggerEvent = new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT, payload);
        AsyncTrigger asyncTrigger = new AsyncTrigger(getEngine(), triggerEvent);
        asyncTrigger.start();
    }

    /**
     * Does nothing; ok to stay in this thread to do it.
     * 
     * @see EventDispatcher#cancel(String)
     */
    @Override
    public void cancel(final String sendId) {
        Logger.getLogger(EngineManager.class).debug("got cancel( sendId: " + sendId + ")"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Handle "send" messages from the FSM.
     * 
     * In an attempt to avoid deadlock, the work here is done by a new thread.
     * 
     * TODO: some sort of worker pool.
     * 
     * The "params" arg here is really "namelist".
     * 
     * Most arguments here are ignored.
     * 
     * @see EventDispatcher#send(String, String, String, String, Map, Object, long, List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void send(final String sendId, //
        final String target, //
        final String targetType, // usually "x-java"
        final String event, //
        final Map params, // namelist
        final Object hints, //
        final long delay,//
        final List externalNodes) {
        if (getListener() == null) {
            Logger.getLogger(EngineManager.class).error("null listener"); //$NON-NLS-1$
            return;
        }

        SendLogger.logSend(sendId, target, targetType, event, params, hints, delay, externalNodes);

        if (target != null) {
            Logger.getLogger(EngineManager.class).info("received 'send' target name: " + target); //$NON-NLS-1$
        }

        if (params == null) {
            Logger.getLogger(EngineManager.class).info("null params (namelist)"); //$NON-NLS-1$
        }

        AsyncSender asyncSender = new AsyncSender(target, targetType, params, getListener());
        asyncSender.start();
    }

    /**
     * 
     * What's the name of the current state? Used by the demonstration (see StopWatchDisplay
     * usecase). Returns ONE of the concurrent states, if in multiple states.
     * 
     * @return
     */
    public String getCurrentState() {
        Set<?> states = getEngine().getCurrentStatus().getStates();
        // Just return *ONE* of the concurrent states.
        Object o = states.iterator().next();
        if (o instanceof State) {
            State s = (State) o;
            return s.getId();
        }
        return null;
    }

    //

    public Listener getListener() {
        return this.listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public SCXMLExecutor getEngine() {
        return this.engine;
    }

    public SCXML getStateMachine() {
        return this.stateMachine;
    }

    public void setStateMachine(SCXML stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void setEngine(SCXMLExecutor engine) {
        this.engine = engine;
    }
}
