/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import gate.util.GateException;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.joelsgarage.callcenter.gateexamples.GateRecognizer;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Takes a user chat and determines which command it corresponds to, if any.
 * 
 * @author joel
 */
public class Recognizer {
    /** The recognizer can't normalize the input, and so provides it verbatim in the payload */
    public static final String UNRECOGNIZED_INPUT = "unrecognized_input"; //$NON-NLS-1$

    private GateRecognizer gateRecognizer;

    public Recognizer() throws InitializationException {
        Logger.getLogger(Recognizer.class).info("ctor"); //$NON-NLS-1$
        this.gateRecognizer = new GateRecognizer();
        Logger.getLogger(Recognizer.class).info("ctor done"); //$NON-NLS-1$

    }

    /**
     * Return the event (including UNRECOGNIZED_INPUT) corresponding to the input string.
     * 
     * @param in
     *            what the user said
     * @return the user-input document corresponding thereto
     */
    public Document recognize(String verbatim) {
        // We'd like the Gate Recognizer to recognize it.
        if (verbatim.length() > 0) {
            try {
                this.gateRecognizer.run(verbatim);
                Document gateDocument = this.gateRecognizer.getUserInput();
                if (gateDocument != null) {
                    Logger.getLogger(Recognizer.class).info(
                        "Recognized: " + XMLUtil.writeXML(gateDocument)); //$NON-NLS-1$
                    return gateDocument;
                }
            } catch (GateException e) {
                e.printStackTrace();
            } catch (InitializationException e) {
                e.printStackTrace();
            }
        }
        Logger.getLogger(Recognizer.class).info("Gate Recognizer failed; unrecognized."); //$NON-NLS-1$

        // If the recognizer didn't recognize it, it's "unrecognized." :-)
        // So we make a response right here.
        // The response document is a bag of text elements.
        Map<String, String> elements = new HashMap<String, String>();
        // We always include the verbatim utterance, no matter the interpretation.
        elements.put(GateRecognizer.UTTERANCE, verbatim);
        elements.put(EngineManager.EVENT, UNRECOGNIZED_INPUT);
        return XMLUtil.makeDoc(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
            GateRecognizer.USER_INPUT, elements);
    }

    /**
     * Turns on the filter of preference recognition, ignoring individuals that are not members of
     * the class specified
     */
    public void setPreferenceFilter(ExternalKey classKey) {
        this.gateRecognizer.setPreferenceFilter(classKey);
    }

    /**
     * Turns on property name filtering, accepting only properties whose domain is the specified
     * class
     */
    public void setPropertyFilterClassKey(ExternalKey classKey) {
        this.gateRecognizer.setPropertyFilterClassKey(classKey);
    }
}
