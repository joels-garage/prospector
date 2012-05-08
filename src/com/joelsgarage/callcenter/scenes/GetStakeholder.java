/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.EventListener;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.callcenter.SendTarget;
import com.joelsgarage.model.Decision;
import com.joelsgarage.util.XMLUtil;

/**
 * Asks the user to add a stakeholder to the current decision. This will result in a StakeholderNews
 * notification to that user.
 * 
 * @author joel
 * 
 */
public class GetStakeholder extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once per minute */
    private static final long MIN_RECENCY = 60000;

    /** TODO: pull this up into SendTargetBase */
    private EventListener listener;

    public GetStakeholder(EventListener listener) {
        setListener(listener);
    }

    protected void sourceSuccessEvent(String base, Map<String, String> elements) {
        sourceResponseEvent(base, SUCCESS, elements);
    }

    protected void sourceFailEvent(String base, Map<String, String> elements) {
        sourceResponseEvent(base, FAIL, elements);

    }

    protected void sourceResponseEvent(String base, String suffix, Map<String, String> elements) {
        getListener().handleMessage(
            base + suffix,
            XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "Result", elements)); //$NON-NLS-1$
    }

    @Override
    protected String sceneName() {
        return Scenes.GET_STAKEHOLDER;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    /** return "Success" if it's a valid email address, "Fail" if not */
    @SendTarget.Registrant
    public void validateEmail(String data, Element element) {
        String email =
            XMLUtil.getNodeValueByPath(namespaceContext, element,
                "scxml:data/cc:emailAddress/text()"); //$NON-NLS-1$
        if (isValidEmail(email)) {
            sourceSuccessEvent(data, null);
        } else {
            sourceFailEvent(data, null);
        }
    }

    /**
     * Return true if the email string is a valid address. For now we always return true!
     * 
     * TODO: actually do some real validation here.
     * 
     * 
     * @param email
     * @return
     */
    protected boolean isValidEmail(String email) {
        return true;
    }

    /** Input contains the current decision. */
    @Override
    protected Document input() {
        Decision decision = getCurrentDecision();
        if (decision == null)
            return null;

        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
        Document resultDoc =
            XMLUtil.makeDocFromNodes(
                CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                HistoryBasedScenePlayer.INPUT, inputs);
        Logger.getLogger(GetStakeholder.class).info("input: " + XMLUtil.writeXML(resultDoc)); //$NON-NLS-1$
        return resultDoc;
    }

    /** Input is the current user, because we sometimes create a new stakeholder */
    protected Document makeGetDecisionInput() {
        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("user", makeNode(getUser())); //$NON-NLS-1$
        return XMLUtil.makeDocFromNodes(
            CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            HistoryBasedScenePlayer.INPUT, inputs);
    }

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }
}
