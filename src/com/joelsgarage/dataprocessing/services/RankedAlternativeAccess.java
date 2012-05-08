/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.joelsgarage.model.RankedAlternativeCollection;

/**
 * Fetches a list (decisions) of lists (individuals), ranked by consensus preference.
 * 
 * @author joel
 * 
 */
public interface RankedAlternativeAccess extends RemoteService {
    public List<RankedAlternativeCollection> get(int pageSize);
}