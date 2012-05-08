/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import org.w3c.dom.Element;

import com.joelsgarage.util.XMLUtil;

/**
 * Stuff for sandbox.sc.xml.
 * 
 * @author joel
 * 
 */
public class Sandbox extends SendTargetBase {
    private EventListener listener;
    private String outcome = new String();

    public Sandbox(EventListener listener) {
        setListener(listener);
    }

    /** Clear the subroutine outcome. */
    @SendTarget.Registrant
    public void clearSub(@SuppressWarnings("unused")
    String data, @SuppressWarnings("unused")
    Element element) {
        this.outcome = new String();
        getListener().handleMessage("sandbox_clear_sub_done", null); //$NON-NLS-1$
    }

    /** This class knows whether the subroutine needs to be called or not */
    @SendTarget.Registrant
    public void callSub(@SuppressWarnings("unused")
    String data, @SuppressWarnings("unused")
    Element element) {
        if (this.outcome.equals(new String())) {
            // no outcome -- need to call it.
            getListener().handleMessage("call", null); //$NON-NLS-1$
        } else if (this.outcome.equals("A")) { //$NON-NLS-1$
            getListener().handleMessage("outcome_a", null); //$NON-NLS-1$
        } else {
            getListener().handleMessage("outcome_b", null); //$NON-NLS-1$
        }
    }

    /** Set the outcome. Called by the subroutine itself. */
    @SendTarget.Registrant
    public void setSub(@SuppressWarnings("unused")
    String data, Element element) {
        this.outcome =
            XMLUtil.getNodeValueByPath(namespaceContext, element, "scxml:data/cc:outcome/text()"); //$NON-NLS-1$
        getListener().handleMessage("done", null); //$NON-NLS-1$
    }

    public EventListener getListener() {
        return this.listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }
}
