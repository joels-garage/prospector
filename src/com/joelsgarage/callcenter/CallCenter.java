/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.User;

/**
 * CallCenter is the IM application. It goes into its own JAR.
 * 
 * The CallCenter simply manages Operators. The Operators manage their own state, including multiple
 * chats.
 * 
 * @author joel
 * 
 */
public class CallCenter implements UpdateListener {
    private static final String PROSPECTOR_DOMAIN = "joelsgarage.com"; //$NON-NLS-1$
    private static final String GOOGLE_TALK_HOSTNAME = "talk.google.com"; //$NON-NLS-1$
    private static final int GOOGLE_TALK_PORT = 5222;

    /**
     * The operator accounts we know about. There should only be one (global!) XMPP "login" per
     * username, thus per operator. So, each operator instance is configured with one of these
     * pairs.
     * 
     * I suppose that different operators could get different behaviors; for now they're copies of
     * each other.
     * 
     * <pre>
     * key: Username
     * value: Password
     * </pre>
     */
    @SuppressWarnings("nls")
    private final Map<String, String> operatorPasswords = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put("nigel", "ANiceGuy");
            put("squeaky", "AimToPlease");
        }
    };
    private final Map<String, Operator> operators = new HashMap<String, Operator>();
    RecordReaderFactory<ModelEntity> factory;
    private PeriodicScannerUpdateGenerator updateGenerator;

    public CallCenter(RecordReaderFactory<ModelEntity> factory) {
        setFactory(factory);
        XMPPConnection.DEBUG_ENABLED = true;
        ConnectionConfiguration conf =
            new ConnectionConfiguration(GOOGLE_TALK_HOSTNAME, GOOGLE_TALK_PORT, PROSPECTOR_DOMAIN);
        putOperator(conf, "nigel"); //$NON-NLS-1$
        putOperator(conf, "squeaky"); //$NON-NLS-1$
        setUpdateGenerator(new PeriodicScannerUpdateGenerator(getFactory(), this));
    }

    private void putOperator(ConnectionConfiguration conf, String username) {
        Operator newOperator = newOperator(conf, username);
        if (newOperator != null) {
            getOperators().put(username, newOperator);
        }
    }

    private Operator newOperator(ConnectionConfiguration conf, String username) {
        String password = getOperatorPasswords().get(username);
        if (password == null) {
            return null;
        }
        return new Operator(conf, username, password);
    }

    public void doWork() {
        for (Operator operator : getOperators().values()) {
            operator.start();
        }
        getUpdateGenerator().start();
    }

    /**
     * Update each operator with the specified user.
     * 
     * TODO: think about this multi-operator thing.
     * 
     * @see com.joelsgarage.callcenter.UpdateListener#update(com.joelsgarage.model.User)
     */
    @Override
    public void update(User user) {
        for (Operator operator : getOperators().values()) {
            operator.update(user);
        }
    }

    //
    //
    //

    public Map<String, String> getOperatorPasswords() {
        return this.operatorPasswords;
    }

    public Map<String, Operator> getOperators() {
        return this.operators;
    }

    public PeriodicScannerUpdateGenerator getUpdateGenerator() {
        return this.updateGenerator;
    }

    public void setUpdateGenerator(PeriodicScannerUpdateGenerator updateGenerator) {
        this.updateGenerator = updateGenerator;
    }

    public RecordReaderFactory<ModelEntity> getFactory() {
        return this.factory;
    }

    public void setFactory(RecordReaderFactory<ModelEntity> factory) {
        this.factory = factory;
    }
}
