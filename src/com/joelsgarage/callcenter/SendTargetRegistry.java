/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

/**
 * Used by SendTargets to register the "send" payload names they want to see.
 * 
 * @author joel
 * 
 */
public interface SendTargetRegistry {
    /** Register the specified sendTarget to receive payloads of the specified name */
    public void registerSendTarget(String name, SendTarget sendTarget);
}
