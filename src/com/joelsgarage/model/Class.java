/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * A class is just a collection of individuals. At the moment, the class membership is purely
 * enumerated (see ClassMember). There is no relationship between classes, and no other way to
 * determine if an individual belongs to a class, other than the enumerated membership.
 * <p>
 * This is pretty simple, and maybe not enough for the long term, but it's enough for now. Certainly
 * if the class membership is really going to be assigned either by a user or by a classifier, then
 * we're just populating ClassMember. For more complicated class-membership predicates, look at the
 * notes wiki.
 * <p>
 * In this application, a class is used to represent the kind of thing you're looking for, i.e. the
 * <x> in "choosing a <x>".
 * <p>
 * Examples:
 * <ul>
 * <li>"campsite"
 * <li>"canon S200 camera"
 * </ul>
 * Sometimes an individual is an actual unique item in the real world, like my particular car, which
 * is differentiated from other cars of the same type by things like mileage. Sometimes an
 * individual is a "canonical individual," in cases where the instances are not usefully
 * distinguishable (e.g. a particular model of camera).
 * <p>
 * There are relatively few classes, say, thousands, at least to start with.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.CLASS_TYPE)
public final class Class extends ModelEntity {
    public static final String NAME = "name"; //$NON-NLS-1$
    // /**
    // * The description is just a text description of the class. OWL uses "description" to mean
    // * something else. Don't think about that.
    // */
    // private String description = new String();
    /** Not persisted; used in the key -- oh yes it is */
    private String name = null;

    protected Class() {
        super();
    }

    // name is not stored; it's just in the key
    public Class(String name, String namespace) throws FatalException {
        super(namespace);
        setName(name);
        populateKey();
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setName(input.get(NAME));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(NAME, getName());
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getName());
    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.encode(getName());
    // }

    // @Override
    // public DisplayModelEntity newDisplayEntity() {
    // DisplayClass d = new DisplayClass(true);
    // d.setInstance(this);
    // return d;
    // }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // @Override
    // @SuppressWarnings("nls")
    // public String toString() {
    // String result = super.toString();
    // result += " description: " + String.valueOf(getDescription());
    // return result;
    // }

    // @VisibleField("description")
    // @WizardField(type = WizardField.Type.OPTIONAL, position = 2)
    // public String getDescription() {
    // return this.description;
    // }
    //
    // protected void setDescription(String description) {
    // this.description = description;
    // }

}
