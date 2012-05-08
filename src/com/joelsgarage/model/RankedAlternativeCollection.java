/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.ArrayList;

/**
 * Represents the list, returned from the server, of alternatives for a decision.
 * 
 * This is not a database entity, it's a synthetic result produced by the server.
 * 
 * @author joel
 * 
 */
public class RankedAlternativeCollection {
    /**
     * We represent a single decision.
     */
    private Decision decision;
    /**
     * These are all the possible alternatives (i.e. members of the class), with annotations.
     */
    private ArrayList<AnnotatedAlternative> alternatives;

    public RankedAlternativeCollection() {
        super();
        setDecision(new Decision());
        setAlternatives(new ArrayList<AnnotatedAlternative>());
    }

    public Decision getDecision() {
        return this.decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public ArrayList<AnnotatedAlternative> getAlternatives() {
        return this.alternatives;
    }

    public void setAlternatives(ArrayList<AnnotatedAlternative> alternatives) {
        this.alternatives = alternatives;
    }
}
