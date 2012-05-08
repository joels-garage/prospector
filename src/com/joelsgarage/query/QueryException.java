/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

/**
 * Represents an unprocessable query. For awhile I thought the right thing to do was to treat
 * garbage queries as specifications of the empty set, but I thought better of it -- best to tell
 * the client they screwed it up.
 * 
 * @author joel
 * 
 */
public class QueryException extends Exception {
    private static final long serialVersionUID = 1L;

    public QueryException(Exception e) {
        super(e);
    }

    public QueryException(String str) {
        super(str);
    }
}