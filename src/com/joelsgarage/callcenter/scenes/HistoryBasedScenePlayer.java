/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.joelsgarage.callcenter.CallCenterNamespaceContext;
import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.SceneHistory;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.callcenter.SendTargetBase;
import com.joelsgarage.callcenter.Util;
import com.joelsgarage.model.Decision;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Scene;
import com.joelsgarage.model.User;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.XMLUtil;

/**
 * A ScenePlayer whose score and input is based solely on the scene history for the current user.
 * 
 * It's also a SendTargetBase -- some scenes need their own SendTargets.
 * 
 * @author joel
 * 
 */
public abstract class HistoryBasedScenePlayer extends SendTargetBase implements ScenePlayer {
    /**
     * All recent scenes for the current user. Sorted in ascending chronological order, i.e. first()
     * is oldest, and last() is most recent.
     */
    private SceneHistory history;
    /** Who we're talking to. */
    private User user;
    /**
     * Container tag for scene input _eventdata
     * 
     * TODO: consider moving this elsewhere
     */
    public static final String INPUT = "input"; //$NON-NLS-1$
    /** A possible response event suffix */
    protected static final String FAIL = "Fail"; //$NON-NLS-1$
    /** A possible response event suffix */
    protected static final String DONE = "Done"; //$NON-NLS-1$
    /** A possible response event suffix */
    protected static final String SUCCESS = "Success"; //$NON-NLS-1$

    /** The name of this scene */
    protected abstract String sceneName();

    /** Produce the input document for this scene. Default input is no input */
    protected Document input() {
        return null;
    }

    /** Default next scene uses recency-based scoring only. */
    public NextScene getNextScene() {
        return new NextScene(sceneName(), input(), Double.valueOf(recencyBasedScore()));
    }

    /**
     * If this scene has run more recently than this many milliseconds, then the default scorer
     * tries not to run it again.
     */
    protected long minRecency() {
        // default is a minute.
        return 60000;
    }

    /**
     * Construct a score based on the recency of this scene.
     * 
     * Scoring is additive, pending any thinking about scoring.
     * 
     * TODO: think about proportional recency scoring, e.g. more recent == worse score.
     */

    protected double recencyBasedScore() {
        return recencyBasedScoreImpl(DateUtil.now());
    }

    /**
     * True if this scene is "novel," i.e. hasn't been recently played.
     * 
     * TODO: extract the threshold.
     */
    public boolean isNovel() {
        if (recencyBasedScore() >= 1.0)
            return true;
        return false;

    }

    protected double recencyBasedScoreImpl(Date now) {
        Scene lastShowing = getMostRecentScene(sceneName());
        if (lastShowing == null) {
            // if we've never showed this scene then it's OK to show it. :-)
            return 1.0;
        }

        try {
            long delta = DateUtil.diff(DateUtil.formatDateToISO8601(now), lastShowing.getStart());
            if (delta > minRecency()) {
                // score of 1 means ok to play, but not special
                return 1.0;
            }
        } catch (ParseException exception) {
            Logger.getLogger(HistoryBasedScenePlayer.class).warn(
                "swallowed parse error: " + exception.getMessage()); //$NON-NLS-1$
        }

        // score of -2 means really don't do it
        return -2.0;
    }

    /**
     * Make a w3c node in the specified namespace containing the serialized input.
     */
    protected static Node makeNode(Object input) {
        Document doc =
            XMLUtil.toXML(input, CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR);
        if (doc == null)
            return null;
        return doc.getDocumentElement();
    }

    /**
     * Return the most recent occurrence of the specified scene, or null if it doesn't occur at all.
     */
    protected Scene getMostRecentScene(String sceneName) {
        return getMostRecentSceneMulti(Collections.singleton(sceneName));
    }

    /**
     * Given a collection of scene names, find the most recent occurrence of one of them. Note that
     * a scene may be unfinished, i.e. have no end timestamp. We ignore these; maybe they were
     * system failures of some kind.
     */
    protected Scene getMostRecentSceneMulti(Collection<String> sceneNames) {
        if (sceneNames == null || getHistory() == null)
            return null;
        Iterator<Scene> iter = getHistory().descendingIterator();
        while (iter.hasNext()) {
            Scene aScene = iter.next();
            if (aScene == null)
                continue;
            if (aScene.getEnd() == null || aScene.getEnd().isEmpty())
                continue;
            String description = aScene.getDescription();
            if (description == null)
                continue;
            if (sceneNames.contains(description))
                return aScene;
        }
        return null;
    }

    // TODO: consider moving this to another class
    public Document getMostRecentSceneOutput(String sceneName) {
        Scene aScene = getMostRecentScene(sceneName);
        if (aScene == null)
            return null;
        String aSceneOutput = aScene.getOutput();
        // this could be null or blank
        Document outputDoc = XMLUtil.readXML(aSceneOutput);
        return outputDoc;
    }

    /**
     * The "current" decision is defined as the output of the most recent "get decision" scene.
     * 
     * TODO: consider a time limit, i.e. if the scene was too long ago, then there's no "current"
     * anymore.
     * 
     * TODO: remove the dependence on this particular GET_DECISION scene.
     */
    protected ExternalKey getCurrentDecisionKey() {
        Document outputDoc = getMostRecentSceneOutput(Scenes.GET_DECISION);
        Node keyNode =
            XMLUtil.getNodeByPath(namespaceContext, outputDoc, "cc:output/cc:key/p:ExternalKey"); //$NON-NLS-1$
        return XMLUtil.makeKeyFromNode(keyNode);
    }

    /**
     * Find the most-recently-discussed decision in the specified sceneList.
     */
    public Decision getCurrentDecision() {
        Logger.getLogger(HistoryBasedScenePlayer.class).info("getCurrentDecision()"); //$NON-NLS-1$
        ExternalKey decisionKey = getCurrentDecisionKey();
        if (decisionKey == null) {
            Logger.getLogger(GetIndividualPreference.class).info("No current decision"); //$NON-NLS-1$
            return null;
        }
        return Util.getPrimary(Decision.class, decisionKey);
    }

    //

    public SceneHistory getHistory() {
        return this.history;
    }

    public void setHistory(SceneHistory history) {
        this.history = history;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
