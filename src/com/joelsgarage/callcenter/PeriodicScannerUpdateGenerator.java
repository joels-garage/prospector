/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.InitializationException;
import com.joelsgarage.dataprocessing.ReaderConstraint;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordReaderFactory;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.User;

/**
 * Periocially scans the database and produces UpdateListener updates for every user.
 * 
 * @author joel
 */
public class PeriodicScannerUpdateGenerator extends TimerTask {
    /** How many ms to wait before starting to scan. */
    private static final long delay = 2000;
    /** How many ms to wait between scans. */
    private static final long period = 10000;
    private RecordReaderFactory<ModelEntity> factory;
    private UpdateListener listener;
    private Timer timer;

    public PeriodicScannerUpdateGenerator(RecordReaderFactory<ModelEntity> factory,
        UpdateListener listener) {
        setFactory(factory);
        setListener(listener);
        setTimer(new Timer());
    }

    public void start() {
        getTimer().scheduleAtFixedRate(this, delay, period);
    }

    /**
     * Obtain a reader and scan it, updating for each record.
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        Logger.getLogger(PeriodicScannerUpdateGenerator.class).info("run"); //$NON-NLS-1$

        if (getListener() == null) {
            Logger.getLogger(PeriodicScannerUpdateGenerator.class).error("no listener."); //$NON-NLS-1$
        }
        ReaderConstraint constraint = new ReaderConstraint(User.class);
        RecordReader<ModelEntity> reader = getFactory().newInstance(constraint);
        try {
            reader.open();

            ModelEntity modelEntity;
            while ((modelEntity = reader.read()) != null) {
                if (modelEntity instanceof User) {
                    User user = (User) modelEntity;
                    Logger.getLogger(PeriodicScannerUpdateGenerator.class).info(
                        "updating user: " + user.getEmailAddress()); //$NON-NLS-1$
                    getListener().update(user);
                } else {
                    Logger.getLogger(PeriodicScannerUpdateGenerator.class).error(
                        "weird type: " + modelEntity.getClass().getName()); //$NON-NLS-1$
                }
            }
        } catch (InitializationException e) {
            Logger.getLogger(PeriodicScannerUpdateGenerator.class).error(
                "reader open failed: " + e.getMessage()); //$NON-NLS-1$
        }
        reader.close();
    }

    //
    //
    //

    public Timer getTimer() {
        return this.timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public UpdateListener getListener() {
        return this.listener;
    }

    public void setListener(UpdateListener listener) {
        this.listener = listener;
    }

    public RecordReaderFactory<ModelEntity> getFactory() {
        return this.factory;
    }

    public void setFactory(RecordReaderFactory<ModelEntity> factory) {
        this.factory = factory;
    }
}
