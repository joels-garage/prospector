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
 * Asks the user for the decision class. If found, looks for a decision of which this user is a
 * stakeholder. If found, done. If not found, create one. It's like get_decision but with no dialog
 * branches.
 * 
 * Note that there may be more than one class matching the user's response, so, I need to ask about
 * a list of possibilities, I guess. Or I could just say "did you mean X" and require the user to
 * try again. The bobbing-for-apples aspect of that scheme doesn't seem appealing.
 * 
 * For now, just ignore this issue entirely. Use the recognizer to identify entities.
 * 
 * @author joel
 * 
 */
public class GetDecisionLite extends HistoryBasedScenePlayer {
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

    /** Input is the current user, because we sometimes create a new stakeholder */
    protected Document makeGetDecisionInput() {
        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("user", makeNode(getUser())); //$NON-NLS-1$
        return XMLUtil.makeDocFromNodes(
            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            HistoryBasedScenePlayer.INPUT, inputs);
    }
}
