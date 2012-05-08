/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.HashSet;
import java.util.Set;

/**
 * Names of the scene states in SCXML.
 * 
 * TODO: put this into ScenePlayer?
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public interface Scenes {
    // add_to_roster has no FSM component, it's "played" by the operator.
    public static final String ADD_TO_ROSTER = "add_to_roster";
    public static final String ASK_SCENE_CHOICE = "ask_scene_choice";
    public static final String ASSISTANT = "assistant";
    // no FSM, just plays the previous scene's "chain" output.
    public static final String CHAIN = "chain";
    public static final String DATABASE = "database";
    public static final String GET_DECISION = "get_decision";
    public static final String GET_DECISION_LITE = "get_decision_lite";
    public static final String GET_INDIVIDUAL_PREFERENCE = "get_individual_preference";

    public static final String GET_INDIVIDUAL_PROPERTY_PREFERENCE =
        "get_individual_property_preference";
    public static final String GET_STAKEHOLDER = "get_stakeholder";

    public static final String INTRO = "intro";
    public static final String JOKE = "joke";
    // originate has no FSM component, it's "played" by the operator.
    public static final String ORIGINATE = "originate";
    public static final String RATE_INDIVIDUAL = "rate_individual";
    public static final String RATE_INDIVIDUAL_PROPERTY = "rate_individual_property";

    public static final String SANDBOX = "sandbox";
    public static final String SHOW_ALTERNATIVES = "show_alternatives";
    public static final String SHOW_URL = "show_url";
    public static final String STAKEHOLDER_NEWS = "stakeholder_news";
    public static final String VERIFY_DECISION = "verify_decision";
    public static final String WATCH = "watch";

    public static Set<String> validScenes = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add(ADD_TO_ROSTER);
            add(ASK_SCENE_CHOICE);
            add(ASSISTANT);
            add(CHAIN);
            add(DATABASE);
            add(GET_DECISION);
            add(GET_INDIVIDUAL_PREFERENCE);

            add(GET_INDIVIDUAL_PROPERTY_PREFERENCE);
            add(GET_STAKEHOLDER);

            add(INTRO);
            add(JOKE);
            add(ORIGINATE);
            add(RATE_INDIVIDUAL);
            add(RATE_INDIVIDUAL_PROPERTY);

            add(SANDBOX);
            add(SHOW_ALTERNATIVES);
            add(SHOW_URL);
            add(STAKEHOLDER_NEWS);
            add(VERIFY_DECISION);
            add(WATCH);
        }
    };
}
