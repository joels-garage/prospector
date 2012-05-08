/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;

/**
 * Trivial adapter. Allows testing of chat.
 * 
 * @author joel
 * 
 */
public class SmackChatAdapter implements ChatAdapter {
    private Chat chat;

    public SmackChatAdapter(Chat chat) {
        setChat(chat);
    }

    @Override
    public void addMessageListener(MessageListener listener) {
        if (getChat() != null)
            getChat().addMessageListener(listener);
    }

    @Override
    public void sendMessage(String text) throws XMPPException {
        Logger.getLogger(SmackChatAdapter.class).info("sendMessage: " + String.valueOf(text)); //$NON-NLS-1$

        if (getChat() == null) {
            Logger.getLogger(SmackChatAdapter.class).error("null chat"); //$NON-NLS-1$
            return;
        }
        getChat().sendMessage(text);
    }

    @Override
    public boolean chatEquals(Chat other) {
        if (getChat() == null)
            return false;
        return getChat().equals(other);
    }

    protected Chat getChat() {
        return this.chat;
    }

    protected void setChat(Chat chat) {
        this.chat = chat;
    }

}
