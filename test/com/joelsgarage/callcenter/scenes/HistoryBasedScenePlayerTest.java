/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter.scenes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.w3c.dom.Document;

import com.joelsgarage.callcenter.NextScene;
import com.joelsgarage.callcenter.SceneHistory;
import com.joelsgarage.callcenter.Scenes;
import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.Scene;
import com.joelsgarage.util.DateUtil;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.TestData;
import com.joelsgarage.util.XMLUtil;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class HistoryBasedScenePlayerTest extends TestCase {
    private TestData testData;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.testData = new TestData();
    }

    public static class FakeHistoryBasedScenePlayer extends HistoryBasedScenePlayer {
        @Override
        protected String sceneName() {
            return null;
        }

        @Override
        public NextScene getNextScene() {
            return null;
        }
    }

    public void testNoEnd() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene("foo-scene0", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), null, null, null, "ns");

        history.add(scene0);
        Scene scene1 =
            new Scene("foo-scene1", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(100000l)), null, null, null, "ns");

        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        assertNull(player.getMostRecentScene("foo"));
    }

    /** verify that a scene without an "end" is not chosen as the most recent scene */
    public void testOneNoEnd() throws FatalException {
        SceneHistory history = new SceneHistory();
        // "end" is required by getMostRecentScene()
        Scene scene0 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", null, null, "ns");
        history.add(scene0);

        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil.formatDateToISO8601(new Date(
                100000l)), null, null, null, "ns");
        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        Scene mostRecentScene = player.getMostRecentScene("foo");
        assertEquals(scene0.makeKey(), mostRecentScene.makeKey());
    }

    public void testSimple0() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", null, null, "ns");

        history.add(scene0);
        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil.formatDateToISO8601(new Date(
                100000l)), "end", null, null, "ns");

        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        Scene mostRecentScene = player.getMostRecentScene("foo");
        assertEquals(scene1.makeKey(), mostRecentScene.makeKey());
    }

    public void testSimple1() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene("bar", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", null, null, "ns");
        history.add(scene0);
        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil.formatDateToISO8601(new Date(
                100000l)), "end", null, null, "ns");
        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        Scene mostRecentScene = player.getMostRecentScene("bar");
        assertEquals(scene0, mostRecentScene);
    }

    public void testSimple1a() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene("bar", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", null, null, "ns");
        history.add(scene0);
        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil.formatDateToISO8601(new Date(
                100000l)), "end", null, null, "ns");
        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        List<String> sceneNames = new ArrayList<String>();
        sceneNames.add("bar");
        Scene mostRecentScene = player.getMostRecentSceneMulti(sceneNames);
        assertEquals(scene0, mostRecentScene);
        sceneNames.add("foo");
        mostRecentScene = player.getMostRecentSceneMulti(sceneNames);
        assertEquals(scene1, mostRecentScene);
    }

    public void testSimple2() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", null,
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                    + "<output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                    + "   <bar>baz</bar>\n" //
                    + "</output>\n", "ns");

        history.add(scene0);
        Scene scene1 =
            new Scene("foofoo", this.testData.user.makeKey(), DateUtil.formatDateToISO8601(new Date(
                100000l)), "end", null, null, "ns");
        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        Document doc = player.getMostRecentSceneOutput("foo");
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
            + "<output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
            + "   <bar>baz</bar>\n" //
            + "</output>\n", XMLUtil.writeXML(doc));
    }

    public void testSimple3() throws FatalException {
        SceneHistory history = new SceneHistory();
        Scene scene0 =
            new Scene(Scenes.GET_DECISION, this.testData.user.makeKey(), DateUtil
                .formatDateToISO8601(new Date(0l)), "end", null,
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                    + "<output xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                    + "   <key>\n"
                    + "      <ExternalKey xmlns=\"http://www.joelsgarage.com/prospector\">\n" //
                    + "         <key>EUQ0JiTYv8c03oxA</key>\n" //
                    + "         <type>decision</type>\n"
                    + "      </ExternalKey>\n" //
                    + "   </key>\n" //
                    + "</output>\n", "ns");

        history.add(scene0);
        Scene scene1 =
            new Scene("foo", this.testData.user.makeKey(), DateUtil.formatDateToISO8601(new Date(
                100000l)), "end", null, null, "ns");

        history.add(scene1);
        HistoryBasedScenePlayer player = new FakeHistoryBasedScenePlayer();
        player.setHistory(history);
        ExternalKey decisionKey = player.getCurrentDecisionKey();
        if (decisionKey == null) {
            fail();
            return;
        }
        assertEquals("decision/EUQ0JiTYv8c03oxA", decisionKey.toString());
    }
}
