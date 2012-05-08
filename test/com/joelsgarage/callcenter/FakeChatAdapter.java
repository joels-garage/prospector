/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Vector;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;

/**
 * Permits collection of chat output in a test.
 * 
 * @author joel
 * 
 */
public class FakeChatAdapter implements ChatAdapter {
    private Vector<String> sentMessages;
    private Vector<MessageListener> messageListeners;

    public FakeChatAdapter() {
        setSentMessages(new Vector<String>());
        setMessageListeners(new Vector<MessageListener>());
    }

    /** I'm not sure this will actually be useful */
    @Override
    public void addMessageListener(MessageListener listener) {
        getMessageListeners().add(listener);
    }

    /** We just accumulate messages */
    @Override
    public void sendMessage(String text) {
        getSentMessages().add(text);
    }

    /** The fake chat is never the same as any other */
    @Override
    public boolean chatEquals(Chat chat) {
        return false;
    }

    //

    public Vector<String> getSentMessages() {
        return this.sentMessages;
    }

    public void setSentMessages(Vector<String> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public Vector<MessageListener> getMessageListeners() {
        return this.messageListeners;
    }

    public void setMessageListeners(Vector<MessageListener> messageListeners) {
        this.messageListeners = messageListeners;
    }
}
