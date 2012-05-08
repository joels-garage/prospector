/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import gate.util.GateException;

import java.net.URISyntaxException;

import junit.framework.TestCase;

import com.joelsgarage.callcenter.gateexamples.TotalGoldfishCount;

/**
 * Tests the goldfish example, which I used to get familiar with GATE.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class GoldfishTest extends TestCase {
    TotalGoldfishCount totalGoldfishCount;

    public GoldfishTest() {
        try {
            this.totalGoldfishCount = new TotalGoldfishCount();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        } catch (GateException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify we can run the Goldfish thing */
    public void testGoldfish() {
        try {
            String input =
                "This sentence contains the word Goldfish once. \n"
                    + "The Goldfish sentence contains Goldfish more than Goldfish once. Goldfish. \n "
                    + "Goldfish Goldfish Goldfish. \n"
                    + "This is really long sentence about nothing in particular, \n"
                    + "but we will probably throw the word Goldfish in it... like that! \n"
                    + "This sentence contains nothing interesting.\n";
            // Constructor does everything.
            this.totalGoldfishCount.run(input);
            assertEquals(46, this.totalGoldfishCount.numberOfWords());
            assertEquals(9, this.totalGoldfishCount.numberOfGoldfish());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGoldfish2() {
        try {
            String input = "This sentence contains nothing interesting.";
            // Constructor does everything.
            this.totalGoldfishCount.run(input);
            assertEquals(5, this.totalGoldfishCount.numberOfWords());
            assertEquals(0, this.totalGoldfishCount.numberOfGoldfish());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testGoldfish3() {
        try {
            String input = "I like Goldfish.";
            // Constructor does everything.
            this.totalGoldfishCount.run(input);
            assertEquals(3, this.totalGoldfishCount.numberOfWords());
            assertEquals(1, this.totalGoldfishCount.numberOfGoldfish());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
