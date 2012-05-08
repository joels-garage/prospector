/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;

/**
 * I use a convention for entity names; this implements the convention.
 * 
 * @author joel
 * 
 */
public abstract class NameUtil {
    // Values for the serialization
    // TODO: use the values for these words found elsewhere, e.g. annotations
    // on the other hand, the annotations could be using these strings.
    // Maybe combine with ExternalKey consts?
    public static final String SUBJECT = "subject"; //$NON-NLS-1$
    public static final String OBJECT = "object"; //$NON-NLS-1$
    public static final String DECISION = "decision"; //$NON-NLS-1$
    public static final String PROPERTY = "property"; //$NON-NLS-1$
    public static final String INDIVIDUAL = "individual"; //$NON-NLS-1$
    public static final String STAKEHOLDER = "stakeholder"; //$NON-NLS-1$
    public static final String REFERENT = "referent"; //$NON-NLS-1$
    public static final String CREATOR = "creator"; //$NON-NLS-1$
    public static final String CREATE_DATE = "createDate"; //$NON-NLS-1$
    public static final String TIMESTAMP = "timestamp"; //$NON-NLS-1$
    public static final String CLASS = "class"; //$NON-NLS-1$
    public static final String SCENE = "scene"; //$NON-NLS-1$
    public static final String START = "start"; //$NON-NLS-1$
    public static final String USER = "user"; //$NON-NLS-1$
    public static final String VALUE = "value"; //$NON-NLS-1$
    public static final String EQUALS = "="; //$NON-NLS-1$
    public static final String AND = "&"; //$NON-NLS-1$
    public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
    public static final String SLASH = "/"; //$NON-NLS-1$

    // /**
    // * Make a namestring for the specified fact It implements the namespace shortcutting -- if the
    // * fact itself is in the same namespace as its subject, the subject's namespace is omitted in
    // * the name, and similarly for the property.
    // *
    // * Might be easier without the weird requirement to have the fact key partially filled in
    // (with
    // * the namespace).
    // *
    // * This "name" is also used for the "key" field of the key.
    // *
    // * TODO: replace all these methods with ModelEntity.setDefaultName()
    // *
    // * @param stringFact
    // * @param namespace
    // * @return
    // */
    // public static String makeFactName(String namespace, String value, ExternalKey subjectKey,
    // ExternalKey propertyKey) {
    // if (namespace == null || subjectKey == null || propertyKey == null)
    // return null;
    // String subject = new String();
    // String property = new String();
    // // this fact in same namespace as its subject?
    // if (namespace.equals(subjectKey.getNamespace())) {
    // subject = subjectKey.getKey();
    // } else {
    // subject = subjectKey.toString();
    // }
    // // this fact in same namespace as its property?
    // if (namespace.equals(propertyKey.getNamespace())) {
    // property = propertyKey.getKey();
    // } else {
    // property = propertyKey.toString();
    // }
    //
    // String encodedSubject = subject;
    // String encodedProperty = property;
    // String encodedValue = value;
    //
    // encodedSubject = encode(subject);
    // encodedProperty = encode(property);
    // encodedValue = encode(value);
    //
    // String name = SUBJECT + EQUALS + encodedSubject //
    // + AND + PROPERTY + EQUALS + encodedProperty //
    // + AND + VALUE + EQUALS + encodedValue;
    //
    // return name;
    // }

    /**
     * Derive the name for an individual based on a string fact value. It's like the above minus the
     * subject.
     * 
     * The thing is that this should be made human readable, at the expense of being unique.
     * 
     * TODO: replace all these methods with ModelEntity.setDefaultName()
     */
    public static String makeIndividualName(String namespace, String value, ExternalKey propertyKey) {
        if (namespace == null || propertyKey == null)
            return null;
        String property = new String();

        property = propertyKey.toString();

        String encodedProperty = property;
        String encodedValue = value;

        encodedProperty = encode(property);
        encodedValue = encode(value);

        String name = PROPERTY + EQUALS + encodedProperty //
            + AND + VALUE + EQUALS + encodedValue;

        return name;
    }

    // public static String makeSceneName(String sceneName, ExternalKey userKey, String time) {
    // return SCENE + EQUALS + sceneName //
    // + AND + USER + EQUALS + encode(userKey) //
    // + AND + START + EQUALS + time;
    // }

    /** Serialize the key and encode it */
    public static String encode(ExternalKey key) {
        return encode(key.toString());
    }

    /** URLEncode if possible, else return the unencoded input */
    public static String encode(String input) {
        Logger.getLogger(NameUtil.class).info("Encoding: " + String.valueOf(input)); //$NON-NLS-1$
        if (input == null) {
            Logger.getLogger(NameUtil.class).info("null input!"); //$NON-NLS-1$
            return null;
        }
        try {
            return URLEncoder.encode(input, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Logger.getLogger(NameUtil.class).error(
                "Encoding problem with URLEncoder, pressing on unescaped"); //$NON-NLS-1$
            return input;
        }
    }

    /** URLDecode if possible, else return the undecoded input */
    public static String decode(String input) {
        try {
            return URLDecoder.decode(input, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Logger.getLogger(NameUtil.class).error(
                "Encoding problem with URLDecoder, pressing on unescaped"); //$NON-NLS-1$
            return input;
        }
    }
}
