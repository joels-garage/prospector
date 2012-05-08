/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

@VisibleType(ExternalKey.PROPERTY_PREFERENCE_TYPE)
public abstract class PropertyPreference extends Preference {
    /**
     * The property being evaluated, e.g. "color". Obviously this property can conflict with the
     * individuals below, e.g. if they're not members of the property range class.
     * 
     * TODO: do something about that, e.g. validation of some kind.
     * 
     * This reference is covariant, i.e. subclasses of PropertyPreferences generally refer to
     * subclasses of Property.
     */
    protected ExternalKey propertyKey = new ExternalKey();

    protected PropertyPreference() {
        super();
    }

    /** Key includes stakeholder and property */
    protected PropertyPreference(ExternalKey stakeholderKey, ExternalKey propertyKey,
        String namespace) {
        super(stakeholderKey, namespace);
        setPropertyKey(propertyKey);
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " property : " + String.valueOf(getPropertyKey());
    // return result;
    //    }

    /** The property being evaluated. Covariant. */
    @VisibleJoin(value = Property.class, name = "property")
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getPropertyKey() {
        return this.propertyKey;
    }

    public void setPropertyKey(final ExternalKey propertyKey) {
        this.propertyKey = propertyKey;
    }
}