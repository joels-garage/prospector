/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.Scene;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.XMLUtil;

/**
 * The VerifyDecision scene asks the user if they want to keep working on a specific decision.
 * 
 * This only happens if it's been awhile since we've worked with it.
 * 
 * @author joel
 * 
 */
public class VerifyDecision extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once per minute */
    private static final long MIN_RECENCY = 60000;
    /** If it's been more than 10 minutes since the last scene, we should play. */
    private static final long MAX_HIATUS = 600000;
    /** If we've played any scene more recently than MAX_HIATUS, we should play. */
    private static final Set<String> workingScenes = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add(Scenes.GET_DECISION);
            add(Scenes.GET_INDIVIDUAL_PREFERENCE);
            add(Scenes.RATE_INDIVIDUAL);
            add(Scenes.RATE_INDIVIDUAL_PROPERTY);
            add(Scenes.SHOW_ALTERNATIVES);
            add(Scenes.SHOW_URL);
            add(Scenes.VERIFY_DECISION);
        }
    };

    @Override
    protected String sceneName() {
        return Scenes.VERIFY_DECISION;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    /**
     * try to play this scene if it's been longer than max hiatus.
     */
    @Override
    public NextScene getNextScene() {
        // when did we last show a qualifying scene?
        Scene lastQualifiedPlay = getMostRecentSceneMulti(workingScenes);
        if (lastQualifiedPlay == null) {
            return new NextScene(Scenes.VERIFY_DECISION, getInputImpl(getCurrentDecision()), Double
                .valueOf(-5.0));
        }

        double score = recencyBasedScore();

        try {
            long hiatus = DateUtil.diff(DateUtil.nowString(), lastQualifiedPlay.getStart());
            if (hiatus > MAX_HIATUS) {
                // It's ok to show
                score += 5.0;
            } else {
                // We don't really want to show
                score += -5.0;
            }
        } catch (ParseException exception) {
            score += -5.0;
        }

        return new NextScene(Scenes.VERIFY_DECISION, getInputImpl(getCurrentDecision()), Double
            .valueOf(score));
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
                CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER, INPUT, inputs);
        Logger.getLogger(VerifyDecision.class).info("input: " + XMLUtil.writeXML(resultDoc)); //$NON-NLS-1$
        return resultDoc;
    }
}
