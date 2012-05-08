/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.joelsgarage.util.ClassUtil;
import com.joelsgarage.util.KeyUtil;

/**
 * Uses BeanUtils to translate from ModelEntity subclasses to XML elements.
 * 
 * @author joel
 * 
 */
public class Translator {
    private static final String MODEL_PACKAGE = "com.joelsgarage.model."; //$NON-NLS-1$

    public Translator() {
        // foo
    }

    public static Element toXML(Object object) {
        return toXML(object, null);
    }

    public static Element toXMLNS(Object object, String namespacePrefix, String namespaceURI) {
        return toXMLNS(object, null, namespacePrefix, namespaceURI);
    }

    /** Return true if we should ignore properties of this type */
    protected static boolean skip(Class<?> clazz) {
        if (clazz.equals(Class.class)) {
            Logger.getLogger(Translator.class).debug("skip: " + clazz.getName()); //$NON-NLS-1$
            return true;
        }
        Logger.getLogger(Translator.class).debug("don't skip: " + clazz.getName()); //$NON-NLS-1$
        return false;
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
            Logger.getLogger(Translator.class).debug("primitive: " + clazz.getName()); //$NON-NLS-1$
            return true;
        }
        Logger.getLogger(Translator.class).debug("not primitive: " + clazz.getName());//$NON-NLS-1$
        return false;
    }

    /**
     * Construct an XML Element reflecting the specified object.
     * 
     * @param object
     *            the object to serialize
     * @param outerName
     *            the container tag name (or null to use the short classname as the container)
     * @param namespaceURI
     *            optional namespace for the element and its children
     * @return
     */
    public static Element toXMLNS(Object object, String outerName, String namespacePrefix,
        String namespaceURI) {
        Namespace namespace = new Namespace(namespacePrefix, namespaceURI);
        Logger.getLogger(Translator.class).debug("toXMLNS"); //$NON-NLS-1$
        String shortTypeName = ClassUtil.shortClassName(object.getClass());

        Logger.getLogger(Translator.class).debug("working on objectname: " + shortTypeName); //$NON-NLS-1$

        if (shortTypeName == null) {
            Logger.getLogger(Translator.class).error("barf"); //$NON-NLS-1$
            return null;
        }

        String elementName;
        if (outerName == null) {
            elementName = shortTypeName;
        } else {
            elementName = outerName;
        }

        // no matter what it is, every object gets a container element with either the
        // typename (for "outer" elements) or the specified element name.
        Element element = DocumentHelper.createElement(new QName(elementName, namespace));

        Logger.getLogger(Translator.class).debug("created element for name: " + elementName); //$NON-NLS-1$

        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(object);
        if (propertyDescriptors.length == 0) {
            // it's primitive, or has no properties, so just set the text as best we can, and we're
            // done.
            Logger.getLogger(Translator.class).debug(
                "primitive, setting text to " + object.toString()); //$NON-NLS-1$

            element.setText(object.toString());
            return element;
        }
        for (int i = 0; i < propertyDescriptors.length; ++i) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String name = descriptor.getName(); // e.g. creatorKey
            Logger.getLogger(Translator.class).debug("looking at property name: " + name); //$NON-NLS-1$

            Class<?> clazz = descriptor.getPropertyType();

            if (skip(clazz)) {
                Logger.getLogger(Translator.class).debug("skipping property name: " + name); //$NON-NLS-1$
                continue;
            }
            try {
                if (primitive(clazz)) {
                    // We just do toString() on primitive types.
                    Logger.getLogger(Translator.class)
                        .debug("got primitive property name: " + name); //$NON-NLS-1$
                    // TODO: escaping
                    Object property = PropertyUtils.getProperty(object, name);
                    if (property == null) {
                        Logger.getLogger(Translator.class).debug(
                            "got null property: " + name + " for class " + clazz.getName() //$NON-NLS-1$//$NON-NLS-2$
                                + " for object class " + shortTypeName); //$NON-NLS-1$
                        continue;
                    }
                    Element propertyElement = element.addElement(new QName(name, namespace));
                    propertyElement.setText(property.toString());
                    continue;
                } else if (clazz == byte[].class) {
                    // TODO: clean up this cut n paste
                    Logger.getLogger(Translator.class).debug("got byte[] property name: " + name); //$NON-NLS-1$
                    Object property = PropertyUtils.getProperty(object, name);
                    if (property == null) {
                        Logger.getLogger(Translator.class).debug(
                            "got null property: " + name + " for class " + clazz.getName() //$NON-NLS-1$//$NON-NLS-2$
                                + " for object class " + shortTypeName); //$NON-NLS-1$
                        continue;
                    }
                    Element propertyElement = element.addElement(new QName(name, namespace));
                    propertyElement.setText(KeyUtil.encode((byte[]) property));
                    continue;
                }

                Logger.getLogger(Translator.class).debug(
                    "adding element for property name: '" + name + "'"); //$NON-NLS-1$ //$NON-NLS-2$

                Object propertyObject = PropertyUtils.getProperty(object, name);
                Logger.getLogger(Translator.class).debug(
                    "got propertyObject: " + propertyObject.toString()); //$NON-NLS-1$

                Element propertyElement =
                    toXMLNS(propertyObject, name, namespacePrefix, namespaceURI);

                Logger.getLogger(Translator.class).debug(
                    "got propertyElement: " + propertyElement.asXML()); //$NON-NLS-1$

                element.add(propertyElement);
            } catch (NoSuchMethodException e) {
                Logger.getLogger(Translator.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
                return element;
            } catch (InvocationTargetException e) {
                Logger.getLogger(Translator.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
                return element;
            } catch (IllegalAccessException e) {
                Logger.getLogger(Translator.class).error("barf: " + e.getMessage()); //$NON-NLS-1$
                return element;
            }
        }

        return element;

    }

    /**
     * Construct an XML element from the given object, with outerName as the outer tag name.
     * 
     * If outerName is null, use the short typename.
     * 
     * @param object
     * @param outerName
     * @return
     */
    public static Element toXML(Object object, String outerName) {
        return toXMLNS(object, outerName, null);
    }

    /**
     * Parse the element into nested properties.
     * 
     * @param element
     * @return null if there are no child elements
     */
    protected static Map<String, String> getProperties(Element element) {
        Map<String, String> result = new HashMap<String, String>();
        Iterator<?> iter = element.elementIterator();
        if (!iter.hasNext()) {
            return null;
        }
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof Element) {
                Element innerElement = (Element) o;
                String innerElementName = innerElement.getName(); // e.g. creatorKey
                Logger.getLogger(Translator.class).debug("working on: " + innerElementName);//$NON-NLS-1$
                Iterator<?> innerIter = innerElement.elementIterator();
                if (innerIter.hasNext()) {
                    // has children -- recurse on them
                    Logger.getLogger(Translator.class).debug(
                        "working on CHILDREN of: " + innerElementName); //$NON-NLS-1$
                    Map<String, String> childProperties = getProperties(innerElement);
                    if (childProperties == null) {
                        Logger.getLogger(Translator.class).error(
                            "no child properties for: " + innerElementName); //$NON-NLS-1$
                        // this should never happen. if it does, just skip it.
                        break;
                    }
                    // copy the child properties into (prefixed) parent properties
                    for (Map.Entry<String, String> entry : childProperties.entrySet()) {
                        String key = innerElementName + "." + entry.getKey();//$NON-NLS-1$
                        String value = entry.getValue();
                        Logger.getLogger(Translator.class).debug(
                            "adding key: " + key + " value: " + value); //$NON-NLS-1$ //$NON-NLS-2$
                        result.put(key, value);
                    }
                } else {
                    // has no children, just set the text
                    Logger.getLogger(Translator.class).debug(
                        "working on VALUE of: " + innerElementName); //$NON-NLS-1$
                    result.put(innerElementName, innerElement.getText());
                }
            } else {
                Logger.getLogger(Translator.class).error(
                    "got weird type: " + o.getClass().getName()); //$NON-NLS-1$
            }
        }
        return result;
    }

    public static Object fromXML(Element element) {
        String entityName = element.getName(); // like "Individual"
        Logger.getLogger(Translator.class).debug("working on: " + entityName); //$NON-NLS-1$
        Map<String, String> properties = getProperties(element);

        try {
            Object object = Class.forName(MODEL_PACKAGE + entityName).newInstance();
            if (object == null) {
                Logger.getLogger(Translator.class).error("failed newInstance()"); //$NON-NLS-1$
                return null;
            }
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                // set nested properties.
                try {
                    Logger.getLogger(Translator.class).debug(
                        "object type: " + object.getClass().getName() //$NON-NLS-1$
                            + " set name: " + entry.getKey() + " value: " + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
                    BeanUtils.setProperty(object, entry.getKey(), entry.getValue());
                } catch (InvocationTargetException e) {
                    Logger.getLogger(Translator.class).error(
                        "couldn't set property: " + e.getMessage()); //$NON-NLS-1$
                    // just swallow it, go to the next one
                }
            }
            return object;
        } catch (IllegalAccessException e) {
            Logger.getLogger(Translator.class).error("failed: " + e.getMessage()); //$NON-NLS-1$
            return null;
        } catch (InstantiationException e) {
            Logger.getLogger(Translator.class).error("failed: " + e.getMessage()); //$NON-NLS-1$
            return null;
        } catch (ClassNotFoundException e) {
            Logger.getLogger(Translator.class).error("failed: " + e.getMessage()); //$NON-NLS-1$
            return null;
        }
    }
}
