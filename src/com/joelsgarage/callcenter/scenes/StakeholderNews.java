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
 * Tell a user that they were just added as a stakeholder to a decision.
 * 
 * This is really just the simplest kind of two-user interaction I could think of.
 * 
 * TODO: Chain this with "do you want to work on decision x?"
 * 
 * @author joel
 */
public class StakeholderNews extends HistoryBasedScenePlayer {
    /** Field name for the constraint. TODO: pull this out somewhere */
    // private static final String USER_KEY = "userKey"; //$NON-NLS-1$
    /** The decision we're reporting on, used by input(). */
    private Decision stakeholderDecision = null;

    @Override
    protected String sceneName() {
        return Scenes.STAKEHOLDER_NEWS;
    }

    /**
     * Find stakeholder records for this user; pick the most recent one. If more recent than the
     * last time this scene was played, then play it. Don't play this scene if the stakeholder
     * record creator is ourselves.
     * 
     * Note, this isn't really the right logic, I think -- you want to detect any user interaction
     * with the decision. Maybe we missed the addition, and the user has already interacted with
     * this decision, so our "news" is old news.
     * 
     * Also it is possible to miss some updates since we only look at the most recent stakeholder
     * record, we make no attempt to "catch up" on news. For that, the website is a better resource.
     */
    @Override
    public NextScene getNextScene() {
        return null;

        // TODO: make this do something; i need to follow the association to the "log" entity to do
        // this right.

        // Scene mostRecentPlay = getMostRecentScene(sceneName());
        // // Remove this literal
        // Stakeholder stakeholder = Util.getByField(Stakeholder.class, USER_KEY,
        // getUser().getKey());
        // if (stakeholder == null) {
        // return null;
        // }
        // Decision theDecision = Util.getPrimary(Decision.class, stakeholder.getDecisionKey());
        // if (theDecision == null) {
        // Logger.getLogger(StakeholderNews.class).error(
        // "No matching decision for key: " + stakeholder.getDecisionKey()); //$NON-NLS-1$
        // return null;
        // }
        // setStakeholderDecision(theDecision);
        //
        // try {
        // boolean play = false;
        // if (mostRecentPlay == null) {
        // play = true;
        // } else if (DateUtil.diff(stakeholder.getLastModified(), mostRecentPlay
        // .getLastModified()) > 0) {
        // ExternalKey creatorKey = mostRecentPlay.getCreatorKey();
        // // If we created the stakeholder record ourselves, don't tell us about it.
        // if (creatorKey != null && !(creatorKey.equals(getUser().getKey()))) {
        // play = true;
        // }
        // }
        // if (play) {
        // // History says to play it.
        // return new NextScene(sceneName(), input(), Double.valueOf(Double.POSITIVE_INFINITY));
        // }
        // } catch (java.text.ParseException exception) {
        // Logger.getLogger(StakeholderNews.class).warn("caught parse exception"); //$NON-NLS-1$
        // // if we can't figure out the diff, then just bail; probably some error in writing the
        // // data means we don't know how old it is. probably old.
        // }
        // return null;
    }

    /** Input contains the decision and user mentioned in the stakeholder record. */
    @Override
    protected Document input() {
        Decision decision = getStakeholderDecision();
        if (decision == null)
            return null;

        Map<String, Node> inputs = new HashMap<String, Node>();
        inputs.put("decision", makeNode(decision)); //$NON-NLS-1$
        inputs.put("user", makeNode(getUser())); //$NON-NLS-1$
        Document resultDoc =
            XMLUtil.makeDocFromNodes(
                CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                HistoryBasedScenePlayer.INPUT, inputs);
        Logger.getLogger(GetStakeholder.class).info("input: " + XMLUtil.writeXML(resultDoc)); //$NON-NLS-1$
        return resultDoc;
    }

    public Decision getStakeholderDecision() {
        return this.stakeholderDecision;
    }

    public void setStakeholderDecision(Decision stakeholderDecision) {
        this.stakeholderDecision = stakeholderDecision;
    }
}
