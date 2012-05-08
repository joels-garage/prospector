/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * The default implementation uses annotations and reflection to identify the target for each
 * "send".
 * 
 * @author joel
 * 
 */
public abstract class SendTargetBase implements SendTarget {
    protected static NamespaceContext namespaceContext = new CallCenterNamespaceContext();

    /**
     * Register annotated methods
     * 
     * @see com.joelsgarage.callcenter.SendTarget#registerThyself(com.joelsgarage.callcenter.SendTargetRegistry)
     */
    @Override
    public void registerThyself(SendTargetRegistry registry) {
        Logger.getLogger(SendTargetBase.class).info("registering"); //$NON-NLS-1$

        Class<?> thisClass = this.getClass();
        Method[] methods = thisClass.getMethods();
        for (int iter = 0; iter < methods.length; ++iter) {
            Method method = methods[iter];
            if (method == null)
                continue;

            // Find the annotation
            if (!method.isAnnotationPresent(SendTarget.Registrant.class))
                continue;

            // Verify argument types
            Class<?>[] classes = method.getParameterTypes();
            if (classes.length != 2)
                continue;
            if (classes[0] != String.class)
                continue;
            if (classes[1] != Element.class)
                continue;

            Logger.getLogger(SendTargetBase.class).info("registering method " + method.getName()); //$NON-NLS-1$
            if (registry != null) {
                registry.registerSendTarget(method.getName(), this);
            }
        }
    }

    /**
     * Find the method to call through reflection.
     * 
     * TODO: change the method signature to involve a return value, which is turned into a response
     * event.
     * 
     * @see com.joelsgarage.callcenter.SendTarget#send(java.lang.String, org.w3c.dom.Element)
     */
    @Override
    public void send(String data, Element element) {
        if (data == null)
            return;
        try {
            Class<?> thisClass = this.getClass();
            Logger.getLogger(SendTargetBase.class).info("This is class: " + thisClass.getName()); //$NON-NLS-1$

            Method method =
                this.getClass().getMethod(data, new Class<?>[] { String.class, Element.class });
            method.invoke(this, data, element);
        } catch (SecurityException e) {
            Logger.getLogger(SendTargetBase.class).error("No such method: " + data); //$NON-NLS-1$
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Logger.getLogger(SendTargetBase.class).error("No such method: " + data); //$NON-NLS-1$
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Logger.getLogger(SendTargetBase.class).error("Bad invocation: " + data); //$NON-NLS-1$
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Logger.getLogger(SendTargetBase.class).error("Bad invocation: " + data); //$NON-NLS-1$
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Logger.getLogger(SendTargetBase.class).error("Bad invocation: " + data); //$NON-NLS-1$
            e.printStackTrace();
        }
    }
}
