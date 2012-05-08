/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.Scenes;

/**
 * Originate means "try to connect to a user on our roster". This class exists to score that action.
 * 
 * @author joel
 */
public class Originate extends HistoryBasedScenePlayer {
    /**
     * Don't repeat this scene more often than once every five minutes. Since origination isn't
     * necessarily user-visible, it's not that important to minimize.
     */
    private static final long MIN_RECENCY = 300000;

    @Override
    protected String sceneName() {
        return Scenes.ORIGINATE;
    }

    @Override
    protected long minRecency() {
        return MIN_RECENCY;
    }

    @Override
    public NextScene getNextScene() {
        return new NextScene(Scenes.ORIGINATE, null, null);
    }
}
