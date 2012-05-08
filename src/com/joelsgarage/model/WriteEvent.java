/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleJoin;
import com.joelsgarage.util.VisibleType;

/**
 * Represents an update event. Could cover one entity or a million. Referenced by Log.
 * 
 * This is a kind of intermediate form of this entity, using the old key structs.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.WRITE_EVENT_TYPE)
public class WriteEvent extends ModelEntity {
    public static final String TIME = "time"; //$NON-NLS-1$
    public static final String USER = "user"; //$NON-NLS-1$
    /**
     * This is the ISO 8601 extended-format (i.e. with hyphens and colons) representation, in
     * localtime (PDT at the moment) of the update event. Why 8601? So I can read it. It collates
     * correctly, which is all I care about (i.e. easy to select most-recent record)
     */
    private String time = new String();
    /**
     * The USER who originally created this entity.
     */
    private ExternalKey user = new ExternalKey();

    protected WriteEvent() {
        // no no
    }

    /**
     * @param tim
     *            keyfield
     * @param use
     *            keyfield
     * @param namespace
     * @throws FatalException
     *             if we can't populate the key
     */
    public WriteEvent(String tim, ExternalKey use, String namespace) throws FatalException {
        super(namespace);
        setTime(tim);
        setUser(use);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getTime());
        u.update(getUser());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setTime(input.get(TIME));
        setUser(new ExternalKey(input.get(USER)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(TIME, getTime());
        output.put(USER, String.valueOf(getUser()));
    }

    // @Override
    // public String toString() {
    // String result = super.toString();
    // result += " use: " + String.valueOf(getUser()) + //$NON-NLS-1$
    // " tim: " + String.valueOf(getTime()); //$NON-NLS-1$
    // return result;
    //    }

    // /** Assumes the user key is already escaped */
    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.USER + NameUtil.EQUALS + getUse() //
    // + NameUtil.AND //
    // + NameUtil.TIMESTAMP + NameUtil.EQUALS + NameUtil.encode(getTim());
    // }

    //
    //

    // DETERMINED

    public String getTime() {
        return this.time;
    }

    public void setTime(String tim) {
        this.time = tim;
    }

    // DETERMINED
    /** The USER who did this update. */

    @VisibleJoin(value = User.class, name = USER)
    public ExternalKey getUser() {
        return this.user;
    }

    /** The USER who originally created this entity. */
    public void setUser(ExternalKey use) {
        this.user = use;
    }
}
