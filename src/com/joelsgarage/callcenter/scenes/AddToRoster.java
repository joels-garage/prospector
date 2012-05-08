/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;

/**
 * Scoring only.
 * 
 * @author joel
 */
public class AddToRoster extends HistoryBasedScenePlayer {
    /** Don't repeat this scene more often than once per day */
    // private static final long MIN_RECENCY = 86400000;
    // ten minutes for now
    private static final long MIN_RECENCY = 600000;

    @Override
    protected String sceneName() {
        return Scenes.ADD_TO_ROSTER;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    @Override
    public NextScene getNextScene() {
        return new NextScene(Scenes.ADD_TO_ROSTER, null, null);
    }
}
