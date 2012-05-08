/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.joelsgarage.util.XMLUtil;

/**
 * Thread for "send" events coming from the FSM.
 * 
 * @author joel
 * 
 */
public class AsyncSender implements Runnable {
    private String target;
    private String targetType;
    @SuppressWarnings("unchecked")
    private Map params;
    private EngineManager.Listener listener;
    private Thread thread;

    @SuppressWarnings("unchecked")
    public AsyncSender(String target, String targetType, Map params, EngineManager.Listener listener) {
        Logger.getLogger(AsyncSender.class).info("ctor"); //$NON-NLS-1$
        this.target = target;
        this.targetType = targetType;
        this.params = params;
        this.listener = listener;
    }

    /**
     * Fire the trigger(s) asynchronously.
     */
    public void start() {
        Logger.getLogger(AsyncSender.class).info("start"); //$NON-NLS-1$
        setThread(new Thread(this));
        getThread().start();
        Logger.getLogger(AsyncSender.class).info("started"); //$NON-NLS-1$
    }

    public void run() {
        Logger.getLogger(AsyncSender.class).info("run"); //$NON-NLS-1$
        synchronized (this.listener) {
            this.listener.handleSend(this.target, this.targetType, makeDataMap(this.params));
        }
        Logger.getLogger(AsyncSender.class).info("ran"); //$NON-NLS-1$
    }

    // ///

    /**
     * This serves only to convert the 1.4-style Map to a 1.5-style parameterized Map<K, V>.
     */
    @SuppressWarnings("unchecked")
    protected static Map<String, Node> makeDataMap(Map params) {
        if (params == null)
            return null;
        Map<String, Node> dataMap = new HashMap<String, Node>();
        Set<Map.Entry<String, Node>> entrySet = params.entrySet();
        for (Map.Entry<String, Node> entry : entrySet) {
            String name = entry.getKey();
            if (name != null) {
                Node node = entry.getValue();
                Logger.getLogger(EngineManager.class).info("received 'send' key name: " + name); //$NON-NLS-1$
                Logger.getLogger(EngineManager.class).info(
                    "payload follows:\n" + XMLUtil.writeXML(node)); //$NON-NLS-1$

                String data = name.trim();
                if (data != null) {
                    dataMap.put(data, node);
                }
            }
        }
        return dataMap;
    }

    public Thread getThread() {
        return this.thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

}
