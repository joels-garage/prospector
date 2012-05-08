/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import com.joelsgarage.callcenter.NextScene;

/**
 * A ScenePlayer knows how to play an SCXML scene. It produces ranking and input.
 * 
 * @author joel
 * 
 */
public interface ScenePlayer {
    /**
     * This ScenePlayer's opinion of the correct next scene, specified as a NextScene object, which
     * contains the name, input Document, and score of the scene choice.
     * 
     * It is possible to return null, i.e. to specify no next scene at all.
     */
    public NextScene getNextScene();
}
