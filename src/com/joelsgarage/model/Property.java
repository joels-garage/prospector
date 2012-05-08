/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;
import com.joelsgarage.util.WizardField;

/**
 * Represents the potential existence of some description of an individual, i.e. a Property instance
 * is the type describing instances of Fact.
 * 
 * All properties have the same kind of domain (i.e. an individual, with a class constraint)
 * <p>
 * Would be nice to say something about existence, in these cases:
 * <ul>
 * <li> property must exist -- e.g. person must have gender
 * <li> property may not exist -- e.g. a computer may not have a hard disk at all
 * <li>property may not be known -- e.g. we just don't know the gender of person-x
 * <p>
 * So I think that just comes down to representing a "don't know" value and min-cardinality (1).
 * <p>
 * Properties may be primitive-valued ("data") or individual-valued ("object"). See subclasses for
 * details.
 * <p>
 * Another possibility is class-valued properties, like, "this individual is a component of this set
 * [class] of other individuals," e.g. "this water pump fits this class of engines." It's like a
 * class axiom, if you define a singleton class for this individual, but that seems like a weird
 * thing to do. Or maybe it isn't, since it means a simpler data model, and the truth is that if
 * there is such a component-relationship, it is almost certainly non-unique, i.e. a water pump from
 * manufacturer A or B will work, so it's a little class.
 * <p>
 * Examples
 * <ul>
 * <li>Cars have mileage
 * <li>Camera lenses have zoom (which may be zero)
 * </ul>
 * 
 * TODO: think about property inheritance, so that I don't have to restate the property over and
 * over.
 */
@VisibleType(ExternalKey.PROPERTY_TYPE)
public abstract class Property extends ModelEntity {
    public static final String DOMAIN_CLASS = "domain_class"; //$NON-NLS-1$
    /**
     * Domain Class, e.g. "hard disk drive". The class of thing this property describes.
     */
    private ExternalKey domainClassKey = new ExternalKey();

    /**
     * Used in keys, not persisted (oh yes it is now!). Every property has a name; in fact, a
     * property is distinguished only by the name and domain.
     */
    private String name;

    protected Property() {
        super();
    }

    public Property(ExternalKey domainClassKey, String name, String namespace) {
        super(namespace);
        setName(name);
        setDomainClassKey(domainClassKey);
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getDomainClassKey());
        u.update(getName());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setDomainClassKey(new ExternalKey(input.get(DOMAIN_CLASS)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(DOMAIN_CLASS, String.valueOf(getDomainClassKey()));
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " domain class : " + String.valueOf(getDomainClassKey());
    // return result;
    // }

    // /**
    // * all properties subtypes have the same key structure.
    // *
    // * Assumes the domain class key is escaped and the name is not.
    // */
    // @Override
    // public String compositeKeyKey() {
    // return DOMAIN_CLASS + NameUtil.EQUALS + getDomainClassKey() //
    // + NameUtil.AND //
    // + NAME + NameUtil.EQUALS + NameUtil.encode(getName());
    // }

    @VisibleJoin(value = Class.class, name = DOMAIN_CLASS)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getDomainClassKey() {
        return this.domainClassKey;
    }

    public void setDomainClassKey(ExternalKey domainClassKey) {
        this.domainClassKey = domainClassKey;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
