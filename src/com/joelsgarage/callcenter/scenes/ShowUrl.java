/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.model.Decision;
import com.joelsgarage.util.XMLUtil;

/**
 * The ShowUrl scene simply utters the Decision URL available in the Stripes server.
 * 
 * @author joel
 * 
 */
public class ShowUrl extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once per minute */
    private static final long MIN_RECENCY = 60000;

    @Override
    protected String sceneName() {
        return Scenes.SHOW_URL;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    /**
     * We always select ourselves as the next scene.
     * 
     * TODO: pull up the recency stuff -- all scenes have recency cutoffs (except repetitive "rate"
     * ones);
     */
    @Override
    public NextScene getNextScene() {
        return new NextScene(Scenes.SHOW_URL, getInputImpl(getCurrentDecision()), Double
            .valueOf(recencyBasedScore()));
    }

    /**
     * Produce the input document containing the specified decision.
     */
    protected Document getInputImpl(Decision decision) {
        if (decision == null)
            return null;
        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
        Document resultDoc =
            XMLUtil.makeDocFromNodes(
                CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                HistoryBasedScenePlayer.INPUT, inputs);
        Logger.getLogger(ShowUrl.class).info("input: " + XMLUtil.writeXML(resultDoc)); //$NON-NLS-1$
        return resultDoc;
    }
}
