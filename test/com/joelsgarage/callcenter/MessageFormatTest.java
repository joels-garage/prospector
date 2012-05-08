package com.joelsgarage.callcenter;

import java.text.MessageFormat;

import junit.framework.TestCase;

/**
 * Fucking MessageFormat doesn't work the way I expect it to so now I spent hours troubleshooting
 * the fucking thing fuck.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class MessageFormatTest extends TestCase {
    public void testWorking() {
        String argument = "something";
        String pattern = "you know {0}?";
        String result = MessageFormat.format(pattern, argument);
        assertEquals("you know something?", result);
    }

    public void testAlsoWorking() {
        String argument = "class/a1/tree";
        String pattern = "you know {0}?";
        String result = MessageFormat.format(pattern, argument);
        assertEquals("you know class/a1/tree?", result);
    }

    public void testYetAlsoWorking() {
        String argument = "class/a1/tree";
        Object[] arguments = new Object[] { argument };
        String pattern = "you know {0}?";
        String result = MessageFormat.format(pattern, arguments);
        assertEquals("you know class/a1/tree?", result);
    }

}
