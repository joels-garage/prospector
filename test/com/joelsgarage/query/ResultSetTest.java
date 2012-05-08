/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.query;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.joelsgarage.model.AnnotatedAlternative;
import com.joelsgarage.model.Individual;
import com.joelsgarage.util.FatalException;

/**
 * @author joel
 */
@SuppressWarnings("nls")
public class ResultSetTest extends TestCase {
    public void testSimple() throws FatalException {
        ResultSet r = new ResultSet(3);
        r.add(new AnnotatedAlternative(new Individual("a", "n"), Double.valueOf(0.0)));
        r.add(new AnnotatedAlternative(new Individual("b", "n"), Double.valueOf(0.1)));
        r.add(new AnnotatedAlternative(new Individual("c", "n"), Double.valueOf(0.2)));
        r.add(new AnnotatedAlternative(new Individual("d", "n"), Double.valueOf(0.3)));
        r.add(new AnnotatedAlternative(new Individual("e", "n"), Double.valueOf(0.4)));
        r.add(new AnnotatedAlternative(new Individual("f", "n"), Double.valueOf(0.5)));
        r.add(new AnnotatedAlternative(new Individual("g", "n"), Double.valueOf(0.6)));
        r.add(new AnnotatedAlternative(new Individual("h", "n"), Double.valueOf(0.7)));
        r.add(new AnnotatedAlternative(new Individual("i", "n"), Double.valueOf(0.8)));
        r.add(new AnnotatedAlternative(new Individual("j", "n"), Double.valueOf(0.9)));
        r.add(new AnnotatedAlternative(new Individual("k", "n"), Double.valueOf(1.0)));

        List<AnnotatedAlternative> top = new ArrayList<AnnotatedAlternative>();
        List<AnnotatedAlternative> bottom = new ArrayList<AnnotatedAlternative>();

        r.sortedTop(top);
        r.sortedBottom(bottom);

        assertEquals(3, top.size());
        assertEquals(3, bottom.size());

        assertEquals("k", top.get(0).getIndividual().getName());
        assertEquals("j", top.get(1).getIndividual().getName());
        assertEquals("i", top.get(2).getIndividual().getName());

        assertEquals("a", bottom.get(0).getIndividual().getName());
        assertEquals("b", bottom.get(1).getIndividual().getName());
        assertEquals("c", bottom.get(2).getIndividual().getName());
    }

}
