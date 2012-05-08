/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;

/**
 * Used for xpath processing, provides namespaces:
 * 
 * <ul>
 * <li> xmlns:cc="http://www.joelsgarage.com/callcenter"
 * <li> xmlns:scxml="http://www.w3.org/2005/07/scxml"
 * </ul>
 * 
 * Note that this is the XPATH prefix space, does not match the scxml source prefix space.
 * 
 * @author joel
 */

public class CallCenterNamespaceContext implements NamespaceContext {
    /** The prospector namespace is for model entities used as payload */
    public static final String HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR =
        "http://www.joelsgarage.com/prospector"; //$NON-NLS-1$
    public static final String P = "p"; //$NON-NLS-1$
    /** The SCXML namespace denotes state chart content */
    public static final String HTTP_WWW_W3_ORG_2005_07_SCXML = "http://www.w3.org/2005/07/scxml"; //$NON-NLS-1$
    public static final String SCXML = "scxml"; //$NON-NLS-1$
    /** The callcenter namespace is used for XML content we process */
    public static final String HTTP_WWW_JOELSGARAGE_COM_CALLCENTER =
        "http://www.joelsgarage.com/callcenter"; //$NON-NLS-1$
    public static final String CC = "cc"; //$NON-NLS-1$

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(CC)) {
            Logger.getLogger(CallCenterNamespaceContext.class).info("cc"); //$NON-NLS-1$
            return HTTP_WWW_JOELSGARAGE_COM_CALLCENTER;
        }
        if (prefix.equals(SCXML)) {
            Logger.getLogger(CallCenterNamespaceContext.class).info("scxml"); //$NON-NLS-1$
            return HTTP_WWW_W3_ORG_2005_07_SCXML;
        }
        if (prefix.equals(P)) {
            Logger.getLogger(CallCenterNamespaceContext.class).info(P);
            return HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR;
        }
        return XMLConstants.NULL_NS_URI;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        if (namespaceURI.equals(HTTP_WWW_JOELSGARAGE_COM_CALLCENTER)) {
            Logger.getLogger(CallCenterNamespaceContext.class).info("ns-cc"); //$NON-NLS-1$
            return CC;
        }
        if (namespaceURI.equals(HTTP_WWW_W3_ORG_2005_07_SCXML)) {
            Logger.getLogger(CallCenterNamespaceContext.class).info("ns-scxml"); //$NON-NLS-1$
            return SCXML;
        }
        if (namespaceURI.equals(HTTP_WWW_JOELSGARAGE_COM_PROSPECTOR)) {
            Logger.getLogger(CallCenterNamespaceContext.class).info("ns-p"); //$NON-NLS-1$
            return P;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        Logger.getLogger(CallCenterNamespaceContext.class).info("looking for: " + namespaceURI); //$NON-NLS-1$
        return null;
    }
}