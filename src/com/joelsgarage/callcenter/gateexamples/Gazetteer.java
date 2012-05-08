package com.joelsgarage.callcenter.gateexamples;

import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.gazetteer.GazetteerList;
import gate.creole.gazetteer.GazetteerNode;
import gate.creole.gazetteer.LinearNode;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class does nothing but provide a separate spot in CreoleRegister's ProcessingResource index.
 * 
 * Note, it used to be a nested class, but the yummy Gate class loader couldn't find it there for
 * some reason, so I pulled it out here.
 * 
 * @author joel
 */
public class Gazetteer extends DefaultGazetteer {
    private static final long serialVersionUID = 1L;

    /** Map<LinearNode, GazetteerList> */
    @SuppressWarnings("unchecked")
    public Map getListsByNode() {
        return this.listsByNode;
    }
    
    @SuppressWarnings("unchecked")
    public void logAllLists() {
        Collection keys = getListsByNode().keySet();
        for (Object keyO : keys) {
            Logger.getLogger(Gazetteer.class).info("start"); //$NON-NLS-1$
            if (keyO instanceof LinearNode) {
                LinearNode ln = (LinearNode) keyO;
                Logger.getLogger(Gazetteer.class).info("node: " + ln.getMajorType()); //$NON-NLS-1$
                Object o = getListsByNode().get(keyO);
                if (o instanceof GazetteerList) {
                    GazetteerList g = (GazetteerList) o;
                    Iterator iter = g.iterator();
                    while (iter.hasNext()) {
                        Object nodeO = iter.next();
                        if (nodeO instanceof GazetteerNode) {
                            GazetteerNode gn = (GazetteerNode) nodeO;
                            Logger.getLogger(Gazetteer.class).info("entry: " + gn.getEntry()); //$NON-NLS-1$
                            Map featureMap = gn.getFeatureMap();
                            if (featureMap == null) {
                                Logger.getLogger(Gazetteer.class).info("null featureMap"); //$NON-NLS-1$
                                continue;
                            }
                            for (Object mapKeyO : featureMap.keySet()) {
                                if (mapKeyO instanceof String) {
                                    String mapKey = (String) mapKeyO;
                                    Logger.getLogger(Gazetteer.class).info("key: " + mapKey // //$NON-NLS-1$
                                        + " value: " + featureMap.get(mapKeyO)); //$NON-NLS-1$
                                } else {
                                    Logger.getLogger(Gazetteer.class).info(
                                        "weird key type: " + mapKeyO.getClass().getName()); //$NON-NLS-1$
                                }
                            }
                        } else {
                            Logger.getLogger(Gazetteer.class).info(
                                "weird node type: " + nodeO.getClass().getName()); //$NON-NLS-1$
                        }
                    }
                } else {
                    Logger.getLogger(Gazetteer.class).info(
                        "weird list type: " + o.getClass().getName()); //$NON-NLS-1$
                }
            } else {
                Logger.getLogger(Gazetteer.class).info(
                    "weird key type: " + keyO.getClass().getName()); //$NON-NLS-1$
            }
            Logger.getLogger(Gazetteer.class).info("done"); //$NON-NLS-1$
        }
        Logger.getLogger(Gazetteer.class).info("done"); //$NON-NLS-1$
    }
}
