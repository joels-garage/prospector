/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.joelsgarage.util.ClassUtil;

/**
 * Because I want entities to be more opaque, this class, in the model package, does all the
 * instantiation that external classes used to do.
 * 
 * @author joel
 * 
 */
public class ModelEntityFactory {
    /**
     * Instantiate the specified className within the same package as clazz. Works for object-type
     * arguments only; dealing with primitives is possible but annoying.
     * 
     * @param clazz
     *            the class whose package you want to use
     * @param className
     *            the name of the class you want
     * @param arguments
     *            the arguments to its constructor
     * @return the new object, or null if there's any reason it couldn't be constructed
     */
    public static Object instantiateInPackage(java.lang.Class<?> clazz, String className,
        java.lang.Class<?>[] parameterTypes, Object[] arguments) {
        Logger.getLogger(ModelEntityFactory.class).error("instantiating class " + clazz.getName()); //$NON-NLS-1$

        if (arguments == null) {
            Logger.getLogger(ModelEntityFactory.class).error("null arguments"); //$NON-NLS-1$
        } else {
            Logger.getLogger(ModelEntityFactory.class).error("arg count " + arguments.length); //$NON-NLS-1$
        }

        if (parameterTypes == null) {
            Logger.getLogger(ModelEntityFactory.class).error("null parameter types"); //$NON-NLS-1$
        } else {
            Logger.getLogger(ModelEntityFactory.class).error(
                "parameter type count " + parameterTypes.length); //$NON-NLS-1$
        }

        String fullClassName = ClassUtil.fullClassName(clazz, className);
        Logger.getLogger(ModelEntityFactory.class).error("full name " + fullClassName); //$NON-NLS-1$
        java.lang.Class<?> targetClass = ClassUtil.classInPackage(clazz, className);
        if (targetClass == null)
            return null;
        Logger.getLogger(ModelEntityFactory.class).error("target class " + targetClass.getName()); //$NON-NLS-1$
        try {
            Constructor<?> constructor = targetClass.getDeclaredConstructor(parameterTypes);
            return constructor.newInstance(arguments);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ModelEntityFactory.class).error(ex.getMessage());
        } catch (InstantiationException ex) {
            Logger.getLogger(ModelEntityFactory.class).error(ex.getMessage());
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(ModelEntityFactory.class).error(ex.getMessage());
        } catch (InvocationTargetException ex) {
            Logger.getLogger(ModelEntityFactory.class).error(ex.getMessage());
        }
        Logger.getLogger(ModelEntityFactory.class).error(
            "Failed to instantiate class " + fullClassName); //$NON-NLS-1$
        return null;
    }
}
