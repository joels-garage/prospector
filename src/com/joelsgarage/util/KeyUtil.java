/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Keys are constructed using a convention implemented here.
 * 
 * Now it's a one-shot thing; construct it, add to it, get the key, done. Dunno if that's slow.
 * 
 * @author joel
 * 
 */
public class KeyUtil {
    private static final String SHA_1 = "SHA-1"; //$NON-NLS-1$
    private MessageDigest md = null;

    public KeyUtil(Class<?> entityClass) throws FatalException {
        try {
            this.md = MessageDigest.getInstance(SHA_1);
            if (entityClass != null)
                this.md.update(entityClass.getClass().getName().getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new FatalException(e);
        }
    }

    /** Add additional content to the key */
    public void update(Object input) {
        if (input == null)
            return;
        this.md.update(String.valueOf(input).getBytes());
    }

    /**
     * Generate a key for use as an entity identifier.
     * 
     * TODO: expose the update() method; it's faster than string concatenation.
     * 
     * @param input
     *            any input reflective of entity identity.
     * @param entityClass
     *            class of the entity
     * @param namespace
     *            namespace of the entity
     * @return a 12-byte key, the 96 msb's of a sha-1 digest constructed from the inputs.
     */
    public byte[] get() {
        byte[] hash = this.md.digest();
        byte[] key = new byte[12];
        System.arraycopy(hash, 0, key, 0, 12);
        return key;
    }

    /** Express the binary key in base64, "URL-safe" encoding (i.e. without slashes) */
    public static String encode(byte[] source) {
        if (source == null)
            return new String();
        return Base64.encodeBytes(source, Base64.URL_SAFE);
    }

    /** Decode base64 serialized key to the byte array form */
    public static byte[] decodeKeyToBytes(String input) {
        return Base64.decode(input, Base64.URL_SAFE);
    }
}
