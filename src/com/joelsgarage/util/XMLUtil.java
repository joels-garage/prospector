/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.ModelEntityFactory;

/**
 * Serialize a w3c node.
 * 
 * @author joel
 * 
 */
public class XMLUtil {
    /**
     * Serialize a w3c node.
     * 
     * @param node
     * @return pretty string representation
     */
    public static String writeXML(Node node) {
        if (node == null)
            return null;

        try {
            // Get a factory (DOMImplementationLS) for creating a Load and Save object.
            org.w3c.dom.ls.DOMImplementationLS impl =
                (org.w3c.dom.ls.DOMImplementationLS) org.w3c.dom.bootstrap.DOMImplementationRegistry
                    .newInstance().getDOMImplementation("LS"); //$NON-NLS-1$

            // Use the factory to create an object (LSSerializer) used to
            // write out or save the document.
            org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
            org.w3c.dom.DOMConfiguration config = writer.getDomConfig();
            config.setParameter("format-pretty-print", Boolean.TRUE); //$NON-NLS-1$

            // Use the LSSerializer to write out or serialize the document to a String.
            String serializedXML = writer.writeToString(node);

            return serializedXML;

        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deserialize an XML string.
     * 
     * @param xmlString
     *            XML UTF-16 data in (prolog not required)
     * @return parsed version
     */
    public static Document readXML(String xmlString) {
        if (xmlString == null || xmlString.isEmpty())
            return null;

        try {
            DOMImplementationLS impl =
                (org.w3c.dom.ls.DOMImplementationLS) org.w3c.dom.bootstrap.DOMImplementationRegistry
                    .newInstance().getDOMImplementation("LS"); //$NON-NLS-1$

            LSInput lsInput = impl.createLSInput();
            lsInput.setStringData(xmlString);

            LSParser lsParser = impl.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);

            Document document = lsParser.parse(lsInput);
            return document;

        } catch (ClassCastException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Logger.getLogger(XMLUtil.class).error("xml deserialization failed: " + xmlString); //$NON-NLS-1$
        return null;
    }

    /** A common operation is deserializing an ExternalKey from a Node */
    public static ExternalKey makeKeyFromNode(Node node) {
        if (node == null)
            return null;
        if (!(node instanceof Element))
            return null;
        Element element = (Element) node;
        Object object = XMLUtil.fromXML(element);
        if (!(object instanceof ExternalKey))
            return null;
        ExternalKey key = (ExternalKey) object;
        return key;
    }

    /**
     * Creates a w3c document based on the contents of the map. For each map entry, we construct a
     * subcontainer node in the root namespace, and then we do one of three things, based on the
     * value type:
     * <dl>
     * <dt>Node
     * <dd>import and append the node to the subcontainer
     * <dt>String
     * <dd>create a text node and append it to the subcontainer
     * <dt>any other type
     * <dd>serialize the object with toXML(), in the objectNamespace, and append it
     * </dl>
     * 
     * @param rootNamespace
     *            namespace for the doc wrapper
     * @param rootName
     *            name of the container element
     * @param objectNamespace
     *            namespace for serialized objects
     * @param elements
     *            map of container element and contents
     * @return
     */
    public static Document makeDocFromAnything(String rootNamespace, String rootName,
        String objectNamespace, Map<String, ?> elements) {
        Logger.getLogger(XMLUtil.class).info("makeDocFromAnything"); //$NON-NLS-1$
        if (rootName == null || !(rootName.length() > 0)) {
            Logger.getLogger(XMLUtil.class).info("null or blank rootName"); //$NON-NLS-1$
            return null;
        }
        if (elements == null || !(elements.size() > 0)) {
            Logger.getLogger(XMLUtil.class).info("null or empty elements"); //$NON-NLS-1$
            return null;
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Logger.getLogger(XMLUtil.class).error("failed newDocumentBuilder()"); //$NON-NLS-1$
            return null;
        }

        Document doc = db.newDocument();

        Element root = doc.createElementNS(rootNamespace, rootName); // Create Root Element

        for (Map.Entry<String, ?> entry : elements.entrySet()) {
            String key = entry.getKey(); // subcontainer name
            Object value = entry.getValue(); // subcontents
            if (value == null || key == null)
                continue;

            Element item = doc.createElementNS(rootNamespace, key);
            Node newNode = null;
            if (value instanceof String) {
                newNode = doc.createTextNode((String) value);
            } else if (value instanceof Node) {
                // Note we need to copy this node to keep w3c from freaking out.
                newNode = doc.importNode((Node) value, true);
            } else {
                Node node = toXML(value, objectNamespace).getDocumentElement();
                if (node != null) {
                    newNode = doc.importNode(node, true);
                }
            }
            if (newNode != null) {
                item.appendChild(newNode);
                root.appendChild(item);
            }
        }

        doc.appendChild(root); // Add Root to Document
        Logger.getLogger(XMLUtil.class).info("makeDoc result: " + writeXML(doc)); //$NON-NLS-1$
        return doc;
    }

    /**
     * Creates a w3c Document based on the contents of the specified map
     * 
     * @param rootNamespace
     *            the namespace for the contents of the doc, including the container and the
     *            contained elements
     * @param rootName
     *            name of the root (container) XML element
     * @param elements
     *            child elements, name/value map
     * @return
     * 
     * TODO: remove this method
     */
    public static Document makeDoc(String rootNamespace, String rootName,
        Map<String, String> elements) {
        return makeDocFromAnything(rootNamespace, rootName, null, elements);
    }

    /**
     * as above but use a map of nodes rather than text.
     * 
     * TODO: remove this method
     */
    public static Document makeDocFromNodes(String rootNamespace, String rootName,
        Map<String, Node> elements) {
        return makeDocFromAnything(rootNamespace, rootName, null, elements);
    }

    /**
     * Select the first xpath-matching node within the specified element, or null if no matching
     * elements.
     * 
     * TODO: test this.
     * 
     * @param nsContext
     *            provides namespace context (i.e. prefix map) for the expression (the context node
     *            need not use the same prefixes)
     * @param node
     *            the context node for the evaluation
     * @param expression
     *            the xpath expression
     * @return
     */
    public static Node getNodeByPath(NamespaceContext nsContext, Node node, String expression) {
        if (node == null || expression == null)
            return null;

        Logger.getLogger(XMLUtil.class).info("node namespace: " + node.getNamespaceURI()); //$NON-NLS-1$

        Logger.getLogger(XMLUtil.class).info("getNodeByPath for node " + writeXML(node) //$NON-NLS-1$
            + " xpath expression: " + expression); //$NON-NLS-1$

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();

        xpath.setNamespaceContext(nsContext);
        try {
            // See below, roundtripping through XML serialization for no reason I can understand.
            // If I don't do this, the XPATH doesn't work at all (!).

            XPathExpression expr = xpath.compile(expression);
            Object result = expr.evaluate(readXML(writeXML(node)), XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            Logger.getLogger(XMLUtil.class).info(
                "number of matching nodes: " + String.valueOf(nodes.getLength())); //$NON-NLS-1$
            if (nodes.getLength() > 0) {
                Logger.getLogger(XMLUtil.class).info("got node: " + writeXML(nodes.item(0))); //$NON-NLS-1$
                return nodes.item(0);
            }
            Logger.getLogger(XMLUtil.class).info("no nodes"); //$NON-NLS-1$
            return null;

        } catch (XPathExpressionException e) {
            e.printStackTrace();
            Logger.getLogger(XMLUtil.class).error("xpath error: " + e.getMessage()); //$NON-NLS-1$
            return null;
        }
    }

    /** Returns the nodeValue() of the node found as above */
    public static String getNodeValueByPath(NamespaceContext nsContext, Node node, String expression) {
        Node aNode = getNodeByPath(nsContext, node, expression);
        if (aNode == null)
            return null;
        return aNode.getNodeValue();
    }

    /** Wraps toXMLElement() with a document */
    public static Document toXML(Object object, String nsURI) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        Document doc = db.newDocument();

        Element root = toXMLElement(doc, object, nsURI, null);

        if (root == null)
            return null;

        doc.appendChild(root);

        return doc;
    }

    /**
     * Just like Translator.toXML() but uses w3c instead of dom4j. The resulting XML is within the
     * specified namespace, without a prefix.
     */
    public static Element toXMLElement(Document doc, Object object, String nsURI,
        String containerName) {
        // Logger.getLogger(XMLUtil.class).info("toXMLElement"); //$NON-NLS-1$

        String elementName = null;

        if (object != null) {
            String shortTypeName = ClassUtil.shortClassName(object.getClass());
            if (shortTypeName == null) {
                Logger.getLogger(XMLUtil.class).error("failed to get class name"); //$NON-NLS-1$
                return null;
            }
            elementName = shortTypeName;
        }

        if (containerName != null) {
            elementName = containerName;
        }

        if (elementName == null) {
            Logger.getLogger(XMLUtil.class).error("no element name"); //$NON-NLS-1$
            return null;
        }

        Element element = doc.createElementNS(nsURI, elementName);

        if (object == null)
            return element;

        try {
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);

            if (propertyDescriptors.length == 0) {
                Logger.getLogger(XMLUtil.class).info("no properties"); //$NON-NLS-1$
                element.setTextContent(object.toString());
                return element;
            }
            // Logger.getLogger(XMLUtil.class).info(
            // "num properties: " + String.valueOf(propertyDescriptors.length)); //$NON-NLS-1$

            for (int i = 0; i < propertyDescriptors.length; ++i) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String name = descriptor.getName(); // e.g. creatorKey
                Class<?> clazz = descriptor.getPropertyType();

                if (skip(clazz)) {
                    Logger.getLogger(XMLUtil.class).debug("skipping: " + clazz.getName()); //$NON-NLS-1$
                    continue;
                }

                if (primitive(clazz)) {
                    Object property = PropertyUtils.getProperty(object, name);
                    if (property == null) {
                        Logger.getLogger(XMLUtil.class).info("no value for property: " + name); //$NON-NLS-1$
                        continue;
                    }
                    // Logger.getLogger(XMLUtil.class).info("got primitive property name: " + name
                    // //$NON-NLS-1$
                    // + " value: " + property.toString()); //$NON-NLS-1$

                    Element propertyElement = doc.createElementNS(nsURI, name);
                    propertyElement.setTextContent(property.toString());
                    element.appendChild(propertyElement);
                    continue;
                } else if (clazz == byte[].class) {
                    // TODO: clean up this cut n paste
                    Logger.getLogger(XMLUtil.class).debug("got byte[] property name: " + name); //$NON-NLS-1$
                    Object property = PropertyUtils.getProperty(object, name);
                    if (property == null) {
                        Logger.getLogger(XMLUtil.class).info("no value for property: " + name); //$NON-NLS-1$
                        continue;
                    }
                    Logger.getLogger(XMLUtil.class).info(
                        "property value: " + new String((byte[]) property)); //$NON-NLS-1$
                    String encoded = KeyUtil.encode((byte[]) property);
                    Logger.getLogger(XMLUtil.class).info("encoded value: " + encoded); //$NON-NLS-1$

                    Element propertyElement = doc.createElementNS(nsURI, name);
                    propertyElement.setTextContent(KeyUtil.encode((byte[]) property));
                    element.appendChild(propertyElement);
                    continue;
                }

                Object propertyObject = PropertyUtils.getProperty(object, name);
                Element propertyElement = toXMLElement(doc, propertyObject, nsURI, name);
                // Logger.getLogger(XMLUtil.class).info("got object property name: " + name
                // //$NON-NLS-1$
                // + " value: " + writeXML(propertyElement)); //$NON-NLS-1$
                element.appendChild(propertyElement);
            }
            return element;
        } catch (IllegalAccessException e) {
            Logger.getLogger(XMLUtil.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
            return element;
        } catch (InvocationTargetException e) {
            Logger.getLogger(XMLUtil.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
            return element;
        } catch (NoSuchMethodException e) {
            Logger.getLogger(XMLUtil.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
            return element;
        }
    }

    /**
     * Instantiate an object reflecting the specified node, which must be an element.
     * 
     * @param node
     *            the node representing the object; the outer container contains the class name.
     * @return the object represented by the node, or null if something went awry.
     */
    public static Object fromXMLNode(Node node) {
        if (node instanceof Element)
            return fromXML((Element) node);
        return null;
    }

    /** Convert the serialized form of ExternalKey to the object */
    public static class ExternalKeyConverter implements Converter {
        @SuppressWarnings("unchecked")
        @Override
        public Object convert(Class type, Object value) {
            Logger.getLogger(XMLUtil.class).info("converting"); //$NON-NLS-1$
            if (type != ExternalKey.class)
                throw new ConversionException("Bad type specified: " + type.getName()); //$NON-NLS-1$
            if (value == null)
                return null;
            Logger.getLogger(XMLUtil.class).info("converting: " + value.toString()); //$NON-NLS-1$
            if (value instanceof ExternalKey)
                return value;
            if (value instanceof String)
                return new ExternalKey((String) value);
            throw new ConversionException("Bad value type: " + value.getClass().getName()); //$NON-NLS-1$
        }
    }

    // /** Convert the serialized byte key (in base64) into the binary one */
    // public static class ByteConverter implements Converter {
    // @SuppressWarnings("unchecked")
    // @Override
    // public Object convert(Class type, Object value) {
    // Logger.getLogger(XMLUtil.class).info("converting"); //$NON-NLS-1$
    // if (type != byte[].class)
    // throw new ConversionException("Bad type specified: " + type.getName()); //$NON-NLS-1$
    // if (value == null)
    // return null;
    // Logger.getLogger(XMLUtil.class).info("converting: " + value.toString()); //$NON-NLS-1$
    // if (value instanceof byte[])
    // return value;
    // if (value instanceof String)
    // return NameUtil.decodeKeyToBytes((String) value);
    // throw new ConversionException("Bad value type: " + value.getClass().getName()); //$NON-NLS-1$
    // }
    // }

    /**
     * Instantiate an object reflecting the specified element.
     * 
     * Uses a converter for ExternalKey-valued fields, so we can specify them either as "deep" XML
     * elements, or in the string-serialized (slash-delimited) form. I'm amazed that this works,
     * actually.
     * 
     * Ignores namespace.
     */
    public static Object fromXML(Element element) {
        if (element == null)
            return null;
        Logger.getLogger(XMLUtil.class).info("element: " + writeXML(element)); //$NON-NLS-1$
        try {
            String entityName = element.getLocalName();
            Map<String, String> properties = getProperties(element);

            Object object =
                ModelEntityFactory.instantiateInPackage(ModelEntity.class, entityName, null, null);
            if (object == null) {
                Logger.getLogger(XMLUtil.class).error("no instance for name " + entityName); //$NON-NLS-1$
                return null;
            }
            // Convert string inputs to ExternalKey fields.
            ConvertUtils.register(new ExternalKeyConverter(), ExternalKey.class);
            Method[] methods = object.getClass().getMethods();
            properties: for (Map.Entry<String, String> entry : properties.entrySet()) {
                Logger.getLogger(XMLUtil.class).debug("key: " + entry.getKey() //$NON-NLS-1$
                    + " value: " + entry.getValue()); //$NON-NLS-1$
                // ConvertUtils won't let me register a string -> byte[] converter
                try {
                    String fragment =
                        entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
                    // TODO: remove n * n.
                    // TODO: maybe special-case by name? or convert ALL byte[] fields?
                    for (Method method : methods) {
                        if (method.getName().equals("set" + fragment)) { //$NON-NLS-1$
                            Class<?>[] types = method.getParameterTypes();
                            if (types.length == 1) {
                                if (types[0].equals(byte[].class)) {
                                    byte[] arg = KeyUtil.decodeKeyToBytes(entry.getValue());
                                    method.invoke(object, arg);
                                    continue properties;
                                }
                            }
                        }
                    }

                    BeanUtils.setProperty(object, entry.getKey(), entry.getValue());

                } catch (SecurityException e) {
                    Logger.getLogger(XMLUtil.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
                    e.printStackTrace();
                    continue;
                }
            }
            return object;
        } catch (IllegalAccessException e) {
            Logger.getLogger(XMLUtil.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
            return null;
        } catch (InvocationTargetException e) {
            Logger.getLogger(XMLUtil.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Parse the element into nested properties.
     * 
     * It is possible for things like this to exist:
     * 
     * <a>foo<b>bar</b>baz</a>
     * 
     * in this case, we don't try very hard - we support a single text node, which is overwritten as
     * we walk through the child nodes. so in this case we would produce {a => baz, a.b => bar}.
     * 
     * this should never come up in a real situation.
     * 
     * Ignores namespaces, i.e. <a:b>foo</a:b> becomes {b => foo}. I think if you pass a
     * mixed-namespace element to this method and expect anything intelligent to happen, you deserve
     * what you get.
     */
    protected static Map<String, String> getProperties(Node element) {
        Logger.getLogger(XMLUtil.class).debug("getProperties for node: " + writeXML(element)); //$NON-NLS-1$

        Map<String, String> result = new HashMap<String, String>();
        NodeList nodeList = element.getChildNodes();
        // some of these children could be "#text" nodes mixed together with
        // other node types (e.g. Element).
        for (int index = 0; index < nodeList.getLength(); ++index) {
            Node node = nodeList.item(index);
            String name = node.getLocalName();
            Logger.getLogger(XMLUtil.class).debug("node name: " + name); //$NON-NLS-1$
            Logger.getLogger(XMLUtil.class).debug(
                "node type: " + String.valueOf(node.getNodeType())); //$NON-NLS-1$
            if (node.hasChildNodes()) {
                // might be #text
                Logger.getLogger(XMLUtil.class).debug("has children"); //$NON-NLS-1$
                Map<String, String> nodeProperties = getProperties(node);
                // Now prefix the resulting subnodes with the current name prefix
                for (Map.Entry<String, String> entry : nodeProperties.entrySet()) {
                    Logger.getLogger(XMLUtil.class).debug("got child key: " + entry.getKey()//$NON-NLS-1$
                        + " value: " + entry.getValue()); //$NON-NLS-1$
                    String propertyName = entry.getKey();
                    String propertyValue = entry.getValue();
                    if (propertyName.isEmpty()) {
                        // it's a text node, so it goes on the parent
                        result.put(name, propertyValue);
                    } else {
                        result.put(name + "." + propertyName, propertyValue); //$NON-NLS-1$
                    }
                }
            } else {
                Logger.getLogger(XMLUtil.class).debug(
                    "no children, using content " + node.getNodeValue()); //$NON-NLS-1$
                if (node.getNodeType() == Node.TEXT_NODE) {
                    if (!node.getTextContent().trim().isEmpty()) {
                        // ignore empty text nodes, and don't use the node name (#text).
                        result.put("", node.getTextContent()); //$NON-NLS-1$
                    }
                } else {
                    result.put(name, node.getNodeValue());
                }
            }
        }
        return result;
    }

    /**
     * Return true if properties of this type are primitive types, and should just be serialized
     * directly rather than expanded into their own property subtree.
     * 
     * @param clazz
     * @return
     */
    protected static boolean primitive(Class<?> clazz) {
        if (clazz.equals(Integer.TYPE) || clazz.equals(Long.TYPE) || clazz.equals(Short.TYPE)
            || clazz.equals(Byte.TYPE) || clazz.equals(Boolean.TYPE) || clazz.equals(Float.TYPE)
            || clazz.equals(Double.TYPE) || Integer.class.isAssignableFrom(clazz)
            || Long.class.isAssignableFrom(clazz) || Short.class.isAssignableFrom(clazz)
            || Byte.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz)
            || Float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz)
            || String.class.isAssignableFrom(clazz)) {
            Logger.getLogger(XMLUtil.class).debug("primitive: " + clazz.getName()); //$NON-NLS-1$
            return true;
        }
        Logger.getLogger(XMLUtil.class).debug("not primitive: " + clazz.getName());//$NON-NLS-1$
        return false;
    }

    /** Return true if we should ignore properties of this type */
    protected static boolean skip(Class<?> clazz) {
        if (clazz.equals(Class.class)) {
            Logger.getLogger(XMLUtil.class).debug("skip: " + clazz.getName()); //$NON-NLS-1$
            return true;
        }
        Logger.getLogger(XMLUtil.class).debug("don't skip: " + clazz.getName()); //$NON-NLS-1$
        return false;
    }
}
