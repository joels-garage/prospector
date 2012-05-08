/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import junit.framework.TestCase;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class SceneHistoryTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // public void testSimple0() {
    // SceneHistory history = new SceneHistory();
    // Scene scene0 = new Scene();
    // scene0.setStart(DateUtil.formatDateToISO8601(new Date(0l)));
    // scene0.setName("scene0");
    // Logger.getLogger(SceneHistoryTest.class).info("start0: " + scene0.getStart());
    // history.add(scene0);
    // Scene scene1 = new Scene();
    // scene1.setStart(DateUtil.formatDateToISO8601(new Date(100000l)));
    // scene1.setName("scene1");
    // Logger.getLogger(SceneHistoryTest.class).info("start1: " + scene1.getStart());
    // history.add(scene1);
    // assertEquals(scene1.getName(), history.last().getName());
    // }
    //
    // public void testSimple1() {
    // SceneHistory history = new SceneHistory();
    // Scene scene0 = new Scene();
    // scene0.setStart(DateUtil.formatDateToISO8601(new Date(100000l)));
    // history.add(scene0);
    // Scene scene1 = new Scene();
    // scene1.setStart(DateUtil.formatDateToISO8601(new Date(0l)));
    //        history.add(scene1);
    //        assertEquals(scene0, history.last());
    //    }
}
