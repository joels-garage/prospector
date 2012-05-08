/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.Date;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.SceneHistory;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.model.Scene;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;
import com.joelsgarage.util.XMLUtil;

/**
 * Tests of various scenes
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class SceneTest extends TestCase {
    private TestData testData;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.testData = new TestData();
    }

    protected String preamble() {
        return "<?xml version=\"1.0\" encoding=\"UTF-16\"?>"
            + "<input xmlns=\"http://www.joelsgarage.com/callcenter\">\n";
    }

    protected String postamble() {
        return "</input>\n";
    }

    protected String stakeholderOutput() {
        return "   <stakeholder>\n"//
            + "      <Stakeholder xmlns=\"http://www.joelsgarage.com/prospector\">\n"//
            + "         <decisionKey>\n"//
            + "            <key>8GJRbHsRN_Z48sle</key>\n"//
            + "            <type>decision</type>\n"//
            + "         </decisionKey>\n"//
            + "         <id>toxAEYmI6N2tyeFs</id>\n" //
            + "         <namespace>stakeholdernamespace</namespace>\n" //
            + "         <userKey>\n"//
            + "            <key>o6KKkHNPrkucippN</key>\n"//
            + "            <type>user</type>\n"//
            + "         </userKey>\n"//
            + "      </Stakeholder>\n"// /
            + "   </stakeholder>\n";

    }

    protected String decisionOutput() {
        return "   <decision>\n"//
            + "      <Decision xmlns=\"http://www.joelsgarage.com/prospector\">\n"//
            + "         <classKey>\n"//
            + "            <key>kDwrNyFfaRhzLlON</key>\n"//
            + "            <type>class</type>\n"//
            + "         </classKey>\n"//
            + "         <description>decisiondescription</description>\n"//
            + "         <id>8GJRbHsRN_Z48sle</id>\n" //
            + "         <namespace>decisionamespace</namespace>\n" //
            + "         <userKey>\n"// not persisted
            + "            <key>o6KKkHNPrkucippN</key>\n"// not persisted
            + "            <type>user</type>\n"// not persisted
            + "         </userKey>\n"// not persisted
            + "      </Decision>\n"//
            + "   </decision>\n";
    }

    /** Verify the constructed object payload. */
    public void testIndividualPreferenceInput() {
        GetIndividualPreference scene = new GetIndividualPreference(null);
        Document document =
            scene.makeGetIndividualPreferenceInputImpl(this.testData.decision,
                this.testData.stakeholder);
        String documentXML = XMLUtil.writeXML(document);
        assertEquals(preamble() + stakeholderOutput() + decisionOutput() + postamble(), documentXML);
    }

    /** Verify the constructed object payload. */
    public void testShowUrlInput() {
        ShowUrl scene = new ShowUrl();
        Document document = scene.getInputImpl(this.testData.decision);
        String documentXML = XMLUtil.writeXML(document);
        assertEquals(preamble() + decisionOutput() + postamble(), documentXML);
    }

    /**
     * We should have a low score if we've already said it
     * 
     * @throws FatalException
     *             if key population fails
     */
    public void testIntroNegative() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene(Scenes.INTRO, this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", "input", null, "namespace");
        history.add(scene0);
        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(1l)), "end", "input", null, "namespace");
        history.add(scene1);
        HistoryBasedScenePlayer player = new Intro();
        player.setHistory(history);
        NextScene nextScene = player.getNextScene();
        assertEquals(Double.NEGATIVE_INFINITY, nextScene.score.doubleValue(), 0.01);
        assertEquals(Scenes.INTRO, nextScene.name);
    }

    public void testIntroPositive() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene("bar", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", "input", null, "namespace");
        history.add(scene0);
        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(1l)), "end", "input", null, "namespace");
        history.add(scene1);
        HistoryBasedScenePlayer player = new Intro();
        player.setHistory(history);
        NextScene nextScene = player.getNextScene();
        assertEquals(Double.POSITIVE_INFINITY, nextScene.score.doubleValue(), 0.01);
        assertEquals(Scenes.INTRO, nextScene.name);
    }
}
