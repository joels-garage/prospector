package com.joelsgarage.callcenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.scxml.Builtin;
import org.apache.commons.scxml.Context;
import org.apache.commons.scxml.Evaluator;
import org.apache.commons.scxml.SCXMLExpressionException;
import org.apache.commons.scxml.env.jexl.JexlContext;
import org.apache.commons.scxml.env.jexl.JexlEvaluator;
import org.apache.commons.scxml.model.State;
import org.apache.commons.scxml.model.TransitionTarget;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is to figure out what the hell Data() does.
 * 
 * @author joel
 */
@SuppressWarnings("nls")
public class BuiltinTest extends TestCase {
    protected Document makeDoc() {
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
                "cc:person"); // Create Root Element

        Element item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "cc:name"); // Create element
        item.appendChild(doc.createTextNode("Tom"));
        root.appendChild(item); // Attach element to Root element

        item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "cc:name"); // Create element
        item.appendChild(doc.createTextNode("Dick"));
        root.appendChild(item); // Attach element to Root element

        item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "cc:name"); // Create element
        item.appendChild(doc.createTextNode("Harry"));
        root.appendChild(item); // Attach element to Root element

        item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "cc:age"); // Create another Element
        item.appendChild(doc.createTextNode("28"));
        root.appendChild(item); // Attach Element to previous element down tree

        item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "cc:height");
        item.appendChild(doc.createTextNode("1.80"));
        root.appendChild(item); // Attach another Element - grandaugther

        item =
            doc.createElementNS(CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER,
                "cc:foo");
        item.setAttribute("attrname", "attrvalue");
        root.appendChild(item); // Attach another Element - grandaugther

        doc.appendChild(root); // Add Root to Document

        return doc;
    }

    protected String writeXML(Node node) {
        // Get a factory (DOMImplementationLS) for creating a Load and Save object.
        org.w3c.dom.ls.DOMImplementationLS impl;

        try {
            impl =
                (org.w3c.dom.ls.DOMImplementationLS) org.w3c.dom.bootstrap.DOMImplementationRegistry
                    .newInstance().getDOMImplementation("LS");
        } catch (ClassCastException e) {
            e.printStackTrace();
            fail();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            fail();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            fail();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            fail();
            return null;
        }

        // Use the factory to create an object (LSSerializer) used to
        // write out or save the document.
        org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
        org.w3c.dom.DOMConfiguration config = writer.getDomConfig();
        config.setParameter("format-pretty-print", Boolean.TRUE);

        // Use the LSSerializer to write out or serialize the document to a String.
        String serializedXML = writer.writeToString(node);

        return serializedXML;
    }

    public BuiltinTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BuiltinTest.class);
    }

    public static void main(String args[]) {
        String[] testCaseName = { BuiltinTest.class.getName() };
        junit.textui.TestRunner.main(testCaseName);
    }

    public void testIsMemberEmptySet() {
        Set<TransitionTarget> set = new HashSet<TransitionTarget>();

        assertFalse(Builtin.isMember(set, "on"));
    }

    public void testIsMemberFalse() {
        TransitionTarget state = new State();
        state.setId("off");

        Set<TransitionTarget> set = new HashSet<TransitionTarget>();
        set.add(state);

        assertFalse(Builtin.isMember(set, "on"));
    }

    public void testIsMemberTrue() {
        TransitionTarget state = new State();
        state.setId("on");

        Set<TransitionTarget> set = new HashSet<TransitionTarget>();
        set.add(state);

        assertTrue(Builtin.isMember(set, "on"));
    }

    /**
     * Verify that I can construct a valid document. :-)
     */
    public void testData() {

        try {
            Logger.getLogger(BuiltinTest.class).info("testData");

            String verbatim = "foo input here";

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.newDocument();
            Element root =
                document.createElementNS(
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER, "cc:UserInput");

            Element item =
                document.createElementNS(
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER, "cc:utterance");
            item.appendChild(document.createTextNode(verbatim));
            root.appendChild(item);
            document.appendChild(root);

            String path = "cc:UserInput";

            Map<String, String> nsMap = new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    put(CallCenterNamespaceContext.CC,
                        CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER);
                }
            };
            Node node = Builtin.dataNode(nsMap, document, path);

            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<cc:UserInput xmlns:cc=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "   <cc:utterance>foo input here</cc:utterance>\n" //
                + "</cc:UserInput>\n", //
                String.valueOf(writeXML(document)));
            assertEquals("<?xml version=\"1.0\" encoding=\"UTF-16\"?>" //
                + "<cc:UserInput xmlns:cc=\"http://www.joelsgarage.com/callcenter\">\n" //
                + "   <cc:utterance>foo input here</cc:utterance>\n" //
                + "</cc:UserInput>\n", //
                String.valueOf(writeXML(node)));

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Verify JEXL evaluation
     */
    public void testJexl() {
        Logger.getLogger(BuiltinTest.class).info("testJexl");

        Context ctx = new JexlContext();
        Evaluator eval = new JexlEvaluator();
        assertNotNull(eval);
        try {
            assertTrue(((Boolean) eval.eval(ctx, "1+1 eq 2")).booleanValue());

        } catch (SCXMLExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Verify the function of Data().
     */
    public void testJexlXpath() {
        try {
            Logger.getLogger(BuiltinTest.class).info("testJexlXpath");

            Context ctx = new JexlContext();

            String verbatim = "foo input here";

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db;

            db = dbf.newDocumentBuilder();

            Document document = db.newDocument();
            Element root =
                document.createElementNS(
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER, "cc:UserInput"); // Create
            // Root
            // Element
            Element item =
                document.createElementNS(
                    CallCenterNamespaceContext.HTTP_WWW_JOELSGARAGE_COM_CALLCENTER, "cc:utterance");
            item.appendChild(document.createTextNode(verbatim));
            root.appendChild(item);
            document.appendChild(root);

            ctx.set("somedata", document);

            Evaluator eval = new JexlEvaluator();
            assertNotNull(eval);

            Node node = (Node) eval.eval(ctx, "somedata");

            Logger.getLogger(BuiltinTest.class).info(
                "input1: " + String.valueOf(writeXML(document)));
            Logger.getLogger(BuiltinTest.class).info("output1: " + String.valueOf(writeXML(node)));

            // This produces the node contents

            assertEquals("foo input here", (String) eval.eval(ctx,
                "Data(somedata, 'cc:UserInput/cc:utterance')"));

            // This produces nothing; the eval somehow doesn't return the matching node.
            assertEquals("", (String) eval.eval(ctx, "Data(somedata, 'cc:UserInput')"));

            assertTrue(((Boolean) eval.eval(ctx,
                "Data(somedata, 'cc:UserInput/cc:utterance') == 'foo input here'")).booleanValue());

        } catch (SCXMLExpressionException e) {
            e.printStackTrace();
            fail();
            return;
        } catch (ClassCastException e) {
            e.printStackTrace();
            fail();
            return;
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /** Verify simple operations on the Document */
    public void testDoc() {
        Logger.getLogger(BuiltinTest.class).info("testDoc");
        Context ctx = new JexlContext();
        Document doc = makeDoc();
        ctx.set("somedata", doc);
        Logger.getLogger(BuiltinTest.class).info("input doc: " + String.valueOf(writeXML(doc)));

        Evaluator eval = new JexlEvaluator();
        assertNotNull(eval);

        try {
            Node node = (Node) eval.eval(ctx, "somedata");
            Logger.getLogger(BuiltinTest.class).info(
                "eval output: " + String.valueOf(writeXML(node)));
        } catch (SCXMLExpressionException e) {
            e.printStackTrace();
            fail();
            return;
        }
    }

    public void testJexlData() {
        Logger.getLogger(BuiltinTest.class).info("testJexlData");
        Context ctx = new JexlContext();
        Document doc = makeDoc();
        ctx.set("somedata", doc);
        Evaluator eval = new JexlEvaluator();
        assertNotNull(eval);
        try {
            // The Data() function only finds the *FIRST* node of a multi-node match.
            assertEquals("Tom", (String) eval.eval(ctx, "Data(somedata, 'cc:person/cc:name')"));
            // But it can pick nodes in a list
            assertEquals("Dick", (String) eval.eval(ctx, "Data(somedata, 'cc:person/cc:name[2]')"));
            // And can pick by attribute
            assertEquals("attrvalue", (String) eval.eval(ctx,
                "Data(somedata, 'cc:person/cc:foo/@attrname')"));
            // Attribute string comparison succeed
            assertTrue(((Boolean) eval.eval(ctx,
                "Data(somedata, 'cc:person/cc:foo/@attrname') == 'attrvalue'")).booleanValue());
            // Attribute string comparison fail
            assertFalse(((Boolean) eval.eval(ctx,
                "Data(somedata, 'cc:person/cc:foo/@attrname') == 'nothing'")).booleanValue());
            // Text node string comparison succeed
            assertTrue(((Boolean) eval.eval(ctx, "Data(somedata, 'cc:person/cc:name[1]') == 'Tom'"))
                .booleanValue());
            // Text node string comparison succeed
            assertFalse(((Boolean) eval.eval(ctx,
                "Data(somedata, 'cc:person/cc:name[1]') == 'Dick'")).booleanValue());
            // Numeric values are converted to Double.
            assertEquals(28, ((Double) eval.eval(ctx, "Data(somedata, 'cc:person/cc:age')"))
                .doubleValue(), 0.1);
            // And can be compared
            assertTrue(((Boolean) eval.eval(ctx, "Data(somedata, 'cc:person/cc:height') == 1.8"))
                .booleanValue());
            // Again, false comparison
            assertFalse(((Boolean) eval.eval(ctx, "Data(somedata, 'cc:person/cc:height') == 2"))
                .booleanValue());
            // Not an empty tag
            assertFalse(((Boolean) eval.eval(ctx, "empty(Data(somedata, 'cc:person/cc:height'))"))
                .booleanValue());
            // This is an empty tag
            assertTrue(((Boolean) eval
                .eval(ctx, "empty(Data(somedata, 'cc:person/cc:missingtag'))")).booleanValue());

        } catch (SCXMLExpressionException e) {
            e.printStackTrace();
            fail();
            return;
        } catch (ClassCastException e) {
            e.printStackTrace();
            fail();
            return;
        }
    }

    public void testJexlCoersion() {
        Logger.getLogger(BuiltinTest.class).info("testJexlCoersion");
        Context ctx = new JexlContext();
        Document doc = makeDoc();
        ctx.set("somedata", doc);
        Evaluator eval = new JexlEvaluator();
        assertNotNull(eval);
        try {
            // This will throw SCXMLExpressionException
            assertFalse(((Boolean) eval.eval(ctx, "Data(somedata, 'cc:person/cc:height') == 'foo'"))
                .booleanValue());
        } catch (SCXMLExpressionException e) {
            // This is where we should end up
            return;
        } catch (ClassCastException e) {
            e.printStackTrace();
            fail();
            return;
        }
        // If we get here, something went wrong.
        fail();
    }
}