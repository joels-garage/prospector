/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

/**
 * Represents an item, with annotations like preference score. Not the same notion as Annotation. At
 * the moment it's just Individuals but maybe this is a more general topic.
 * 
 * This is not a database entity, it's a synthetic result produced by the server.
 * 
 * @author joel
 * 
 */
public class AnnotatedAlternative {

    private Individual individual;
    private Double score;
    /**
     * Because Javascript can't format the score the way I want, we format it on the server. It's
     * not for end-user consumption anyway.
     */
    private String scoreString;

    /**
     * Displayed verbatim, for debugging. In the future this will be more complicated.
     */
    private String reason;

    public AnnotatedAlternative() {
        super();
        setIndividual(new Individual());
        setScore(new Double(0.0));
        setScoreString(new String());
    }

    public AnnotatedAlternative(Individual individual, Double score) {
        setIndividual(individual);
        setScore(score);
    }

    @Override
    @SuppressWarnings("nls")
    public String toString() {
        String result = new String();
        result //
        += "individual: " + String.valueOf(getIndividual()) + //
            " score: " + String.valueOf(getScore()) + //
            " scoreString: " + String.valueOf(getScoreString()) + //
            " reason: " + String.valueOf(getReason());
        return result;
    }

    public Individual getIndividual() {
        return this.individual;
    }

    public void setIndividual(final Individual individual) {
        this.individual = individual;
    }

    public Double getScore() {
        return this.score;
    }

    public void setScore(final Double score) {
        this.score = score;
    }

    public String getScoreString() {
        return this.scoreString;
    }

    public void setScoreString(String scoreString) {
        this.scoreString = scoreString;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
