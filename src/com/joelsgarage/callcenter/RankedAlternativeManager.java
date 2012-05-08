/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * Manages fetching RankedAlternativeCollections in CallCenter.
 * 
 * @author joel
 * 
 */
public class RankedAlternativeManager implements SendTarget {
    private static final String FETCH_RANKED_ALTERNATIVES = "FetchRankedAlternatives"; //$NON-NLS-1$

    public RankedAlternativeManager() {
        // foo
    }

    @Override
    public void send(String data, Element element) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("nls")
    @Override
    public void registerThyself(SendTargetRegistry registry) {
        Logger.getLogger(this.getClass()).info("foo");
        registry.registerSendTarget(FETCH_RANKED_ALTERNATIVES, this);
        Logger.getLogger(this.getClass()).info("foo");

    }
}
