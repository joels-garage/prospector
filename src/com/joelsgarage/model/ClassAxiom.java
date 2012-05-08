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
 * A class axiom is something like "subclass-of," or "accessory to," that applies to all members of
 * the class.
 * <p>
 * I'm not going to implement this until I actually need it.
 * <p>
 * Examples:
 * <ul>
 * <li>This class of water pumps fits this class of engines.
 * <li>A chair is a kind of furniture.
 * <li>The democratic party is a kind of policial party.
 * <li>The class of "lightbulbs" solves the problem "make light"
 * </ul>
 * <p>
 * More thoughts:
 * <p>
 * Actually some pretty simple cases require class axioms, e.g. the salient class is a union of
 * other classes in the decision, "what to bring to a knife fight?" (e.g. knife, gun, police)
 * <p>
 * So, maybe I really do need to implement unions.
 * <p>
 * TODO: how to express this?
 * 
 * The most important class axiom (as well as other kinds of axioms -- add those too?) is to specify
 * a relation between entities in different namespaces, e.g. this "joel" individual in the linkedin
 * namespace is equivalent to that "joel" individual in the facebook namespace, or this "flower"
 * class in the usda namespace is a subset of that "flowering plant" class in some other namespace.
 * The most important cross-class axioms are those that reference the "production" namespace, i.e.
 * constitute justification for production entities.
 * 
 * So, should the axiom be class-specific? Or can it just relate any entities? I think it would be
 * ok for it to be general, but it's also easy to make a type that relates the other types I want.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.CLASS_AXIOM_TYPE)
public abstract class ClassAxiom extends ModelEntity {
    public static final String SUBJECT = "subject"; //$NON-NLS-1$
    public static final String OBJECT = "object"; //$NON-NLS-1$

    /** The subject Class of this axiom */
    private ExternalKey subjectKey = new ExternalKey();

    /** The object Class of this axiom */
    private ExternalKey objectKey = new ExternalKey();

    protected ClassAxiom() {
        super();
        setSubjectKey(new ExternalKey());
        setObjectKey(new ExternalKey());
    }

    public ClassAxiom(ExternalKey subjectKey, ExternalKey objectKey, String namespace) {
        super(namespace);
        setSubjectKey(subjectKey);
        setObjectKey(objectKey);
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setSubjectKey(new ExternalKey(input.get(SUBJECT)));
        setObjectKey(new ExternalKey(input.get(OBJECT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(SUBJECT, getSubjectKey().toString());
        output.put(OBJECT, getObjectKey().toString());
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getSubjectKey());
        u.update(getObjectKey());
    }

    // @Override
    // public String toString() {
    // String result = super.toString();
    // result += " subject: " + String.valueOf(getSubjectKey()); //$NON-NLS-1$
    // result += " object: " + String.valueOf(getObjectKey()); //$NON-NLS-1$
    // return result;
    //    }

    // /** Assumes keys are already escaped */
    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.SUBJECT + NameUtil.EQUALS + getSubjectKey() //
    // + NameUtil.AND //
    // + NameUtil.OBJECT + NameUtil.EQUALS + getObjectKey() //
    // + NameUtil.AND //
    // + NameUtil.CREATOR + NameUtil.EQUALS + getCreatorKey();
    // }

    //
    //

    @VisibleJoin(value = Class.class, name = SUBJECT)
    @WizardField(type = WizardField.Type.REQUIRED, position = 2)
    public ExternalKey getSubjectKey() {
        return this.subjectKey;
    }

    public void setSubjectKey(ExternalKey subjectKey) {
        this.subjectKey = subjectKey;
    }

    @VisibleJoin(value = Class.class, name = OBJECT)
    @WizardField(type = WizardField.Type.REQUIRED, position = 3)
    public ExternalKey getObjectKey() {
        return this.objectKey;
    }

    public void setObjectKey(ExternalKey objectKey) {
        this.objectKey = objectKey;
    }
}
