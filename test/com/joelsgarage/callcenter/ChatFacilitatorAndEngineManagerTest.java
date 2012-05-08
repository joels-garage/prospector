/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.Message;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Verify that an incoming string is correctly recognized by the ChatFacilitator, passed to the
 * EngineManager, where it causes a transition that results in a send, resulting in a chat string
 * being emitted by the ChatFacilitator.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ChatFacilitatorAndEngineManagerTest extends TestCase {
    public static final String TEST_MAIN_SC_XML = "/callcenter/test.main.sc.xml"; //$NON-NLS-1$

    /** Trivial bridge directs "send"s to the chat, and directs events from the chat to the engine */
    public static class FakeBridge implements EventListener, EngineManager.Listener {
        private EngineManager engineManager;
        private SendTarget chatFacilitator;
        private FakeEventListener fakeEventListener;
        private FakeEngineManagerListener fakeEngineManagerListener;

        public FakeBridge() {
            this.fakeEventListener = new FakeEventListener();
            this.fakeEngineManagerListener = new FakeEngineManagerListener();

        }

        public void setup(EngineManager engineManager, SendTarget chatFacilitator) {
            this.engineManager = engineManager;
            this.chatFacilitator = chatFacilitator;
        }

        /** Send to the chat. Performs no checking of any kind */
        public void handleSend(String target, String targetType, Map<String, Node> dataMap) {
            Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("handleSend");

            this.fakeEngineManagerListener.handleSend(target, targetType, dataMap);

            if (dataMap != null) {
                for (Map.Entry<String, Node> entry : dataMap.entrySet()) {
                    Node node = entry.getValue();
                    if (node instanceof Element) {
                        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info(
                            "got Element");
                        Element element = (Element) node;
                        String data = entry.getKey();

                        this.chatFacilitator.send(data, element);
                    } else {
                        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info(
                            "wrong type");
                    }
                }
            }
        }

        /** Send to the engine. */
        public void handleMessage(String eventName, Node utterance) {
            Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("handleMessage");
            // Record it.
            this.fakeEventListener.handleMessage(eventName, utterance);
            this.engineManager.fireEvent(eventName, utterance);
        }

        public FakeEventListener getFakeEventListener() {
            return this.fakeEventListener;
        }

        public FakeEngineManagerListener getEngineManagerListener() {
            return this.fakeEngineManagerListener;
        }
    }

    protected Document makeDoc(String event) {
        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("makeDoc");

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
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "UserInput"); // Create Root Element

        Element eventItem =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "event"); // Create element
        eventItem.appendChild(doc.createTextNode(event));
        root.appendChild(eventItem); // Attach element to Root element

        Element item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "utterance"); // Create element
        item.appendChild(doc.createTextNode("something here"));
        root.appendChild(item); // Attach element to Root element

        doc.appendChild(root); // Add Root to Document

        return doc;
    }

    protected Document makeSpeakDoc() {
        Logger.getLogger(ChatFacilitatorTest.class).info("makeDoc");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
            return null;
        }

        Document doc = db.newDocument();

        Element root =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_W3_ORG_2005_07_SCXML, "data");

        Element item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "messageId");
        item.appendChild(doc.createTextNode("hello"));
        root.appendChild(item); // Attach element to Root element
        doc.appendChild(root); // Add Root to Document

        return doc;
    }

    /**
     * Verify that the chat recognizes "hello," sending the correct event to the FSM, and
     */
    public void testChatHello() {
        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("testChatHello");

        FakeEventListener fakeEventListener = new FakeEventListener();
        FakeChatAdapter fakeChatAdapter = new FakeChatAdapter();
        ChatFacilitator chatFacilitator = new ChatFacilitator(fakeEventListener, fakeChatAdapter);

        // Make a message we should recognize
        Message message = new Message();
        message.setSubject("Subject of the chat");
        message.setBody("hello");
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
            + "   <text>hello</text>\n" //
            + "   <event>hello</event>\n" //
            + "   <rule>CommandRule</rule>\n" //
            + "   <utterance>hello</utterance>\n" //
            + "</UserInput>\n", //
            payloadString);
    }

    /** Verify the chatFacilitator produces the specified outgoing chat */
    public void testSpeak() {
        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("testSpeak");

        FakeEventListener fakeEventListener = new FakeEventListener();
        FakeChatAdapter fakeChatAdapter = new FakeChatAdapter();
        ChatFacilitator chatFacilitator = new ChatFacilitator(fakeEventListener, fakeChatAdapter);

        Document doc = makeSpeakDoc();

        chatFacilitator.send("speak0", doc.getDocumentElement());

        // This produces no events
        assertEquals(0, fakeEventListener.getEvents().size());
        assertEquals(0, fakeEventListener.getPayloads().size());

        // But it does produce the chat output we expect.
        assertEquals(1, fakeChatAdapter.getSentMessages().size());
        assertEquals("Hello There!", fakeChatAdapter.getSentMessages().get(0));
    }

    /**
     * Verify that when we provide the "HELLO" event, we get the "Speak0" send with a message id
     * corresponding to 'hello'.
     */
    public void testEngineHello() {
        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("testEngineHello");

        FakeEngineManagerListener engineManagerListener = new FakeEngineManagerListener();
        try {
            EngineManager engineManager =
                new EngineManager(engineManagerListener, TEST_MAIN_SC_XML);

            // EngineManager.Event event = EngineManager.Event.HELLO;
            Node utterance = makeDoc("hello");

            engineManager.fireEvent("user_input", utterance);
            Thread.sleep(1000); // give the async trigger a chance

            // now we should have some output (the reply, "hello").

            assertEquals(1, engineManagerListener.getTargets().size());
            assertNull(engineManagerListener.getTargets().get(0));

            assertEquals(1, engineManagerListener.getTargetTypes().size());
            assertEquals("x-java", engineManagerListener.getTargetTypes().get(0));

            assertEquals(1, engineManagerListener.getDatas().size());
            if (engineManagerListener.getDatas().get(0) != null) {
                Map<String, Node> data = engineManagerListener.getDatas().get(0);
                assertEquals(1, data.size());
                assertTrue(data.keySet().contains("speak0"));
                Node outputNode = data.values().iterator().next();
                assertEquals(
                    "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                        + "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"speak0\">\n" //
                        + "   <messageId xmlns=\"http://www.joelsgarage.com/callcenter\">hello</messageId>\n" //
                        + "</data>\n", //
                    XMLUtil.writeXML(outputNode));
            }
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }

    /** Verify that the combined chat and engine respond to "hello" with "hello". */
    public void testCombined() {
        Logger.getLogger(ChatFacilitatorAndEngineManagerTest.class).info("testCombined");

        try {
            FakeBridge fakeBridge = new FakeBridge();

            FakeChatAdapter fakeChatAdapter = new FakeChatAdapter();
            ChatFacilitator chatFacilitator = new ChatFacilitator(fakeBridge, fakeChatAdapter);
            EngineManager engineManager = new EngineManager(fakeBridge, TEST_MAIN_SC_XML);

            fakeBridge.setup(engineManager, chatFacilitator);

            // Make a message we should recognize
            Message message = new Message();
            message.setSubject("Subject of the chat");
            message.setBody("hello"); // should produce HELLO event.
            message.setProperty("favoriteColor", "red");

            chatFacilitator.processMessageInternal(message);
            Thread.sleep(1000); // give the async trigger a chance

            assertEquals(1, fakeBridge.getFakeEventListener().getEvents().size());
            assertEquals(1, fakeBridge.getFakeEventListener().getPayloads().size());

            String event = fakeBridge.getFakeEventListener().getEvents().get(0);
            Node payloadNode = fakeBridge.getFakeEventListener().getPayloads().get(0);

            assertEquals("user_input", event);
            String payloadString = XMLUtil.writeXML(payloadNode);
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<UserInput xmlns=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "   <text>hello</text>\n" //
                + "   <event>hello</event>\n" //
                + "   <rule>CommandRule</rule>\n" //
                + "   <utterance>hello</utterance>\n" //
                + "</UserInput>\n", //
                payloadString);

            // now we should have some output (the reply, "hello").

            assertEquals(1, fakeBridge.getEngineManagerListener().getTargets().size());
            assertNull(fakeBridge.getEngineManagerListener().getTargets().get(0));

            assertEquals(1, fakeBridge.getEngineManagerListener().getTargetTypes().size());
            assertEquals("x-java", fakeBridge.getEngineManagerListener().getTargetTypes().get(0));

            assertEquals(1, fakeBridge.getEngineManagerListener().getDatas().size());
            if (fakeBridge.getEngineManagerListener().getDatas().get(0) != null) {
                Map<String, Node> data = fakeBridge.getEngineManagerListener().getDatas().get(0);
                assertEquals(1, data.size());
                assertTrue(data.keySet().contains("speak0"));
                Node outputNode = data.values().iterator().next();
                assertEquals(
                    "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                        + "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"speak0\">\n" //
                        + "   <messageId xmlns=\"http://www.joelsgarage.com/callcenter\">hello</messageId>\n" //
                        + "</data>\n", //
                    XMLUtil.writeXML(outputNode));
            }
            assertEquals(1, fakeChatAdapter.getSentMessages().size());
            assertEquals("Hello There!", fakeChatAdapter.getSentMessages().get(0));
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }

    }

}
