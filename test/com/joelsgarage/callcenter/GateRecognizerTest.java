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
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.joelsgarage.callcenter.gateexamples.GateRecognizer;
import com.joelsgarage.callcenter.gateexamples.Util;
import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Verify that the Gate Recognizer correctly uses its gazetteer and jape resources.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class GateRecognizerTest extends TestCase {
    private static final String PREFERENCE = "Preference";
    private static final String ENTITY = "Entity";
    private GateRecognizer gateRecognizer;

    public GateRecognizerTest() {
        try {
            this.gateRecognizer = new GateRecognizer();
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
    public void confirm(String input, String[] annotationNames, int annotationCount,
        int[] startOffsets, int[] endOffsets, String[] contents, String[] featureKeys,
        String[] featureValues) {
        try {
            this.gateRecognizer.run(input);
            String results = this.gateRecognizer.toXML();
            Logger.getLogger(GateRecognizerTest.class).info(
                "item: " + String.valueOf(results) + "\n");
            AnnotationSet annotationSet =
                this.gateRecognizer.getMultipleAnnotations(Arrays.asList(annotationNames));

            if (annotationSet != null) {
                for (Annotation annotation : annotationSet) {
                    Logger.getLogger(GateRecognizerTest.class).info(
                        "annotation type: " + String.valueOf(annotation.getType()));
                    for (Map.Entry<Object, Object> entry : annotation.getFeatures().entrySet()) {
                        Logger.getLogger(GateRecognizerTest.class).info(
                            " == name: " + String.valueOf(entry.getKey()) //
                                + " value: " + String.valueOf(entry.getValue()));
                    }
                }

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
                                this.gateRecognizer.getDocument().getContent().getContent(
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
    public void testToken1() {
        confirm("This sentence contains the word Goldfish once. \n"
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
    public void testToken2() {
        confirm("Tom Smith and Mary Worth went to London on vacation.",
            // . 0123456789012345678901234567890123456789012345678901234567890123456789

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
    public void testLocation1() {
        confirm("Santa Barbara is in California.",
            // . 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOCATION_ANNOTATION_TYPE },
            2, // two locations
            new int[] { 20, 0 }, // starts
            new int[] { 30, 13 }, // ends
            new String[] { "California", "Santa Barbara" }, new String[] { null, "locType" },
            new String[] { null, "city" });
    }

    /** Because the default rules are UK rules, the address is not recognized, though the city is */
    public void testLocation2() {
        confirm("Joel's house at 4097 Scripps Ave, Palo Alto, CA 94306 is a nice place",
        // .......... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOCATION_ANNOTATION_TYPE }, 1, // one location
            new int[] { 34 }, // starts
            new int[] { 43 }, // ends
            new String[] { "Palo Alto" }, new String[] { "locType" }, new String[] { "city" });
    }

    /** Email address is recognized */
    public void testEmail() {
        confirm("here's my email address: joel@truher.org -- I hope you like it.",
        // ..... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { Util.ADDRESS_ANNOTATION_TYPE }, 1, // one email address
            new int[] { 25 }, // starts
            new int[] { 40 }, // ends
            new String[] { "joel@truher.org" }, new String[] { "kind" }, new String[] { "email" });
    }

    /** Nothing bad happens if we give it a blank string */
    public void testBlank() {
        confirm("", new String[] { ANNIEConstants.TOKEN_ANNOTATION_TYPE }, 0, // no tokens
            null, null, null, null, null);
    }

    /** Verify that our new Gazetteer is running. At the moment it knows about a few bodies of water. */
    public void testNewGazetteer() {
        confirm(
            "The Amazon is longer than the Thames but both flow into the Atlantic.",
            // 23456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOOKUP_ANNOTATION_TYPE }, // the raw gazetteer output
            7, // there are lots of lookups, some of which we don't care about
            new int[] { 60, 4, 4, 60, 30, 30, 4 }, // starts
            new int[] { 68, 10, 10, 68, 36, 36, 10 }, // ends
            new String[] { "Atlantic", "Amazon", "Amazon", "Atlantic", "Thames", "Thames", "Amazon" },
            new String[] {
                "majorType",
                "majorType",
                "majorType",
                "majorType",
                "majorType",
                "majorType",
                "majorType" }, new String[] {
                "sample_major",
                "organization",
                "location",
                "location",
                "location",
                "sample_major",
                "sample_major" });
    }

    /**
     * Verify that our custom gazeteer creates "Lookup" annotations with the correct features
     * attached. Note that the "Lookup" annotation also appears for the stop-word "I".
     * 
     * Includes both default lookups and our database-produced one for "Redwood."
     */
    public void testGazeteerFeatures() {
        confirm("I like Redwood trees.",
        // ..... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOOKUP_ANNOTATION_TYPE }, // raw gazeteer output
            5, // number of lookups
            new int[] { 0, 0, 2, 7, 7 }, // starts
            new int[] { 1, 1, 6, 14, 14 }, // ends
            new String[] { "I", "I", "like", "Redwood", "Redwood" }, new String[] {
                "majorType",
                "majorType",
                "majorType",
                "termType",
                "key" }, new String[] {
                "stop",
                "pronoun",
                "sentiment",
                "entity",
                "individual/a1/redwood" });
    }

    /** Verify that our database-derived features are annotated correctly. */
    public void testMyGazeteerFeatures() {
        Logger.getLogger(GateRecognizerTest.class).info("testMyGazeteerFeatures");
        confirm("I like Barack Obama.",
        // ..... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ANNIEConstants.LOOKUP_ANNOTATION_TYPE }, // raw gazeteer output
            5, // number of lookups
            new int[] { 0, 0, 2, 7, 14 }, // starts
            new int[] { 1, 1, 6, 19, 19 }, // ends
            new String[] { "I", "I", "like", "Barack Obama", "Obama" }, new String[] {
                "majorType",
                "majorType",
                "majorType",
                "key",
                "key" }, new String[] {
                "stop",
                "pronoun",
                "sentiment",
                "individual/p1/Obama",
                "individual/p1/Obama" });
    }

    /** Verify that our custom transducer creates the correct annotation */
    public void testTransducer() {
        confirm("I like Redwood trees.",
        // ..... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { ENTITY }, // output of the entity rule
            1, // one annotation
            new int[] { 7 }, // starts
            new int[] { 14 }, // ends
            new String[] { "Redwood" }, new String[] { "externalKey" }, new String[] { "fooKey" });
    }

    /** Verify that our custom transducer creates the correct annotation, "I like Redwood" */
    public void testPreference() {
        Logger.getLogger(GateRecognizerTest.class).info("testPreference");
        confirm(
            "I like Redwood trees.",
            // 23456789012345678901234567890123456789012345678901234567890123456789
            new String[] { PREFERENCE }, // output of the preference rule
            1, // one annotation
            new int[] { 0 }, // starts
            new int[] { 14 }, // ends
            new String[] { "I like Redwood" }, new String[] { "externalKey" },
            new String[] { "individual/a1/redwood" });

        confirm("I like Redwood trees.",
        // 23456789012345678901234567890123456789012345678901234567890123456789
            new String[] { PREFERENCE }, // output of the preference rule
            1, // one annotation
            new int[] { 0 }, // starts
            new int[] { 14 }, // ends
            new String[] { "I like Redwood" }, new String[] { "degree" }, new String[] { "0.7" });
    }

    /** Verify that our custom transducer creates the correct annotation */
    public void testNumbers() {
        confirm("3",
        // ..... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { "UserInput" }, 1, // one annotation
            new int[] { 0 }, // starts
            new int[] { 1 }, // ends
            new String[] { "3" }, new String[] { "event" }, new String[] { "number" });
    }

    /** Verify that our custom transducer creates the correct annotation */
    public void testNumbers2() {
        confirm("3.5",
        // ..... 0123456789012345678901234567890123456789012345678901234567890123456789
            new String[] { "UserInput" }, 1, // one annotation
            new int[] { 0 }, // starts
            new int[] { 3 }, // ends
            new String[] { "3.5" }, new String[] { "event" }, new String[] { "number" });
    }

    /**
     * Verify that our custom gazeteer creates "Lookup" annotations with the correct features
     * attached. Note that the "Lookup" annotation also appears for the stop-word "I".
     */
    public void testUserInput() {
        try {
            this.gateRecognizer.run("abort");
            org.w3c.dom.Document userInputDocument = this.gateRecognizer.getUserInput();
            String serializedUserInputDocument = XMLUtil.writeXML(userInputDocument);
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>"
                + "<UserInput xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "   <text>abort</text>\n" //
                + "   <event>abort</event>\n" //
                + "   <rule>CommandRule</rule>\n" //
                + "   <utterance>abort</utterance>\n" //
                + "</UserInput>\n", serializedUserInputDocument);
        } catch (GateException e) {
            e.printStackTrace();
            fail();
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        }

    }

    /**
     * Verify that unrecognized input is correctly tagged.
     */
    public void testUnrecognizedInput() {
        try {
            this.gateRecognizer.run("foo");
            org.w3c.dom.Document userInputDocument = this.gateRecognizer.getUserInput();
            String serializedUserInputDocument = XMLUtil.writeXML(userInputDocument);
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<UserInput xmlns=\"http://www.joelsgarage.com/callcenter\">\n"
                + "   <event>unrecognized_input</event>\n" //
                + "   <utterance>foo</utterance>\n" //
                + "</UserInput>\n", serializedUserInputDocument);
        } catch (GateException e) {
            e.printStackTrace();
            fail();
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        }

    }
}
