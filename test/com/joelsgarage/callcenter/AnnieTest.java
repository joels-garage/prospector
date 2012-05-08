/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import gate.Annotation;
import gate.AnnotationSet;
import gate.creole.ANNIEConstants;
import gate.util.GateException;

import java.util.Arrays;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.callcenter.gateexamples.StandAloneAnnieDriver;
import com.joelsgarage.callcenter.gateexamples.Util;
import com.joelsgarage.util.InitializationException;

/**
 * Verify functionality of plain-vanilla GATE ANNIE.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class AnnieTest extends TestCase {
    StandAloneAnnieDriver standAloneAnnieDriver;

    public AnnieTest() {
        try {
            this.standAloneAnnieDriver = new StandAloneAnnieDriver();
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Verify that the string produces the specified annotations, offsets, and features. Just
     * verifies one feature per annotation.
     */
    @SuppressWarnings("unchecked")
    public void confirmAnnie(String input, String[] annotationNames, int annotationCount,
        int[] startOffsets, int[] endOffsets, String[] contents, String[] featureKeys,
        String[] featureValues) {
        try {
            this.standAloneAnnieDriver.run(input);
            String results = this.standAloneAnnieDriver.toXML();
            Logger.getLogger(AnnieTest.class).info("item: " + String.valueOf(results) + "\n");
            AnnotationSet annotationSet =
                this.standAloneAnnieDriver.getMultipleAnnotations(Arrays.asList(annotationNames));

            if (annotationCount > 0) {
                assertNotNull(annotationSet);
                assertEquals(annotationCount, annotationSet.size());

                Iterator annotationIter = annotationSet.iterator();
                int index = 0;
                while (annotationIter.hasNext()) {
                    Annotation currAnnot = (Annotation) annotationIter.next();

                    currAnnot.getStartNode().getOffset();

                    if (startOffsets != null && startOffsets.length > 0 && endOffsets != null
                        && endOffsets.length > 0 && contents != null && contents.length > 0) {
                        Long startOffset = currAnnot.getStartNode().getOffset();
                        Long endOffset = currAnnot.getEndNode().getOffset();
                        assertEquals(startOffsets[index], startOffset.intValue());

                        assertEquals(endOffsets[index], endOffset.intValue());
                        String annotatedContent =
                            this.standAloneAnnieDriver.getDocument().getContent().getContent(
                                startOffset, endOffset).toString();
                        assertEquals(contents[index], annotatedContent);
                    }

                    if (featureKeys != null && featureKeys.length > index
                        && featureKeys[index] != null) {
                        assertTrue(currAnnot.getFeatures().containsKey(featureKeys[index]));
                        if (featureValues != null && featureValues.length > index)
                            assertEquals(featureValues[index], currAnnot.getFeatures().get(
                                featureKeys[index]));
                    }

                    ++index;
                    if (startOffsets != null && index >= startOffsets.length) {
                        break;
                    }
                }
            }

        } catch (GateException e) {
            e.printStackTrace();
            fail();
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        }

    }

    /** Verify tokens */
    public void testAnnie1() {
        confirmAnnie("This sentence contains the word Goldfish once. \n"
            + "The Goldfish sentence contains Goldfish more than Goldfish once. Goldfish. \n "
            + "Goldfish Goldfish Goldfish. \n"
            + "This is really long sentence about nothing in particular, \n"
            + "but we will probably throw the word Goldfish in it... like that! \n"
            + "This sentence contains nothing interesting.\n",
            new String[] { ANNIEConstants.TOKEN_ANNOTATION_TYPE }, 56, // 56 tokens
            new int[] { 0 }, // starts
            new int[] { 4 }, // ends
            new String[] { "This" }, new String[] { "string" }, new String[] { "This" });
    }

    /**
     * Verify some tokens. This sentence contains 11 tokens. Note that the ordering of tokens is
     * weird; I guess that's why the StandAloneAnnie has that sorted container. Since I really only
     * care about finding a single annotation, for now, I'll delay figuring out the best way to
     * handle reordering.
     */
    public void testAnnie2() {
        confirmAnnie("Tom Smith and Mary Worth went to London on vacation.",
            // .......... 0123456789012345678901234567890123456789012345678901234567890123456789

            new String[] { ANNIEConstants.TOKEN_ANNOTATION_TYPE },
            11, // 11 tokens
            new int[] { 0, 40, 4 }, // starts
            new int[] { 3, 42, 9 }, // ends
            new String[] { "Tom", "on", "Smith" }, new String[] { "string", "string", "string" },
            new String[] { "Tom", "on", "Smith" });
    }

    /**
     * This sentence contains two locations. So the "Location" annotation has "locType" in the
     * feature map, but only for "city," not for "state." Also note peculiar ordering.
     */
    public void testAnnie3() {
        confirmAnnie("Santa Barbara is in California.",
            // .......... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOCATION_ANNOTATION_TYPE },
            2, // two locations
            new int[] { 20, 0 }, // starts
            new int[] { 30, 13 }, // ends
            new String[] { "California", "Santa Barbara" }, new String[] { null, "locType" },
            new String[] { null, "city" });
    }

    /** Because the default rules are UK rules, the address is not recognized, though the city is */
    public void testAnnie4() {
        confirmAnnie("Joel's house at 4097 Scripps Ave, Palo Alto, CA 94306 is a nice place",
        // .......... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOCATION_ANNOTATION_TYPE }, 1, // one location
            new int[] { 34 }, // starts
            new int[] { 43 }, // ends
            new String[] { "Palo Alto" }, new String[] { "locType" }, new String[] { "city" });
    }

    /** Email address is recognized */
    public void testAnnie5() {
        confirmAnnie("here's my email address: joel@truher.org -- I hope you like it.",
        // .......... 0123456789012345678901234567890123456789012345678901234567890123456789

            new String[] { Util.ADDRESS_ANNOTATION_TYPE }, 1, // one email address
            new int[] { 25 }, // starts
            new int[] { 40 }, // ends
            new String[] { "joel@truher.org" }, new String[] { "kind" }, new String[] { "email" });
    }

    /** Nothing bad happens if we give it a blank string */
    public void testAnnie6() {
        confirmAnnie("", new String[] { ANNIEConstants.TOKEN_ANNOTATION_TYPE }, 0, // no tokens
            null, null, null, null, null);
    }
}
