/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.scxml.ErrorReporter;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.EventDispatcher;
import org.apache.commons.scxml.SCXMLExecutor;
import org.apache.commons.scxml.TriggerEvent;
import org.apache.commons.scxml.env.SimpleErrorHandler;
import org.apache.commons.scxml.env.SimpleErrorReporter;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.io.SCXMLParser;
import org.apache.commons.scxml.model.CustomAction;
import org.apache.commons.scxml.model.ModelException;
import org.apache.commons.scxml.model.SCXML;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.joelsgarage.util.XMLUtil;

/**
 * Trying to find a bug in the "assign" operator.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class SCXMLTest extends TestCase {

    public static class MyDispatcher implements EventDispatcher {
        public List<String> paramKeys = new ArrayList<String>();
        public List<String> paramValues = new ArrayList<String>();

        @Override
        public void cancel(String sendId) {
            Logger.getLogger(SCXMLTest.class).info("cancel( sendId: " + sendId + ")");
        }

        @SuppressWarnings("unchecked")
        @Override
        public void send(String sendId, String target, String targetType, String event, Map params,
            Object hints, long delay, List externalNodes) {
            StringBuffer buf = new StringBuffer();
            buf.append("send ( sendId: ").append(sendId);
            buf.append(", target: ").append(target);
            buf.append(", targetType: ").append(targetType);
            buf.append(", event: ").append(event);
            buf.append(", params: ").append(String.valueOf(params));
            buf.append(", hints: ").append(String.valueOf(hints));
            buf.append(", delay: ").append(delay);
            buf.append(')');
            Logger.getLogger(SCXMLTest.class).info(buf.toString());
            logParams(params);
        }

        @SuppressWarnings("unchecked")
        protected void logParams(Map params) {
            if (params == null)
                return;
            Set<Map.Entry<String, Node>> entrySet = params.entrySet();
            for (Map.Entry<String, Node> entry : entrySet) {
                String name = entry.getKey();
                if (name != null) {
                    Node node = entry.getValue();
                    String nodeString = XMLUtil.writeXML(node);
                    Logger.getLogger(EngineManager.class).info("received 'send' key name: " + name); //$NON-NLS-1$
                    Logger.getLogger(EngineManager.class).info("payload follows:\n" + nodeString); //$NON-NLS-1$
                    this.paramKeys.add(name);
                    this.paramValues.add(nodeString);
                }
            }
        }
    }

    public void testSimple() {
        String configResource = "/callcenter/scxmltest.sc.xml";
        URL config = this.getClass().getResource(configResource);
        if (config == null) {
            Logger.getLogger(SCXMLTest.class).info("null url!"); //$NON-NLS-1$
            fail();
            return;
        }
        Logger.getLogger(SCXMLTest.class).info("url: " + config.getFile()); //$NON-NLS-1$

        List<CustomAction> customActions = null;
        ErrorHandler errHandler = new SimpleErrorHandler();
        try {
            SCXML stateMachine = SCXMLParser.parse(config, errHandler, customActions);

            Evaluator evaluator = new JexlEvaluator();
            ErrorReporter errorReporter = new SimpleErrorReporter();
            MyDispatcher eventDispatcher = new MyDispatcher();

            SCXMLExecutor engine = new SCXMLExecutor(evaluator, eventDispatcher, errorReporter);

            engine.setStateMachine(stateMachine);
            engine.setSuperStep(true);
            engine.setRootContext(new JexlContext());

            engine.addListener(stateMachine, new TransitionLogger());

            engine.go();

            {

                String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
                    "<field xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
                    "   <sub1>newfoo</sub1>\n" + //
                    "   <sub2>newbar</sub2>\n" + //
                    "</field>\n";

                String eventName = "foo_event";

                Node payload = XMLUtil.readXML(xmlString).getDocumentElement();
                payload.normalize();

                Logger.getLogger(SCXMLTest.class).info(
                    "firing payload: " + XMLUtil.writeXML(payload));

                TriggerEvent triggerEvent =
                    new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT, payload);
                AsyncTrigger asyncTrigger = new AsyncTrigger(engine, triggerEvent);
                Thread triggerThread = new Thread(asyncTrigger);
                triggerThread.start();
                triggerThread.join();
            }

            {

                String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
                    "<field xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
                    "   <sub1>newnewfoo</sub1>\n" + //
                    "   <sub2>newnewbar</sub2>\n" + //
                    "</field>\n";

                String eventName = "bar_event";

                Node payload = XMLUtil.readXML(xmlString).getDocumentElement();
                payload.normalize();
                Logger.getLogger(SCXMLTest.class).info(
                    "firing payload: " + XMLUtil.writeXML(payload));

                TriggerEvent triggerEvent =
                    new TriggerEvent(eventName, TriggerEvent.SIGNAL_EVENT, payload);
                AsyncTrigger asyncTrigger = new AsyncTrigger(engine, triggerEvent);
                Thread triggerThread = new Thread(asyncTrigger);
                triggerThread.start();
                triggerThread.join();
            }

            assertEquals(4, eventDispatcher.paramKeys.size());
            assertEquals("foo", eventDispatcher.paramKeys.get(0));
            assertEquals("foo", eventDispatcher.paramKeys.get(1));
            assertEquals("foo", eventDispatcher.paramKeys.get(2));
            assertEquals("foo", eventDispatcher.paramKeys.get(3));

            assertEquals(4, eventDispatcher.paramValues.size());
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
                "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"foo\">\n" + //
                "   <field xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
                "      <sub1>foofoo</sub1>\n" + //
                "      <sub2>foobar</sub2>\n" + //
                "   </field>\n" + //
                "</data>\n", eventDispatcher.paramValues.get(0));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
                "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"foo\">\n" + //
                "   <field xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
                "      <sub1>barfoo</sub1>\n" + //
                "      <sub2>barbar</sub2>\n" + //
                "   </field>\n" + //
                "</data>\n", eventDispatcher.paramValues.get(1));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
                "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"foo\">\n" + //
                "   <field xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
                "   <sub1>newfoo</sub1>\n" + //
                "   <sub2>newbar</sub2>\n" + //
                "</field>\n" + //
                "</data>\n", eventDispatcher.paramValues.get(2));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" + //
                "<data xmlns=\"http://www.w3.org/2005/07/scxml\" name=\"foo\">\n" + //
                "   <field xmlns=\"http://www.joelsgarage.com/callcenter\">\n" + //
                "   <sub1>newnewfoo</sub1>\n" + //
                "   <sub2>newnewbar</sub2>\n" + //
                "</field>\n" + //
                "</data>\n", eventDispatcher.paramValues.get(3));

            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ModelException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fail();
    }
}
