/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import org.apache.log4j.Logger;

/**
 * @author joel
 * 
 */
public abstract class ClassUtil {
    /**
     * Strip the package from the classname.
     * 
     * @param clazz
     *            the class to strip e.g. java.lang.String
     * @return the stripped class name, e.g. "String"
     */
    public static String shortClassName(Class<?> clazz) {
        String fullClassName = clazz.getName();
        Logger.getLogger(ClassUtil.class).debug("classname: " + fullClassName); //$NON-NLS-1$
        try {
            String packageName = clazz.getPackage().getName();

            if (packageName.length() == 0)
                return fullClassName;
            int offset = packageName.length() + 1;
            String strippedClassName = fullClassName.substring(offset);
            return strippedClassName;
        } catch (NullPointerException ex) {
            Logger.getLogger(ClassUtil.class).info("null package for: " + fullClassName); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * Create a fully-qualified class name using the package from the supplied peer class
     * 
     * @param peerClass
     *            class with the desired package, e.g. java.lang.String
     * @param className
     *            shortname to append, e.g. "Double"
     * @return full classname in that package, e.g. java.lang.Double
     */
    public static String fullClassName(Class<?> peerClass, String className) {
        Package targetPackage = peerClass.getPackage();
        String fullClassName = targetPackage.getName() + "." + className; //$NON-NLS-1$
        return fullClassName;
    }

    /**
     * Instantiate a Class<?> object with the given short name, in the given package.
     * 
     * @param peerClass
     *            a class in the desired package, e.g. java.lang.String
     * @param className
     *            the short name of the desired class, e.g. Double
     * @return the Class<?> instance, e.g. Class<java.lang.Double>, or null if no such class
     *         exists.
     */
    public static Class<?> classInPackage(Class<?> peerClass, String className) {
        String fullClassName = fullClassName(peerClass, className);
        try {
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClassUtil.class).error(ex.getMessage());
            return null;
        }
    }
}
