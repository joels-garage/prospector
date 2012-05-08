/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import org.hibernate.Session;

import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.model.WordSense;

/**
 * Thanks Java Generics for this awesome vacuous class.
 * 
 * @author joel
 * 
 */
public class WordSenseHibernateRecordReader extends HibernateRecordReader<WordSense> {
    public WordSenseHibernateRecordReader(Session s, ReaderConstraint c) {
        super(s, c);
    }
}
