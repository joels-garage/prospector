/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents a property whose value is an individual, constrained to a type.
 * <p>
 * Example:
 * <ul>
 * <li>individual-property(campsite-x, is-part-of, campground-y)
 * </ul>
 * 
 * @author joel
 */
@VisibleType(ExternalKey.INDIVIDUAL_PROPERTY_TYPE)
public final class IndividualProperty extends Property {
    public static final String RANGE_CLASS = "range_class"; //$NON-NLS-1$
    /**
     * The class of thing that is the description, e.g. "colors." (a bad example of course; color is
     * not symbolic, it's a quantity, the energy of radiation).
     */
    private ExternalKey rangeClassKey = new ExternalKey();

    protected IndividualProperty() {
        super();
    }

    /**
     * An individual property key depends on its domain and its "name" but *NOT* on the range. That
     * is, you can't have a property on the same class, with the same name, but a different range
     * class; that would be craaaazy. So if you just want the key, it's ok to leave the range null.
     * 
     * TODO: break out key generation.
     * 
     * @param domainClassKey
     * @param name
     *            used in key only
     * @param rangeClassKey
     * @param namespace
     * @return
     * @throws FatalException
     */
    public IndividualProperty(ExternalKey domainClassKey, String name, ExternalKey rangeClassKey,
        String namespace) throws FatalException {
        super(domainClassKey, name, namespace);
        setRangeClassKey(rangeClassKey);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setRangeClassKey(new ExternalKey(input.get(RANGE_CLASS)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(RANGE_CLASS, String.valueOf(getRangeClassKey()));
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " range class : " + String.valueOf(getRangeClassKey());
    // return result;
    //    }

    /**
     * This is NOT a key field! Don't make properties with the same domain and name but different
     * types.
     */
    @VisibleJoin(value = Class.class, name = RANGE_CLASS)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getRangeClassKey() {
        return this.rangeClassKey;
    }

    public void setRangeClassKey(ExternalKey rangeClassKey) {
        this.rangeClassKey = rangeClassKey;
    }
}
