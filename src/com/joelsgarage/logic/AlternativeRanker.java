/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.logic;

import java.util.List;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.RankedAlternativeCollection;
import com.joelsgarage.util.FatalException;

/**
 * Takes a pile of data and sorts it. The idea is that this class would not do any data-access, it
 * would be supplied all the data it needs by the caller.
 * 
 * This makes one list. It would be better, I think, to come up with two or three lists,
 * corresponding to the application's natural "like it" "don't like it" and "dunno" classes.
 * 
 * @author joel
 * 
 */
public interface AlternativeRanker {
    public List<RankedAlternativeCollection> getRankedList(int pageSize, ExternalKey decisionKey)
        throws FatalException;

    /**
     * Produces a list of alternative collections, one for each decision.
     * 
     * TODO: provide access on a per-decision basis rather than all together.
     * 
     * @return
     * @throws FatalException
     *             if key population fails
     */
    public List<RankedAlternativeCollection> getRankedList(int pageSize) throws FatalException;
}
