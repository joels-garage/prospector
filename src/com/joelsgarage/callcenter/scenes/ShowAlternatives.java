/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.EventListener;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.callcenter.SendTarget;
import com.joelsgarage.logic.AlternativeRanker;
import com.joelsgarage.logic.AlternativeStore;
import com.joelsgarage.logic.HibernateAlternativeStore;
import com.joelsgarage.logic.UtilityAlternativeRanker;
import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.RankedAlternativeCollection;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.XMLUtil;

/**
 * Look up the alternatives list.
 * 
 * @author joel
 * 
 */
public class ShowAlternatives extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once every five minutes */
    private static final long MIN_RECENCY = 300000;
    private EventListener listener;

    private AlternativeStore store;
    private AlternativeRanker ranker;
    private List<RankedAlternativeCollection> alternatives;

    public ShowAlternatives(EventListener listener) {
        setListener(listener);
        setStore(new HibernateAlternativeStore("")); //$NON-NLS-1$
        // setRanker(new PreferenceAlternativeRanker(getStore()));
        setRanker(new UtilityAlternativeRanker(getStore()));

    }

    @Override
    protected String sceneName() {
        return Scenes.SHOW_ALTERNATIVES;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    /**
     * Get the list of top alternatives for the current decision. I'd like to do this as a
     * structured list, but for now, it's just a verbatim string.
     * 
     * Inputs: cc:key == current decision key
     * 
     * @throws FatalException
     *             if key population fails
     * 
     */
    @SendTarget.Registrant
    public void getAlternatives(String data, @SuppressWarnings("unused")
    Element element) throws FatalException {
        Node keyNode =
            XMLUtil.getNodeByPath(namespaceContext, element, "/scxml:data/cc:key/p:ExternalKey"); //$NON-NLS-1$
        ExternalKey key = XMLUtil.makeKeyFromNode(keyNode);
        if (key == null)
            return;

        Logger.getLogger(ShowAlternatives.class).info("Got key: " + key.toString()); //$NON-NLS-1$

        setAlternatives(getRanker().getRankedList(10, key));

        Map<String, String> elements = new HashMap<String, String>();

        if (getAlternatives().size() == 0) {
            elements.put("error", String.valueOf("got no alternatives")); //$NON-NLS-1$ //$NON-NLS-2$
            getListener().handleMessage(
                data + FAIL,
                XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                    "Result", elements)); //$NON-NLS-1$
            return;
        }

        if (getAlternatives().size() > 1) {
            elements.put("warn", String.valueOf("extra alternative lists returned")); //$NON-NLS-1$//$NON-NLS-2$
        }

        RankedAlternativeCollection rankedAlternativeCollection = getAlternatives().get(0);
        Decision decision = rankedAlternativeCollection.getDecision();

        if (decision == null) {
            elements.put("error", String.valueOf("no decision returned")); //$NON-NLS-1$ //$NON-NLS-2$
            getListener().handleMessage(
                data + FAIL,
                XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                    "Result", elements)); //$NON-NLS-1$
            return;

        }

        if (!decision.makeKey().equals(key)) {
            elements.put("error", String.valueOf("wrong decision returned")); //$NON-NLS-1$ //$NON-NLS-2$
            getListener().handleMessage(
                data + FAIL,
                XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                    "Result", elements)); //$NON-NLS-1$
            return;
        }

        ArrayList<AnnotatedAlternative> annotatedAlternatives =
            rankedAlternativeCollection.getAlternatives();

        if (annotatedAlternatives.size() == 0) {
            elements.put("error", String.valueOf("got no alternatives")); //$NON-NLS-1$ //$NON-NLS-2$
            getListener().handleMessage(
                data + FAIL,
                XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                    "Result", elements)); //$NON-NLS-1$
            return;

        }

        String commaSeparatedList = annotatedAlternatives.get(0).getIndividual().getName();
        if (annotatedAlternatives.size() > 1) {
            for (int iter = 1; iter < annotatedAlternatives.size(); ++iter) {
                commaSeparatedList +=
                    ", " + annotatedAlternatives.get(iter).getIndividual().getName(); //$NON-NLS-1$

            }
        }

        elements.put("alternatives", commaSeparatedList); //$NON-NLS-1$
        getListener().handleMessage(
            data + DONE,
            XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "Result", elements)); //$NON-NLS-1$
    }

    @Override
    public NextScene getNextScene() {
        return new NextScene(Scenes.SHOW_ALTERNATIVES, makeShowAlternativesInput(), Double
            .valueOf(recencyBasedScore()));
    }

    /** show_alternatives just needs the current decision */
    protected Document makeShowAlternativesInput() {
        Decision decision = getCurrentDecision();
        return getInputImpl(decision);
    }

    /** produce an SCXML input document containing the specified decision and stakeholder */
    protected Document getInputImpl(Decision decision) {
        if (decision == null)
            return null;
        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
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

    public AlternativeRanker getRanker() {
        return this.ranker;
    }

    public void setRanker(AlternativeRanker ranker) {
        this.ranker = ranker;
    }

    public AlternativeStore getStore() {
        return this.store;
    }

    public void setStore(AlternativeStore store) {
        this.store = store;
    }

    public List<RankedAlternativeCollection> getAlternatives() {
        return this.alternatives;
    }

    public void setAlternatives(List<RankedAlternativeCollection> alternatives) {
        this.alternatives = alternatives;
    }
}
