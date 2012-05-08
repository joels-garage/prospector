/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.joelsgarage.util.XMLUtil;

/**
 * Bridges between the "bridge" and the watch itself, containing it.
 * 
 * @author joel
 * 
 */
public class WatchManager implements SendTarget {
    private static final String UNSILENCE = "unsilence"; //$NON-NLS-1$
    private static final String SILENCE = "silence"; //$NON-NLS-1$
    private static final String DISPLAY_ROOT = "Display"; //$NON-NLS-1$
    private static final String DISPLAY = "display"; //$NON-NLS-1$
    // for reflection; these do nothing but save us some instantiation
    private static final Class<?>[] SIGNATURE = new Class[0];
    private static final Object[] PARAMETERS = new Object[0];

    /** "send" payload for a user action changing the watch state */
    private static final String WATCH_ACTION = "WatchAction"; //$NON-NLS-1$

    /** event thrown at the FSM, to speak the time -- the watch sources this */
    private static final String CUCKOO = "cuckoo"; //$NON-NLS-1$
    /** If true, don't cuckoo */
    private boolean silent = true;

    private Logger logger;
    private EventListener listener;
    private StopWatch stopWatch;
    /** We spontaneously utter the current time when this timer tells us to */
    private Timer cuckoo;

    public WatchManager(EventListener listener) {
        setLogger(Logger.getLogger(WatchManager.class));
        setListener(listener);
        setStopWatch(new StopWatch());

        this.cuckoo = new Timer();
        this.cuckoo.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                WatchManager.this.cuckooUpdate();
            }
        }, 3000, 3000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.joelsgarage.callcenter.SendTarget#registerThyself(com.joelsgarage.callcenter.SendTargetRegistry)
     */
    @Override
    public void registerThyself(SendTargetRegistry registry) {
        registry.registerSendTarget(WATCH_ACTION, this);
    }

    /**
     * Fire a "say the time" event into the FSM, unless we're in "silent" mode.
     */
    protected void cuckooUpdate() {
        if (this.silent)
            return;
        String timeString = getDisplay();
        String event = CUCKOO;

        Map<String, String> elements = new HashMap<String, String>();
        elements.put(DISPLAY, timeString);
        Document doc =
            XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                DISPLAY_ROOT, elements);

        getListener().handleMessage(event, doc);
    }

    /**
     * The only "send" target we understand is the "watch_action" one, which tells us to do
     * something to the watch, like start or stop it.
     * 
     * This method intercepts some actions (e.g. silence) and passes the rest to the watch instance
     * through reflection.
     * 
     * @see SendTarget#send(Element)
     */
    @Override
    public void send(String data, Element e) {
        if (!(data.equals(WATCH_ACTION))) {
            getLogger().error("bailing, got unexpected data type " + data); //$NON-NLS-1$
            return;
        }

        String action =
            XMLUtil.getNodeValueByPath(new CallCenterNamespaceContext(), e,
                "scxml:data/cc:action/text()"); //$NON-NLS-1$

        if (action == null) {
            getLogger().error("no action"); //$NON-NLS-1$
            return;
        }

        // Look for the silence command
        if (action.equals(SILENCE)) {
            this.silent = true;
            return;
        }
        if (action.equals(UNSILENCE)) {
            this.silent = false;
            return;
        }

        // Otherwise assume the action is a methodname of the Stopwatch class.
        if (invoke(getStopWatch().getClass(), action)) {
            getLogger().info("invokation succeeded"); //$NON-NLS-1$
        } else {
            getLogger().info("invokation failed"); //$NON-NLS-1$
        }
    }

    public boolean invoke(final Class<?> clas, final String methodName) {
        getLogger().info("Trying to invoke method " + methodName); //$NON-NLS-1$
        try {
            Method method = clas.getDeclaredMethod(methodName, SIGNATURE);
            method.invoke(getStopWatch(), PARAMETERS);
        } catch (SecurityException se) {
            getLogger().error(se);
            return false;
        } catch (NoSuchMethodException nsme) {
            getLogger().error(nsme);
            return false;
        } catch (IllegalArgumentException iae) {
            getLogger().error(iae);
            return false;
        } catch (IllegalAccessException iae) {
            getLogger().error(iae);
            return false;
        } catch (InvocationTargetException ite) {
            getLogger().error(ite);
            return false;
        }
        return true;
    }

    public String getDisplay() {
        return getStopWatch().getDisplay();
    }

    public StopWatch getStopWatch() {
        return this.stopWatch;
    }

    public void setStopWatch(StopWatch stopWatch) {
        this.stopWatch = stopWatch;
    }

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
