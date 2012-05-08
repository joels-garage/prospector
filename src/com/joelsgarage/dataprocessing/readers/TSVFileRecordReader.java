/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.readers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.joelsgarage.dataprocessing.TSVUtil;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.StreamFactory;

/**
 * given a file base name, reads from <filebasename>.<shortclassname>
 * 
 * first line is the name of the fields in each column.
 * 
 * tabs inside fields are escaped as \t.
 * 
 * somehow, find all the files matching the base name.
 * 
 * or, just "know" the types to read, and read only those.
 * 
 * 
 * @author joel
 * 
 */
public class TSVFileRecordReader extends RecordReaderBase<ModelEntity> {
    private StreamFactory streamFactory;
    private String basename;
    private Set<Class<? extends ModelEntity>> typesToRead =
        new HashSet<Class<? extends ModelEntity>>();
    private Iterator<Class<? extends ModelEntity>> typeIterator;
    // thanks java
    private Class<? extends ModelEntity> currentType;
    private Map<Class<?>, List<String>> headers = new HashMap<Class<?>, List<String>>();
    private Map<Class<?>, BufferedReader> inputs = new HashMap<Class<?>, BufferedReader>();

    public TSVFileRecordReader(StreamFactory streamFactory, String basename,
        Collection<Class<? extends ModelEntity>> typesToRead) {
        this.streamFactory = streamFactory;
        this.basename = basename;
        this.typesToRead.addAll(typesToRead);
    }

    @Override
    public void close() {
        try {
            for (BufferedReader input : this.inputs.values()) {
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open all the files, read all the headers. Opening all the streams first means fast failure,
     * rather than failing in the middle of a long job. And it's not that many streams, so OK to
     * keep a tiny bit of state about the streams.
     */
    @Override
    public void open() {
        // open all the streams
        for (Class<?> type : this.typesToRead) {
            try {
                InputStream inputStream =
                    this.streamFactory.getStreamForInput(TSVUtil.makeFileName(this.basename, type));
                if (inputStream == null) {
                    Logger.getLogger(TSVFileRecordReader.class).info(
                        "Ignoring missing type: " + type.getName()); //$NON-NLS-1$
                    continue;
                }
                BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(inputStream));
                this.inputs.put(type, bufferedReader);
                // The first line is the field list.
                String header = bufferedReader.readLine();
                List<String> headerRow = TSVUtil.fromTSV(header);
                this.headers.put(type, headerRow);
            } catch (FileNotFoundException e) {
                Logger.getLogger(TSVFileRecordReader.class).info(
                    "Ignoring missing type: " + type.getName()); //$NON-NLS-1$
                e.printStackTrace();
            } catch (IOException e) {
                Logger.getLogger(TSVFileRecordReader.class).info(
                    "Ignoring missing type: " + type.getName()); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
        this.typeIterator = this.typesToRead.iterator();
        if (this.typeIterator.hasNext()) {
            this.currentType = this.typeIterator.next();
        } else {
            Logger.getLogger(TSVFileRecordReader.class).error("No types?"); //$NON-NLS-1$
        }
    }

    @Override
    public ModelEntity read() {
        if (this.currentType == null)
            return null;
        try {
            BufferedReader bufferedReader = this.inputs.get(this.currentType);
            if (bufferedReader == null) {
                // there's no file for this desired input; press on
                if (this.typeIterator.hasNext()) {
                    this.currentType = this.typeIterator.next();
                    return read();
                }
                this.currentType = null;
                return null;
            }
            String dataRow = bufferedReader.readLine();
            if (dataRow == null) {
                // no more to read in this stream; is there a next stream?
                if (this.typeIterator.hasNext()) {
                    this.currentType = this.typeIterator.next();
                    return read();
                }
                this.currentType = null;
                return null;
            }
            Map<String, String> input = new HashMap<String, String>();
            List<String> header = this.headers.get(this.currentType);
            List<String> values = TSVUtil.fromTSV(dataRow);
            for (int index = 0; index < header.size(); ++index) {
                input.put(header.get(index), values.get(index));
            }
            return ModelEntity.newInstanceFromMap(this.currentType, input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
