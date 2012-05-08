/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ModelEntity;

/**
 * The Dowser holds various maps of visible model types and fields. It "tells the user where they
 * can drill down," which is what a dowser does. :-)
 * 
 * Would be good to figure out how to cache the instance, if it's expensive to make.
 * 
 * I thought about using annotations and scanning the model package to find them, but that's yet
 * another thing to figure out, even though it would be kinda cool.
 * 
 * @author joel
 * 
 */
public class Dowser {
    /**
     * Allowed values for the "type" field. Not all configured types are allowed. Examples:
     * 
     * <code>
     * "decision" => Decision.class
     * "fact" => Fact.class
     * </code>
     */
    private Map<String, Class<? extends ModelEntity>> allowedTypes //
    = new HashMap<String, Class<? extends ModelEntity>>();

    /**
     * The reverse of above. Examples:
     * 
     * <code>
     * Decision.class => "decision"
     * Fact.class => "fact"
     * </code>
     */
    private Map<Class<? extends ModelEntity>, String> typeNames //
    = new HashMap<Class<? extends ModelEntity>, String>();

    /**
     * If a non-final key type is specified, we don't show a mixed list, show a clustered list of
     * all the final descendant types. This map holds the descendants of every visible type.
     * Examples:
     * 
     * <code>
     * Fact.class => {IndividualFact.class, QuantityFact.class, StringFact.class}
     * </code>
     */

    private SubclassMap<Class<? extends ModelEntity>> allowedSubtypes =
        new SubclassMap<Class<? extends ModelEntity>>();

    /**
     * Maps foreign class to a set of maps, which map the join class to a set of fields. For
     * example,
     * 
     * <code>
     * Individual => {Fact => {Subject, Object}, ClassMember => {Individual}}
     * </code>
     */
    private JoinMap allowedJoins = new JoinMap();

    public Dowser() {
        // foo
    }

    /**
     * Is the specified class an allowed class, i.e. will not result in a suicidal union when
     * requested from Hibernate?
     */
    public static boolean isAllowed(Class<?> clas) {
        if (clas == null) {
            Logger.getLogger(Dowser.class).error("Null class in isAllowed()"); //$NON-NLS-1$
            return false;
        }
        // We denote allowedness with finality.
        if (Modifier.isFinal(clas.getModifiers())) {
            return true;
        }
        return false;
    }

    public Map<String, Class<? extends ModelEntity>> getAllowedTypes() {
        return this.allowedTypes;
    }

    public void setAllowedTypes(Map<String, Class<? extends ModelEntity>> allowedTypes) {
        this.allowedTypes = allowedTypes;
    }

    public SubclassMap<Class<? extends ModelEntity>> getAllowedSubtypes() {
        return this.allowedSubtypes;
    }

    public void setAllowedSubtypes(SubclassMap<Class<? extends ModelEntity>> allowedSubtypes) {
        this.allowedSubtypes = allowedSubtypes;
    }

    public Map<Class<? extends ModelEntity>, String> getTypeNames() {
        return this.typeNames;
    }

    public void setTypeNames(Map<Class<? extends ModelEntity>, String> typeNames) {
        this.typeNames = typeNames;
    }

    public JoinMap getAllowedJoins() {
        return this.allowedJoins;
    }

    public void setAllowedJoins(JoinMap allowedJoins) {
        this.allowedJoins = allowedJoins;
    }
}
