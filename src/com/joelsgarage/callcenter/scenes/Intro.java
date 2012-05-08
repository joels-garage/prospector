/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;

/**
 * The Intro scene simply utters the intro spiel, only if it hasn't been said before.
 * 
 * @author joel
 * 
 */
public class Intro extends HistoryBasedScenePlayer {

    @Override
    protected String sceneName() {
        return Scenes.INTRO;
    }

    /**
     * We always select ourselves as the next scene.
     * 
     * TODO: a realistic score.
     */
    @Override
    public NextScene getNextScene() {
        return new NextScene(Scenes.INTRO, null, getScore());
    }

    /**
     * If we've never done the intro scene before, give it a high score. Ignores recency; this scene
     * only occurs once.
     * 
     * TODO: think about what scores to use.
     */
    protected Double getScore() {
        if (getMostRecentScene(Scenes.INTRO) == null)
            return Double.valueOf(Double.POSITIVE_INFINITY);
        return Double.valueOf(Double.NEGATIVE_INFINITY);
    }
}
