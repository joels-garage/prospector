/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.joelsgarage.util.InitializationException;
import com.joelsgarage.util.XMLUtil;

/**
 * Verify the engine produces the expected "send" responses to fired events.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class EngineManagerTest extends TestCase {
    public static final String TEST_MAIN_SC_XML = "/callcenter/test.main.sc.xml"; //$NON-NLS-1$

    protected Document makeDoc(String event) {
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
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "UserInput"); // Create
        // Root
        // Element
        Element eventItem =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "event");
        eventItem.appendChild(doc.createTextNode(event));
        root.appendChild(eventItem);

        Element item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "utterance"); // Create element
        item.appendChild(doc.createTextNode("something here"));
        root.appendChild(item); // Attach element to Root element

        doc.appendChild(root); // Add Root to Document

        return doc;
    }

    /**
     * Verify that when we provide the "HELLO" event, we get the "Speak0" "send" with a message id
     * corresponding to 'hello'.
     */
    public void testHello() {
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

    public void testProd() {
        FakeEngineManagerListener engineManagerListener = new FakeEngineManagerListener();
        try {
            // Don't specify the input file; should get the prod one.
            EngineManager engineManager = new EngineManager(engineManagerListener);
            assertNotNull(engineManager);
        } catch (InitializationException e) {
            e.printStackTrace();
            fail();
        }
    }
}
