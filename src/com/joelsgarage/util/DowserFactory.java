/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sourceforge.stripes.util.ResolverUtil;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;

/**
 * Does the scanning, constructs the Dowser instance, and caches it. Uses the Stripes ResolverUtil
 * scanner.
 * 
 * @author joel
 * 
 */
public abstract class DowserFactory {
    private static final String COMMA = ","; //$NON-NLS-1$
    private static final String PROD_PROPERTIES_FILE = "/dowser.properties"; //$NON-NLS-1$
    private static final String PACKAGES = "dowser.packages"; //$NON-NLS-1$

    private static Properties properties = new Properties();
    public static List<String> packages = new ArrayList<String>();

    public static Set<Class<? extends ModelEntity>> matches;

    /** Cached dowser instance */
    private static Dowser dowser;

    /** Retrieve the dowser from cache, or make a new one, using the file */
    public static Dowser newDowser() {
        if (dowser != null) {
            Logger.getLogger(DowserFactory.class).trace("got cached dowser"); //$NON-NLS-1$
            return dowser;
        }
        setProperties(PROD_PROPERTIES_FILE);
        Logger.getLogger(DowserFactory.class).debug("initializing dowser"); //$NON-NLS-1$

        initializeDowser();
        Logger.getLogger(DowserFactory.class).debug("all done"); //$NON-NLS-1$

        return dowser;
    }

    /** Make, and cache, a new dowser using the specified properties */
    public static Dowser newDowser(Map<String, String> propertiesMap) {
        setProperties(propertiesMap);
        initializeDowser();
        return dowser;
    }

    /** Make, and cache, a new dowser using the specified properties */
    public static Dowser newDowser(String propertiesFile) {
        setProperties(propertiesFile);
        initializeDowser();
        return dowser;
    }

    /** Make a new dowser instance, i.e. do the scan */
    @SuppressWarnings("nls")
    protected static void initializeDowser() {
        String packageString = properties.getProperty(PACKAGES);
        String[] packageArray = packageString.split(COMMA);
        packages = new ArrayList<String>();
        for (String s : packageArray) {
            s = s.trim();
            packages.add(s); // maybe don't need this.
        }

        Logger.getLogger(DowserFactory.class).debug("new resolverutil"); //$NON-NLS-1$

        // Since the stripes resolver looks good, i'll just use that.
        ResolverUtil<ModelEntity> resolver = new ResolverUtil<ModelEntity>();

        Logger.getLogger(DowserFactory.class).debug("find implementations"); //$NON-NLS-1$

        // I'm finding all the implementations and scanning them myself
        // because findAnnotated() seems not to respect the parameter type
        // constraint, thus returning bad (ClassCastException-producing) results
        resolver.findImplementations(ModelEntity.class, packageArray);

        // Set<Class<? extends ModelEntity>> matches = resolver.getClasses();
        matches = resolver.getClasses();

        dowser = new Dowser();
        Logger.getLogger(DowserFactory.class).debug("constructing dowser"); //$NON-NLS-1$

        // Map<String, Class<? extends ModelEntity>> allowedTypes;
        // Map<Class<? extends ModelEntity>, String> typeNames;
        for (Class<? extends ModelEntity> clas : matches) {
            // e.g. Fact.class
            VisibleType visibleType = clas.getAnnotation(VisibleType.class);
            if (visibleType == null)
                continue;
            // It's a visible type, so add it to the bijection.
            dowser.getAllowedTypes().put(visibleType.value(), clas);
            dowser.getTypeNames().put(clas, visibleType.value());

            Logger.getLogger(DowserFactory.class).debug(
                "Trying to cast " + clas.getSuperclass().getName() + " to ModelEntity");
            if (clas.isAssignableFrom(ModelEntity.class)) {
                Logger.getLogger(DowserFactory.class).debug("BOO, this is ModelEntity itself");
                // this is ModelEntity itself, so don't go looking for the superclass.
                continue;
            }
            Class<? extends ModelEntity> superClass =
                clas.getSuperclass().asSubclass(ModelEntity.class);
            VisibleType visibleSuperType = superClass.getAnnotation(VisibleType.class);
            if (visibleSuperType != null) {
                // add it to the inheritance map
                dowser.getAllowedSubtypes().put(superClass, clas);
            }

        }

        dowser.getAllowedSubtypes().transitiveClosure();

        for (Class<? extends ModelEntity> clas : matches) {
            VisibleType visibleType = clas.getAnnotation(VisibleType.class);
            if (visibleType == null)
                continue;
            if (!Modifier.isFinal(clas.getModifiers())) {
                Logger.getLogger(DowserFactory.class).debug(
                    "Skipping non-final join class " + clas.getName());
                continue;
            }
            Method[] methods = clas.getMethods();
            for (Method method : methods) {
                // e.g. getSubjectKey();
                // the only possible joins are ExternalKey getters.
                Class<?> returnType = method.getReturnType();
                if (returnType != ExternalKey.class)
                    continue;
                VisibleJoin visibleJoin = method.getAnnotation(VisibleJoin.class);
                if (visibleJoin == null)
                    continue;
                // it's a join field.
                // e.g. primaryClass = Individual.class
                // the "clas" is the "foreign" class.
                Class<? extends ModelEntity> primaryClass = visibleJoin.value();
                // Check that the join class is visible
                VisibleType joinVisibleType = primaryClass.getAnnotation(VisibleType.class);
                if (joinVisibleType == null)
                    continue;
                // e.g. "subject"
                String joinLabel = visibleJoin.name();
                if (joinLabel == null || joinLabel.length() == 0)
                    continue;
                // This join goes on the explicit class, if it's final.
                if (Modifier.isFinal(primaryClass.getModifiers())) {
                    Logger.getLogger(DowserFactory.class).debug(
                        "allowed join from " + primaryClass.getName() + " to final class "
                            + clas.getName());
                    dowser.getAllowedJoins().put(primaryClass, clas, method, joinLabel);
                    continue;
                }
                Logger.getLogger(DowserFactory.class).debug(
                    "non-final primary class " + primaryClass.getName() + " modifiers "
                        + String.valueOf(primaryClass.getModifiers()));
                // ... and if it's not, go on its descendants.
                Set<Class<? extends ModelEntity>> allChildren =
                    dowser.getAllowedSubtypes().get(primaryClass);
                if (allChildren == null) {
                    Logger.getLogger(DowserFactory.class).debug(
                        "no children for class " + primaryClass.getName());
                    continue;
                }
                // I think this is bad. We should use only the direct annotations,
                // otherwise there is no way to indicate covariance, e.g.
                // fact subject => property
                // string-fact subject => string-property (and not quantity-property)
                //
                // but man that is tedious.

                for (Class<? extends ModelEntity> child : allChildren) {
                    // only join to final classes, not middle ones.
                    if (!Modifier.isFinal(child.getModifiers())) {
                        Logger.getLogger(DowserFactory.class).debug(
                            "no allowed join on non final class " + primaryClass.getName());
                        continue;
                    }
                    Logger.getLogger(DowserFactory.class).debug(
                        "allowed join on final class " + primaryClass.getName());
                    dowser.getAllowedJoins().put(child, clas, method, joinLabel);
                }
            }
        }
    }

    /** Set the properties using the specified file */
    @SuppressWarnings("nls")
    protected static void setProperties(String filename) {
        properties = new Properties();
        try {
            URL url = DowserFactory.class.getResource(filename);
            if (url == null) {
                Logger.getLogger(DowserFactory.class).error("couldn't find: " + filename);
                throw new IOException("couldn't find: " + filename);
            }
            properties.load(url.openStream());
        } catch (IOException ex) {
            ex.printStackTrace();
            Logger.getLogger(DowserFactory.class).error(
                "Couldn't open dowser properties.  It's not exactly "
                    + "fatal, so we'll continue, but probably stuff won't work.");
            throw new RuntimeException("ok its actually fatal so die");
        }
    }

    /** Set the properties with a map; for testing */
    protected static void setProperties(Map<String, String> map) {
        properties = new Properties();
        properties.putAll(map);
    }

}
