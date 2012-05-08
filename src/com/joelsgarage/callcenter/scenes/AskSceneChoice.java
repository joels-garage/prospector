/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import com.joelsgarage.callcenter.EventListener;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.SceneChooser;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.callcenter.SendTarget;
import com.joelsgarage.util.XMLUtil;

/**
 * Ask the user what scene they want to play. This class keeps the state about the choice.
 * 
 * @author joel
 */
public class AskSceneChoice extends HistoryBasedScenePlayer {
    /** sceneChoice outcome: user-specified scene name is good */
    private static final String ASK_SCENE_CHOICE_VALID = "ask_scene_choice_valid"; //$NON-NLS-1$
    /** sceneChoice outcome: user-specified scene name is good */
    private static final String ASK_SCENE_CHOICE_INVALID = "ask_scene_choice_invalid"; //$NON-NLS-1$
    /** Event access to the FSM */
    private EventListener listener;
    /** This provides access to the other scenes */
    private SceneChooser chooser;
    /** I forgot what this does */
    private static final double LESSENING = 1.0;

    public AskSceneChoice(EventListener listener, SceneChooser chooser) {
        setListener(listener);
        setChooser(chooser);
    }

    @Override
    protected String sceneName() {
        return Scenes.ASK_SCENE_CHOICE;
    }

    /** User gave us a choice, so record it and indicate if it's valid. */
    @SendTarget.Registrant
    public void sceneChoice(@SuppressWarnings("unused")
    String data, Element element) {
        String choice =
            XMLUtil.getNodeValueByPath(namespaceContext, element, "scxml:data/cc:sceneName/text()"); //$NON-NLS-1$
        Logger.getLogger(AskSceneChoice.class).info("Looking for scene: " + choice); //$NON-NLS-1$
        String outcome;
        if (Scenes.validScenes.contains(choice)) {
            Logger.getLogger(AskSceneChoice.class).info("Found scene: " + choice); //$NON-NLS-1$

            outcome = ASK_SCENE_CHOICE_VALID;
        } else {
            Logger.getLogger(AskSceneChoice.class).info("Can't find scene: " + choice); //$NON-NLS-1$

            outcome = ASK_SCENE_CHOICE_INVALID;
        }
        getListener().handleMessage(outcome, null);
    }

    /**
     * If user is admin, then recency score, otherwise never.
     */
    @Override
    public NextScene getNextScene() {
        if (getUser().isAdmin()) {  // admin only
            double recencyScore = recencyBasedScore() - LESSENING;
            return new NextScene(Scenes.ASK_SCENE_CHOICE, null, Double.valueOf(recencyScore));
        }
        return null;  // never
    }

    //

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public SceneChooser getChooser() {
        return this.chooser;
    }

    public void setChooser(SceneChooser chooser) {
        this.chooser = chooser;
    }
}
