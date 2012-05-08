/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Takes an utterance id and produces an NLS utterance.
 * 
 * An utterance id is something sent us by the FSM.
 * 
 * TODO: validation of the parameter (e.g. avoid propagating naughty words?) TODO: use a
 * resourcebundle.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class Generator {
    private Properties properties = new Properties();

    // TODO: use a convention of prefixing for these properties.
    protected void loadProperties(String fileName) {
        URL url = Generator.class.getResource(fileName);
        if (url == null) {
            Logger.getLogger(Generator.class).info("Failed to find fileName: " + fileName); //$NON-NLS-1$
            return;
        }
        Logger.getLogger(Generator.class).info("Loading properties: " + fileName); //$NON-NLS-1$
        try {
            getProperties().load(url.openStream());
        } catch (IOException e) {
            Logger.getLogger(Generator.class).error(
                "Could not load joke.properties: " + e.getMessage()); //$NON-NLS-1$
        }
    }

    /**
     * Merge a few properties files to make a single map.
     * 
     * TODO: put this list in its own file somewhere, or maybe make some other class hold this list
     * (the SceneChooser?)
     */
    public Generator() {
        loadProperties("/main.properties");

        loadProperties("/scenes/ask_scene_choice.properties");
        loadProperties("/scenes/get_decision.properties");
        loadProperties("/scenes/get_decision_lite.properties");
        loadProperties("/scenes/get_individual_preference.properties");
        loadProperties("/scenes/get_individual_property_preference.properties");
        loadProperties("/scenes/get_stakeholder.properties");
        loadProperties("/scenes/intro.properties");
        loadProperties("/scenes/joke.properties");
        loadProperties("/scenes/rate_individual.properties");
        loadProperties("/scenes/rate_individual_property.properties");
        loadProperties("/scenes/show_alternatives.properties");
        loadProperties("/scenes/show_url.properties");
        loadProperties("/scenes/stakeholder_news.properties");
        loadProperties("/scenes/verify_decision.properties");

        loadProperties("/error_handler.properties");
        loadProperties("/assistant.properties");
        loadProperties("/database.properties");
        loadProperties("/watch.properties");
        loadProperties("/wumpus.properties");
        loadProperties("/sandbox.properties");

    }

    public String generate0(String messageId) {
        return fetchMessage(messageId);
    }

    // Finding the pattern used to be factored out into a "generateN" method but for some really
    // mysterious reason it got confused about its vararg, so I got rid of it. Yay, 150 minutes of
    // my life, gone.
    public String generate1(String messageId, String verbatim1) {
        Logger.getLogger(Generator.class).info(
            "generate1 for id: " + messageId + " verbatim1: " + verbatim1);
        String pattern = fetchMessage(messageId);
        if (pattern == null) {
            Logger.getLogger(Generator.class).error(
                "can't find message id " + String.valueOf(messageId));
            return null;
        }
        return new MessageFormat(pattern).format(new Object[] { verbatim1 }, new StringBuffer(),
            null).toString();
    }

    public String generate2(String messageId, String verbatim1, String verbatim2) {
        String pattern = fetchMessage(messageId);
        if (pattern == null) {
            return null;
        }
        return new MessageFormat(pattern).format(new Object[] { verbatim1, verbatim2 },
            new StringBuffer(), null).toString();
    }

    public String generate3(String messageId, String verbatim1, String verbatim2, String verbatim3) {
        String pattern = fetchMessage(messageId);
        if (pattern == null) {
            return null;
        }
        return new MessageFormat(pattern).format(new Object[] { verbatim1, verbatim2, verbatim3 },
            new StringBuffer(), null).toString();
    }

    /**
     * Fetch the specified message string from the properties, or return null if it can't be found.
     * 
     * @param messageId
     * @return
     */
    protected String fetchMessage(String messageId) {
        String result = null;

        if (messageId != null) {
            result = getProperties().getProperty(messageId);
            if (result == null) {
                Logger.getLogger(Generator.class).error("can't find messageid " + messageId); //$NON-NLS-1$
            }
        } else {
            Logger.getLogger(Generator.class).error("null messageid"); //$NON-NLS-1$
        }
        return result;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
