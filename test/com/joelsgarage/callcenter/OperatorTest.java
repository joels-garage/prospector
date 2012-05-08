/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import junit.framework.TestCase;

import com.joelsgarage.model.User;
import com.joelsgarage.util.FatalException;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class OperatorTest extends TestCase {
    public void testSimple() {
        Operator operator = new Operator(null, null, null);
        assertNotNull(operator);
        operator.start();
    }

    public void testNewUser() throws FatalException {
        User user = Operator.newUser("foo");
        assertEquals("foo", user.getEmailAddress());
        assertEquals("user/_q87-TfwTHzNiUOG", user.makeKey().toString());
    }

}
