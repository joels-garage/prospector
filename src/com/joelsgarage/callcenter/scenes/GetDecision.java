/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.util.XMLUtil;

/**
 * GetDecision asks the user what they want to work on.
 * 
 * TODO: overhaul this (maybe make a copy)
 * 
 * @author joel
 * 
 */
public class GetDecision extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once per minute */
    private static final long MIN_RECENCY = 60000;

    @Override
    protected String sceneName() {
        return Scenes.GET_DECISION;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    /** If there's an active decision, don't ask this at all */
    @Override
    public NextScene getNextScene() {
        // Now it's admin only for this version.
        if (getUser().isAdmin()) {
            double score = recencyBasedScore();
            if (getCurrentDecision() == null) {
                // no current decision, so we should DEFINITELY play!
                score += 10.0;
            } else {
                // we have a current decision, so we don't need to play.
                score += -10.0;
            }

            return new NextScene(Scenes.GET_DECISION, makeGetDecisionInput(), Double.valueOf(score));
        }
        return null;
    }

    /** Input is the current user, because we sometimes create a new stakeholder */
    protected Document makeGetDecisionInput() {
        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("user", makeNode(getUser())); //$NON-NLS-1$
        return XMLUtil.makeDocFromNodes(
            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            HistoryBasedScenePlayer.INPUT, inputs);
    }
}
