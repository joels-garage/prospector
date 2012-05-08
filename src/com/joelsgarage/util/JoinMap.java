/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ModelEntity;

/**
 * Stores data about "one to many" joins, i.e. where a little list is associated with an entity.
 * Example, kinda like this:
 * 
 * <code>
 * class Individual {
 *   List<Fact> facts-of-which-this-individual-is-a-subject
 * </code>
 * 
 * note this class is not written that way. :-)
 * 
 * This only exists to prettify the access in Dowser.
 * 
 * So this map currently covers *explicit* relations, i.e. it just mirrors the annotations.
 * 
 * But what we actually *want* is, for a subclass, to inherit the superclass joins.
 * 
 * I think that can be done pretty simply in the factory...
 * 
 * ok, so, what i need is primary => joindataset, but also joinclass => joindataset for the reverse
 * relation.
 * 
 * oh maybe i can just get those through reflection.
 * 
 * @author joel
 */
public class JoinMap {
    private Map<Class<? extends ModelEntity>, Set<ForeignKey>> foreignKeys;

    public JoinMap() {
        this.foreignKeys = new HashMap<Class<? extends ModelEntity>, Set<ForeignKey>>();
    }

    public void put(Class<? extends ModelEntity> primary, Class<? extends ModelEntity> foreign,
        Method method, String label) {
        Logger.getLogger(JoinMap.class).debug(//
            "Adding primary: " + primary.getName() //$NON-NLS-1$
                + " foreign: " + foreign.getName() //$NON-NLS-1$
                + " method: " + method.getName() //$NON-NLS-1$
                + " label: " + label); //$NON-NLS-1$

        Set<ForeignKey> foreignSet = this.foreignKeys.get(primary);
        if (foreignSet == null) {
            Logger.getLogger(JoinMap.class).debug("Making new set"); //$NON-NLS-1$
            foreignSet = new HashSet<ForeignKey>();
            this.foreignKeys.put(primary, foreignSet);
        }
        ForeignKey foreignKey = new ForeignKey(foreign, method, label);
        foreignSet.add(foreignKey);
    }

    public Set<ForeignKey> get(Class<? extends ModelEntity> primary) {
        return this.foreignKeys.get(primary);
    }
}
