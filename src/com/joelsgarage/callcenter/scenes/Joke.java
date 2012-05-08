/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.util.XMLUtil;

/**
 * @author joel
 */
public class Joke extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once per minute */
    private static final long MIN_RECENCY = 60000;
    private static final double LESSENING = 1.0;

    @Override
    protected String sceneName() {
        return Scenes.JOKE;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    @Override
    public NextScene getNextScene() {
        if (getUser().isAdmin()) { // admin only
            return new NextScene(Scenes.JOKE, getInput(), Double.valueOf(recencyBasedScore()
                - LESSENING));
        }
        return null;
    }

    /**
     * Create the input document for the joke scene, which contains the joke index from last time we
     * told a joke, or zero if we can't find last time.
     */
    public Document getInput() {
        Map<String, String> inputs = new HashMap<String, String>();
        // Default start joke index
        inputs.put("current_joke_index", "0"); //$NON-NLS-1$ //$NON-NLS-2$

        Document outputDoc = getMostRecentSceneOutput(Scenes.JOKE);

        String jokeIndex =
            XMLUtil.getNodeValueByPath(new CallCenterNamespaceContext(), outputDoc,
                "/cc:output/cc:current_joke_index/text()"); //$NON-NLS-1$

        if (jokeIndex != null && !(jokeIndex.isEmpty())) {
            inputs.put("current_joke_index", jokeIndex); //$NON-NLS-1$
        }

        return XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            HistoryBasedScenePlayer.INPUT, inputs);
    }
}
