/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import junit.framework.TestCase;

import com.joelsgarage.model.User;
import com.joelsgarage.util.FatalException;

/**
 * TODO: put some real tests in here.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class SceneChooserTest extends TestCase {
    public void testNothing() throws FatalException {
        SceneChooser chooser = new SceneChooser(null, new User("foo@foo.com", "ns"));
        assertNotNull(chooser);
        SceneHistory history = new SceneHistory();
        NextScene scene = chooser.getNextScene(history);
        assertNotNull(scene);
    }
}
