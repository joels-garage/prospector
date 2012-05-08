/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * Represents a particular thing in the real world.
 * <p>
 * An individual may belong to any number of classes -- see ClassMember -- because it may be an
 * alternative in a number of different decisions, which use different facets of its identity.
 * Mostly, though, things will belong to a single class, I think.
 * <p>
 * Some types involve "canonical individuals," i.e. all canon S200 camera are close to identical,
 * and so can be described by the canonical individual of the class. For other types, specific
 * individuals are pretty important, e.g. cars. Some types have no canonical individual, e.g. a
 * canonical "campsite" is meaningless.
 * <p>
 * TODO: give this canonical individual topic some more thought.
 * <p>
 * There are a whole lot of individuals, something like 100B.
 * <p>
 * It is tempting to make a lot of things individuals. For example, a user comment could be cast as
 * an Individual of Class "Comment," with properties like "author" and "body text." But it would be
 * a bad idea -- Individuals and Classes exist as extensible things (rather than Java classes) so
 * that users can create new ones, and "comment" isn't like that. Perhaps "Individual" should be
 * renamed "Prospect" (after Kahneman/Tversky 79), to make its narrow scope more clear.
 * <p>
 * Examples
 * <ul>
 * <li>my particular volvo v70
 * <li>a specific campsite
 * </ul>
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.INDIVIDUAL_TYPE)
public final class Individual extends ModelEntity {
    public static final String NAME = "name"; //$NON-NLS-1$

    /** Used in key, not persisted -- oh wait now it is */
    private String name;

    protected Individual() {
        super();
    }

    /**
     * @param name --
     *            used in hash key but not stored -- oh wait yes it is
     * @param namespace
     * @return
     * @throws FatalException
     */
    public Individual(String name, String namespace) throws FatalException {
        super(namespace);
        setName(name);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getName());
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

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
