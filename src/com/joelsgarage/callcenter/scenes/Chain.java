/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.SceneChooser;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.model.Scene;
import com.joelsgarage.util.XMLUtil;

/**
 * Facilitates "chaining," where one scene specifies the next scene to run, through its output.
 * 
 * Looks at the most-recent output for a chain payload, and if found, produces an infinite-score
 * next scene.
 * 
 * @author joel
 */
public class Chain extends HistoryBasedScenePlayer {
    /** This allows us to get the nextScene (complete with input) for the chained scene */
    private SceneChooser chooser;

    private static final Set<String> predecessors = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add(Scenes.ASK_SCENE_CHOICE);
        }
    };

    public Chain(SceneChooser chooser) {
        setChooser(chooser);
    }

    @Override
    protected String sceneName() {
        return Scenes.CHAIN;
    }


    /** If the previous scene specified a chain scene, then play it. Else null */
    @Override
    public NextScene getNextScene() {
        Scene mostRecentScene = getHistory().last();
        if (mostRecentScene == null) {
            Logger.getLogger(Chain.class).info("no recent scene at all"); //$NON-NLS-1$
            return null;
        }
        Logger.getLogger(Chain.class).info(
            "most recent scene name: " + mostRecentScene.getDescription()); //$NON-NLS-1$
        if (!(predecessors.contains(mostRecentScene.getDescription()))) {
            Logger.getLogger(Chain.class).info("no qualified predecessor"); //$NON-NLS-1$
            return null;
        }

        Logger.getLogger(Chain.class).info(
            "found qualified predecessor: " + mostRecentScene.getDescription()); //$NON-NLS-1$

        String xmlOutput = mostRecentScene.getOutput();
        Logger.getLogger(Chain.class).info("Looking at output: " + xmlOutput); //$NON-NLS-1$

        Document document = XMLUtil.readXML(xmlOutput);
        String sceneName =
            XMLUtil.getNodeValueByPath(new CallCenterNamespaceContext(), document,
                "/cc:output/cc:sceneName/text()"); //$NON-NLS-1$

        Logger.getLogger(Chain.class).info("got scene name: " + sceneName); //$NON-NLS-1$

        if (sceneName == null || sceneName.isEmpty()) {
            Logger.getLogger(Chain.class).info(
                "failed to find chosen scene in doc: " + XMLUtil.writeXML(document)); //$NON-NLS-1$
            return null;
        }

        if (!(Scenes.validScenes.contains(sceneName))) {
            Logger.getLogger(Chain.class).info("User chose invalid scene " + sceneName); //$NON-NLS-1$
            return null;
        }

        Logger.getLogger(Chain.class).info("User chose scene " + sceneName); //$NON-NLS-1$
        HistoryBasedScenePlayer aScene = getChooser().getAllScenes().get(sceneName);
        if (aScene == null)
            return null;
        aScene.setHistory(getHistory());
        NextScene nextScene = aScene.getNextScene();
        if (nextScene == null)
            return null;
        // Boost the priority to infinity, since this is what the user wanted.
        nextScene.score = Double.valueOf(Double.POSITIVE_INFINITY);
        return nextScene;
    }
    
    //
    //

    public SceneChooser getChooser() {
        return this.chooser;
    }

    public void setChooser(SceneChooser chooser) {
        this.chooser = chooser;
    }
}
