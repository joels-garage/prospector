/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.fetchers;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.joelsgarage.model.ExternalKey;
import com.joelsgarage.model.ModelEntity;

/**
 * @author joel
 * 
 */
public class ModelEntityMapRecordFetcher extends MapRecordFetcher<ExternalKey, ModelEntity> {
    public ModelEntityMapRecordFetcher(Map<ExternalKey, ModelEntity> data) {
        super(data);
        for (Map.Entry<ExternalKey, ModelEntity> entry : data.entrySet()) {
            Logger.getLogger(ModelEntityMapRecordFetcher.class).info(
                entry.getKey().toString() + " : " + entry.getValue().toString()); //$NON-NLS-1$
        }
    }

    /** Find the specified key by fieldname */
    @Override
    protected ExternalKey extractForeignKey(String fieldName, ModelEntity instance) {
        Map<String, String> output = new HashMap<String, String>();
        instance.toMap(output);
        return ExternalKey.newInstance(output.get(fieldName));
    }
}
