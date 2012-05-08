/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.Map;

import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * Associates an entity with an Update.
 * 
 * @author joel
 */
@VisibleType(ExternalKey.LOG_TYPE)
public final class Log extends ModelEntity {
    public static final String ENTITY = "entity"; //$NON-NLS-1$
    public static final String WRITE_EVENT = "write_event"; //$NON-NLS-1$
    /** The entity updated; refers to any ModelEntity type. */
    private ExternalKey entity = new ExternalKey();
    /** The update; refers to Update type. */
    private ExternalKey writeEvent = new ExternalKey();

    protected Log() {
        // don't use this
    }

    /**
     * Hash incorporates entity referent, update record, and type and namespace.
     * 
     * @param ent
     * @param upd
     * @param namespace
     * @return
     * @throws FatalException
     */
    public Log(ExternalKey ent, ExternalKey upd, String namespace) throws FatalException {
        super(namespace);
        setEntity(ent);
        setWriteEvent(upd);
        populateKey();
    }

    @Override
    protected void populateKey(KeyUtil u) {
        super.populateKey(u);
        u.update(getEntity());
        u.update(getWriteEvent());
    }

    @Override
    public void fromMap(Map<String, String> input) {
        super.fromMap(input);
        setEntity(new ExternalKey(input.get(ENTITY)));
        setWriteEvent(new ExternalKey(input.get(WRITE_EVENT)));
    }

    @Override
    public void toMap(Map<String, String> output) {
        super.toMap(output);
        output.put(ENTITY, String.valueOf(getEntity()));
        output.put(WRITE_EVENT, String.valueOf(getWriteEvent()));
    }

    // @Override
    // public String toString() {
    // String result = super.toString();
    // result += " entity: " + String.valueOf(getEntity()) + //$NON-NLS-1$
    // " update: " + String.valueOf(getWriteEvent()); //$NON-NLS-1$
    // return result;
    //    }

    // @Override
    // public String compositeKeyKey() {
    // return NameUtil.REFERENT + NameUtil.EQUALS + getEnt().toString() //
    // + NameUtil.AND //
    // + NameUtil.OBJECT + NameUtil.EQUALS + getUpd().toString();
    // }

    //

    public ExternalKey getEntity() {
        return this.entity;
    }

    public void setEntity(ExternalKey ent) {
        this.entity = ent;
    }

    public ExternalKey getWriteEvent() {
        return this.writeEvent;
    }

    public void setWriteEvent(ExternalKey upd) {
        this.writeEvent = upd;
    }

}
