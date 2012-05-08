/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import com.joelsgarage.model.User;

/**
 * When a user does something that other users might be interested in, we use this interface to
 * notify anyone interested in the update. For example, if a user creates an alternative, then all
 * the stakeholders in all the decisions of that class would be notified. For now, the update source
 * (elsewhere), brute-force scans everything.
 * 
 * The reason to have this listener interface is that the update source can be made more efficient
 * than the current scan-everything source. So I can change the source without changing the
 * listener.
 * 
 * @author joel
 */
public interface UpdateListener {
    /** The specified user may have new qualifying scenes. */
    public void update(User user);
}
