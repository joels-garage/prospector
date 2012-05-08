package com.joelsgarage.callcenter;

import java.util.Date;

import org.jivesoftware.smack.packet.Message;

/**
 * Tests the chat functionality.
 * 
 * TODO: finish this test
 * 
 * @author Gaston Dombiak
 */
@SuppressWarnings( { "nls", "boxing" })
public class ChatTest extends SmackTestCase {

    public ChatTest(String arg0) {
        super(arg0);
    }

    public void testProperties() {
        // try {
        // Chat newChat = getConnection(0).getChatManager().createChat(getFullJID(1), null);
        // PacketCollector collector =
        // getConnection(1).createPacketCollector(new ThreadFilter(newChat.getThreadID()));

        Message msg = new Message();

        msg.setSubject("Subject of the chat");
        msg.setBody("Body of the chat");
        msg.setProperty("favoriteColor", "red");
        msg.setProperty("age", 30);
        msg.setProperty("distance", 30f);
        msg.setProperty("weight", 30d);
        msg.setProperty("male", true);
        msg.setProperty("birthdate", new Date());
        // newChat.sendMessage(msg);

        // Message msg2 = (Message) collector.nextResult(2000);

        // assertNotNull("No message was received", msg2);
        // assertEquals("Subjects are different", msg.getSubject(), msg2.getSubject());
        // assertEquals("Bodies are different", msg.getBody(), msg2.getBody());
        // assertEquals("favoriteColors are different", msg.getProperty("favoriteColor"), msg2
        // .getProperty("favoriteColor"));
        // assertEquals("ages are different", msg.getProperty("age"), msg2.getProperty("age"));
        // assertEquals("distances are different", msg.getProperty("distance"), msg2
        // .getProperty("distance"));
        // assertEquals("weights are different", msg.getProperty("weight"), msg2
        // .getProperty("weight"));
        // assertEquals("males are different", msg.getProperty("male"),
        // msg2.getProperty("male"));
        // assertEquals("birthdates are different", msg.getProperty("birthdate"), msg2
        // .getProperty("birthdate"));
        // } catch (XMPPException e) {
        // e.printStackTrace();
        // fail(e.getMessage());
        // }
    }

    @Override
    protected int getMaxConnections() {
        return 2;
    }
}
