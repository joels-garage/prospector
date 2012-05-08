/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

/**
 * Trivial adapter interface. Allows testing of chat.
 * 
 * @author joel
 * 
 */
public interface ChatAdapter {
    /** Add the specified listener for incoming messages */
    void addMessageListener(MessageListener listener);

    /** Send the specified text to the chat */
    void sendMessage(String text) throws XMPPException;

    /** True if the specified chat is the same as the underlying one (if present) */
    boolean chatEquals(Chat chat);
}
