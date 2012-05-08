/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.EventListener;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.callcenter.Util;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.Stakeholder;
import com.joelsgarage.util.XMLUtil;

/**
 * Get an individual preference from a user; maybe of a new individual.
 * 
 * TODO: overhaul this; ask for comments accompanying the preference.
 * 
 * @author joel
 * 
 */
public class GetIndividualPreference extends HistoryBasedScenePlayer {
    private EventListener listener;

    public GetIndividualPreference(EventListener listener) {
        setListener(listener);
    }

    @Override
    protected String sceneName() {
        return Scenes.GET_INDIVIDUAL_PREFERENCE;
    }

    @Override
    public NextScene getNextScene() {
        Decision decision = getCurrentDecision();
        double score;
        if (decision == null) {
            score = Double.NEGATIVE_INFINITY;
        } else {
            score = recencyBasedScore();
        }

        return new NextScene(Scenes.GET_INDIVIDUAL_PREFERENCE,
            makeGetIndividualPreferenceInput(decision), Double.valueOf(score));
    }

    /**
     * The input document contains the ENTIRE current decision, since we need more than one field
     * from it and I don't like doing the lookups from the SCXML, since we ALWAYS need the class
     * key. If the lookup was rare, then fine. But we should be attaching structured stuff to the
     * input anyway.
     * 
     * @param sceneList
     * @return
     */
    protected Document makeGetIndividualPreferenceInput(Decision decision) {
        if (decision == null)
            return null;
        Map<String, Object> queryTerms = new HashMap<String, Object>();
        queryTerms.put("decisionKey", decision.makeKey()); //$NON-NLS-1$
        queryTerms.put("userKey", getUser().makeKey()); //$NON-NLS-1$
        Stakeholder stakeholder = Util.getCompound(Stakeholder.class, queryTerms);
        return makeGetIndividualPreferenceInputImpl(decision, stakeholder);
    }

    /** produce an SCXML input document containing the specified decision and stakeholder */
    protected Document makeGetIndividualPreferenceInputImpl(Decision decision,
        Stakeholder stakeholder) {
        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
        inputs.put("stakeholder", makeNode(stakeholder)); //$NON-NLS-1$
        return XMLUtil.makeDocFromNodes(
            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            HistoryBasedScenePlayer.INPUT, inputs);
    }

    //

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }
}
