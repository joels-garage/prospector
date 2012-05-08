/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class TSVUtilTest extends TestCase {
    public void testOneFieldToTSV() {
        List<String> input = new ArrayList<String>();
        input.add("hi\t");
        String output = TSVUtil.toTSV(input);
        assertEquals("hi\\t", output);
    }

    public void testOneFieldFromTSV() {
        String input = "hi\\t";
        List<String> output = TSVUtil.fromTSV(input);
        assertEquals(1, output.size());
        assertEquals("hi\t", output.get(0));
    }

    public void testSimpleToTSV() {
        List<String> input = new ArrayList<String>();
        input.add("hi");
        input.add("there");
        String output = TSVUtil.toTSV(input);
        assertEquals("hi\tthere", output);
    }

    public void testSimpleFromTSV() {
        String input = "hi\tthere";
        List<String> output = TSVUtil.fromTSV(input);
        assertEquals(2, output.size());
        assertEquals("hi", output.get(0));
        assertEquals("there", output.get(1));
    }

    public void testToTSV() {
        List<String> input = new ArrayList<String>();
        input.add("hi\ttab");
        input.add("there\ttab\nnewline\n");
        String output = TSVUtil.toTSV(input);
        assertEquals("hi\\ttab\tthere\\ttab\\nnewline\\n", output);
    }

    public void testFromTSV() {
        String input = "hi\\ttab\tthere\\ttab\\nnewline\\n";
        List<String> output = TSVUtil.fromTSV(input);
        assertEquals(2, output.size());
        assertEquals("hi\ttab", output.get(0));
        assertEquals("there\ttab\nnewline\n", output.get(1));
    }

    public void testEscape() {
        assertEquals("hi\\tthere\\n", TSVUtil.escape("hi\tthere\n"));
    }

    public void testUnescape() {
        assertEquals("hi\tthere\n", TSVUtil.unescape("hi\\tthere\\n"));
    }
}
