/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.joelsgarage.util.XMLUtil;

/**
 * @author joel
 * 
 */
public class FakeEngineManagerListener implements EngineManager.Listener {
    Vector<String> targets;
    Vector<String> targetTypes;
    Vector<Map<String, Node>> datas;

    public FakeEngineManagerListener() {
        this.targets = new Vector<String>();
        this.targetTypes = new Vector<String>();
        this.datas = new Vector<Map<String, Node>>();
    }

    @Override
    public void handleSend(String target, String targetType, Map<String, Node> data) {
        Logger.getLogger(FakeEngineManagerListener.class).info(
            "got send target: " + String.valueOf(target) //$NON-NLS-1$
                + " targettype: " + String.valueOf(targetType)); //$NON-NLS-1$
        if (data != null) {
            Logger.getLogger(FakeEngineManagerListener.class).info(" data size: " + data.size()); //$NON-NLS-1$
            for (Map.Entry<String, Node> entry : data.entrySet()) {
                Logger.getLogger(FakeEngineManagerListener.class).info("key: " + entry.getKey()); //$NON-NLS-1$
                Logger.getLogger(FakeEngineManagerListener.class).info(
                    "value: " + XMLUtil.writeXML(entry.getValue())); //$NON-NLS-1$

            }
        }
        this.targets.add(target);
        this.targetTypes.add(targetType);
        this.datas.add(data);
    }

    //

    public Vector<String> getTargets() {
        return this.targets;
    }

    protected void setTargets(Vector<String> targets) {
        this.targets = targets;
    }

    public Vector<String> getTargetTypes() {
        return this.targetTypes;
    }

    protected void setTargetTypes(Vector<String> targetTypes) {
        this.targetTypes = targetTypes;
    }

    public Vector<Map<String, Node>> getDatas() {
        return this.datas;
    }

    protected void setDatas(Vector<Map<String, Node>> datas) {
        this.datas = datas;
    }
}