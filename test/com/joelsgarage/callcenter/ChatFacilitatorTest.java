/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.util.XMLUtil;

/**
 * @author joel
 * 
 */
@SuppressWarnings( { "unused", "nls" })
public class ChatFacilitatorTest extends TestCase {

    /**
     * Make a speak0, e.g.:
     * 
     * <pre>
     * &lt;data xmlns=&quot;http://www.w3.org/2005/07/scxml&quot; name=&quot;speak0&quot;&gt;
     *    &lt;messageId xmlns=&quot;http://www.joelsgarage.com/callcenter&quot;&gt;hello&lt;/messageId&gt;
     * &lt;/data&gt;
     * </pre>
     */
    protected Document makeDoc() {
        Logger.getLogger(ChatFacilitatorTest.class).info("makeDoc");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
            return null;
        }

        Document doc = db.newDocument();
        Element root =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_W3_ORG_2005_07_SCXML, "data");
        Element item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "messageId"); // Create element
        item.appendChild(doc.createTextNode("hello"));
        root.appendChild(item); // Attach element to Root element
        doc.appendChild(root); // Add Root to Document
        return doc;
    }

    /** Verify that incoming chat produces the expected event. */
    public void testAbort() {
        Logger.getLogger(ChatFacilitatorTest.class).info("testAbort");

        FakeEventListener fakeEventListener = new FakeEventListener();
        FakeChatAdapter fakeChatAdapter = new FakeChatAdapter();
        ChatFacilitator chatFacilitator = new ChatFacilitator(fakeEventListener, fakeChatAdapter);

        // Make a message we should recognize
        Message message = new Message();
        message.setSubject("Subject of the chat");
        message.setBody("abort");
        message.setProperty("favoriteColor", "red");

        chatFacilitator.processMessageInternal(message);

        assertEquals(1, fakeEventListener.getEvents().size());
        assertEquals(1, fakeEventListener.getPayloads().size());

        String event = fakeEventListener.getEvents().get(0);
        Node payloadNode = fakeEventListener.getPayloads().get(0);

        assertEquals("user_input", event);
        String payloadString = XMLUtil.writeXML(payloadNode);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
            + "<UserInput xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
            + "   <text>abort</text>\n" //
            + "   <event>abort</event>\n" //
            + "   <rule>CommandRule</rule>\n" //
            + "   <utterance>abort</utterance>\n" //
            + "</UserInput>\n", //
            payloadString);
    }

    /** Verify the chatFacilitator produces outgoing chat */
    public void testSpeak() {
        Logger.getLogger(ChatFacilitatorTest.class).info("testSpeak");
        FakeEventListener fakeEventListener = new FakeEventListener();
        FakeChatAdapter fakeChatAdapter = new FakeChatAdapter();
        ChatFacilitator chatFacilitator = new ChatFacilitator(fakeEventListener, fakeChatAdapter);

        Document doc = makeDoc();

        chatFacilitator.send("speak0", doc.getDocumentElement());

        // This produces no events
        assertEquals(0, fakeEventListener.getEvents().size());
        assertEquals(0, fakeEventListener.getPayloads().size());

        // But it does produce the chat output we expect.
        assertEquals(1, fakeChatAdapter.getSentMessages().size());
        assertEquals("Hello There!", fakeChatAdapter.getSentMessages().get(0));
    }
}
