/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.InitializationException;

/**
 * "Bridges" between the various "service" modules (e.g. the chat, the database) and the FSM, which
 * contains all the "action" logic (i.e. what to do in case of various stimuli)
 * 
 * @author joel
 */
public class Bridge implements EventListener, EngineManager.Listener, SendTargetRegistry {
    /** targettype of messages we handle */
    private static final String X_JAVA = "x-java"; //$NON-NLS-1$
    /** On startup, this means we called the user */
    private static final String ORIGINATE = "originate"; //$NON-NLS-1$
    /** On startup, this means the user called us. */
    private static final String ANSWER = "answer"; //$NON-NLS-1$
    /** Contains the SCXML executor */
    private EngineManager engineManager;
    /** Decides which scene to play next */
    private SceneChooser sceneChooser;
    /** True if we are supposed to originate the chat (i.e. fire the "originate" event on startup) */
    private boolean originate;
    private Thread thread;

    /**
     * Which targets get what payload.
     * 
     * key: the name of the payload data element
     * 
     * values: the classes that should get the payload sent to them.
     */
    private Multimap<String, SendTarget> sendTargets = new HashMultimap<String, SendTarget>();

    /**
     * This class is instantiated with a chat, and a mode.
     * 
     * @throws InitializationException
     *             if the Engine can't be initialized.
     */
    public Bridge() {
        Logger.getLogger(Bridge.class).info("Constructing Bridge"); //$NON-NLS-1$
    }

    /**
     * Populate and start the bridge
     * 
     * @throws FatalException
     *             if keypopulation fails
     */
    public void run(Chat chat, boolean aOriginate, SceneChooser aSceneChooser)
        throws InitializationException, FatalException {
        Logger.getLogger(Bridge.class).info("run"); //$NON-NLS-1$

        setOriginate(aOriginate);

        // upon instantiation the engine manager waits for an event to play some scene.
        setEngineManager(new EngineManager(this));

        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$

        setSceneChooser(aSceneChooser);
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$

        Set<SendTarget> targets = new HashSet<SendTarget>();
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$
        targets.add(new WatchManager(this));
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$
        targets.add(new ChatFacilitator(this, new SmackChatAdapter(chat)));
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$
        targets.add(new RankedAlternativeManager());
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$
        targets.add(new Cogitator(this));
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$
        targets.add(new Sandbox(this));
        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$
        targets.add(getSceneChooser());

        Logger.getLogger(Bridge.class).info("foo"); //$NON-NLS-1$

        // Ask each to register. If they don't register, they get GC'ed.
        for (SendTarget target : targets) {
            if (target != null) {
                target.registerThyself(this);
            }
        }

        if (isOriginate()) {
            Logger.getLogger(Bridge.class).info("Originate"); //$NON-NLS-1$
            getEngineManager().fireEvent(ORIGINATE, null);
        } else {
            Logger.getLogger(Bridge.class).info("Answer"); //$NON-NLS-1$
            getEngineManager().fireEvent(ANSWER, null);
        }
    }

    @Override
    public void registerSendTarget(String name, SendTarget sendTarget) {
        Logger.getLogger(Bridge.class).info("Registering: " + name); //$NON-NLS-1$
        getSendTargets().put(name, sendTarget);
    }

    /**
     * Performs the external actions requested by the Engine. The only way the Engine can make a
     * request is to use a "send" message, in which the "namelist" field (dereferenced and included
     * here) indicates data we should handle.
     * 
     * @param target
     *            the target is like a "data" key without a value. if the same string is used as
     *            both a target and a data map key, the map takes precedence (if the value is null
     *            then the effect is the same either way). using 'target' makes the 'send' syntax
     *            for argument-less function calls more compact (you don't need a vacuous datamodel
     *            element just to do the call).
     * @param targetType
     *            type type of this event; must be "x-java" for us to pay attention
     * @param dataMap
     *            the namelist payload, a map of XML nodes. The key indicates the type, thus the
     *            verb, i.e. what we should do with the value.
     * @see EngineManager.Listener#handleSend(String, String, Map)
     */
    @Override
    public void handleSend(String target, String targetType, // must be "x-java"
        Map<String, Node> dataMap) {
        Logger.getLogger(Bridge.class).info("received send for target: " + String.valueOf(target)); //$NON-NLS-1$

        if (targetType == null || !(targetType.trim().equalsIgnoreCase(X_JAVA))) {
            Logger.getLogger(Bridge.class).info("Wrong or missing targetType"); //$NON-NLS-1$
            return;
        }

        if (target == null && dataMap == null) {
            Logger.getLogger(Bridge.class).info("null args; no response needed"); //$NON-NLS-1$
            return;
        }

        // use the target if there's no datamap, or if there is but it doesn't have the same key
        if (target != null && (dataMap == null || !dataMap.containsKey(target))) {
            Logger.getLogger(Bridge.class).info("sending target: " + target); //$NON-NLS-1$
            sendForTarget(target, null);
        }

        if (dataMap == null) {
            Logger.getLogger(Bridge.class).info("null datamap"); //$NON-NLS-1$
            return;
        }
        for (Map.Entry<String, Node> entry : dataMap.entrySet()) {
            Node node = entry.getValue();
            if (node instanceof Element) {
                sendForTarget(entry.getKey(), (Element) node);
            } else {
                Logger.getLogger(Bridge.class).info("node is not element"); //$NON-NLS-1$
            }
        }
    }

    /** Send the payload to the correct SendTarget (i.e. lookup by name) */
    protected void sendForTarget(String name, Element element) {
        Collection<SendTarget> sendTargetCollection = getSendTargets().get(name);
        if (sendTargetCollection.isEmpty()) {
            Logger.getLogger(Bridge.class).info("unrecognized namelist entry: " + name); //$NON-NLS-1$
        }
        for (SendTarget sendTarget : sendTargetCollection) {
            Logger.getLogger(Bridge.class).info("sending name " + name); //$NON-NLS-1$
            sendTarget.send(name, element);
        }
    }

    /**
     * Handle a message from the ChatFacilitator by firing the event into the Engine.
     * 
     * @param event
     *            the event corresponding to the recognizer's interpretation of the message
     * @param utterance
     *            the payload containing the verbatim chat message
     * 
     * @see EventListener#handleMessage(EngineManager.Event, Node)
     */
    @Override
    public void handleMessage(String event, Node utterance) {
        getEngineManager().fireEvent(event, utterance);
    }

    //

    public EngineManager getEngineManager() {
        return this.engineManager;
    }

    public void setEngineManager(EngineManager engineManager) {
        this.engineManager = engineManager;
    }

    public Multimap<String, SendTarget> getSendTargets() {
        return this.sendTargets;
    }

    public void setSendTargets(Multimap<String, SendTarget> sendTargets) {
        this.sendTargets = sendTargets;
    }

    public boolean isOriginate() {
        return this.originate;
    }

    public void setOriginate(boolean originate) {
        this.originate = originate;
    }

    public SceneChooser getSceneChooser() {
        return this.sceneChooser;
    }

    public void setSceneChooser(SceneChooser sceneChooser) {
        this.sceneChooser = sceneChooser;
    }

    public Thread getThread() {
        return this.thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
