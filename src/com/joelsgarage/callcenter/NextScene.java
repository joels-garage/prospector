/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.w3c.dom.Document;

/**
 * Container for the next scene's inputs and score for selection.
 * 
 * @author joel
 */
public class NextScene {
    /** scene name */
    public String name;
    /** input doc */
    public Document input;
    /** score, for scene selection, ranges from negative to positive infinity, zero is "dunno" */
    public Double score;

    public NextScene(String name, Document input, Double score) {
        this.name = name;
        this.input = input;
        this.score = score;
    }
}