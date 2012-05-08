/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.joelsgarage.util.DowserFactory;
import com.joelsgarage.util.FatalException;
import com.joelsgarage.util.KeyUtil;
import com.joelsgarage.util.VisibleType;

/**
 * Denotes something in the DB; a row.
 * 
 * Every class in this hierarchy must be either abstract or final, otherwise polymorphic
 * queries-of-death may result.
 * 
 * @author joel
 * 
 */
@VisibleType(ExternalKey.MODEL_ENTITY_TYPE)
public abstract class ModelEntity {
    // public static final String NAME = "name"; //$NON-NLS-1$
    public static final String NAMESPACE = "namespace"; //$NON-NLS-1$
    public static final String ID = "id"; //$NON-NLS-1$

    /**
     * Namespace of this entity. It's no longer a key component. It is not necessary for lookups;
     * the key hash input includes the namespace. It's here so that we can filter by namespace at
     * load time.
     */
    private String namespace;

    /**
     * Fetches should be done with this key alone. Note this is a BINARY key, 12 bytes.
     * 
     * old comments:
     * 
     * Hibernate uses this id; all the subclasses (i.e. all the datatypes) inherit it. It is now
     * only used to hook the table hierarchy together, and is otherwise invisible to the
     * application.
     * 
     * And now it's an ExternalKey, not a Long. Probably this will tank join performance, so, TODO:
     * make this a hash or something.
     * 
     * For external entities, this is a unique key. It could be the URL of the external entity, but
     * it could also be anything else derivable from the source data, with these properties:
     * <ul>
     * <li>Unique within the namespace
     * <li>Stable between refreshes
     * </ul>
     * So, for the entity representing the individual, me, on LinkedIn, the externalKey is
     * www.linkedin.com/pub/0/17/734. It's just the URL, because LinkedIn provides URLs that
     * correspond to individuals, and once published, the pressure to make these URLs stable seems
     * reliable. For most purposes, this key is opaque. The only part of the system that understands
     * what's inside is the loader that constructs it.
     * 
     * See http://sites.google.com/a/joelsgarage.com/wik/Home/design/keys-for-loading-and-serving
     * 
     * All entity references (e.g. the subject of a fact) refer to this key.
     */
    private byte[] id;

    protected ModelEntity() {
        // foo
    }

    protected ModelEntity(String namespace) {
        setNamespace(namespace);
    }

    /**
     * Serialization. All entities should trivially map to flat files (imagine that!). If somehow I
     * don't like flat file mapping I could try protobuffers, but if I don't need it, then flat
     * files seems like a better compromise, i.e. avoids duplicating the datamodel in protos, can
     * use tools like grep directly, is only slightly bigger (30%).
     * 
     * Could use reflection for this, but boy i'm tired of annotation and reflection.
     * 
     * Anyway this contains the serialized form of the key.
     */
    public void toMap(Map<String, String> output) {
        if (output == null)
            return;
        output.put(NAMESPACE, getNamespace());
        output.put(ID, KeyUtil.encode(getId()));
    }

    /**
     * Serialization. Could use reflection for this but boy i'm tired of annotation and reflection.
     */
    public void fromMap(Map<String, String> input) {
        if (input == null)
            return;
        setNamespace(input.get(NAMESPACE));
        setId(KeyUtil.decodeKeyToBytes(input.get(ID)));
    }

    /**
     * Return an instance of the specified type, populated with the specified map. This is here
     * because I don't want other classes to use the ctor. Could also put the class name in the map
     * itself, but it's not a column in the file, so that's kinda weird.
     * 
     * @param input
     */
    public static <T extends ModelEntity> T newInstanceFromMap(java.lang.Class<T> specifiedClass,
        Map<String, String> input) {
        try {
            T result = specifiedClass.newInstance();
            result.fromMap(input);
            return result;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Each subclass overrides this and must call the super version to populate the keyutil hash.
     * 
     * @throws FatalException
     *             if the reflection used to get the keyfields fails
     */
    protected void populateKey(KeyUtil u) {
        u.update(getNamespace());
    }

    /**
     * This is the one the public uses
     * 
     * @throws FatalException
     *             if KeyUtil is misconfigured
     */
    public void populateKey() throws FatalException {
        KeyUtil u = new KeyUtil(this.getClass());
        populateKey(u);
        setId(u.get());
    }

    /** TODO: this is retarded. replace it with a simple "typeName" method */
    public ExternalKey makeKey() {
        return new ExternalKey(DowserFactory.newDowser().getTypeNames().get(this.getClass()),
            getId());
    }

    /**
     * @see Object#toString()
     */
    @Override
    public final String toString() {
        try {
            Map<String, String> output = new HashMap<String, String>();
            toMap(output);
            List<String> keys = new ArrayList<String>(output.keySet());
            Collections.sort(keys);
            JSONObject jo = new JSONObject();
            for (String key : keys) {
                jo.put(key, output.get(key));
            }
            return jo.toString(2);
        } catch (JSONException e) {
            // TODO: push this up as an exception?
            Logger.getLogger(ModelEntity.class).error("serialization error: " + e.getMessage()); //$NON-NLS-1$
            return new String();
        }
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public byte[] getId() {
        return this.id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }
}
