/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.w3c.dom.Element;

/**
 * Interface implemented by services capable of receiving "send" payload Elements, which are DOM
 * elements.
 * 
 * @author joel
 */
public interface SendTarget {
    /**
     * Indicates the method should be registered as a SendTarget.
     * 
     * @author joel
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface Registrant {
        // just a marker
    }

    /**
     * @param data
     *            the type of this message; corresponds to the "data" element name in the FSM
     *            datamodel
     * @param element
     *            the payload, i.e. children of that data element.
     */
    public void send(String data, Element element);

    /**
     * Instruct the send target to register itself with the registry.
     * 
     * @param registry
     */
    public void registerThyself(SendTargetRegistry registry);
}
