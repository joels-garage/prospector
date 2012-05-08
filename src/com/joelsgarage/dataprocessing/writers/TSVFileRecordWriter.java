/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.writers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joelsgarage.dataprocessing.RecordWriter;
import com.joelsgarage.dataprocessing.TSVUtil;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.util.StreamFactory;

/**
 * given a file base name, writes to <filebasename>.<shortclassname>
 * 
 * first line is the name of the fields in each column.
 * 
 * tabs inside fields are escaped as \t.
 * 
 * Lazily opens files depending on the write payload.
 * 
 * @author joel
 * 
 */
public class TSVFileRecordWriter implements RecordWriter<ModelEntity> {
    private static final char NEWLINE = '\n';
    private StreamFactory streamFactory;
    private String basename;
    private Map<Class<?>, OutputStream> outputs = new HashMap<Class<?>, OutputStream>();
    /** Remember the header in order to guarantee a consistent field ordering */
    private Map<Class<?>, List<String>> headers = new HashMap<Class<?>, List<String>>();

    public TSVFileRecordWriter(StreamFactory streamFactory, String basename) {
        this.streamFactory = streamFactory;
        this.basename = basename;
    }

    @Override
    public void close() {
        try {
            for (OutputStream output : this.outputs.values()) {
                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void open() {
        // lazily open streams as we encounter types to write.
    }

    @Override
    public void write(ModelEntity payload) {
        Class<?> payloadClass = payload.getClass();
        OutputStream outputStream = this.outputs.get(payloadClass);
        List<String> headerRow = this.headers.get(payloadClass);
        try {
            if (outputStream == null) {
                outputStream = this.streamFactory.getStreamForOutput(makeFileName(payloadClass));
                if (outputStream == null)
                    return;
                this.outputs.put(payloadClass, outputStream);
                Map<String, String> payloadMap = new HashMap<String, String>();
                payload.toMap(payloadMap);
                headerRow = new ArrayList<String>(payloadMap.keySet());
                this.headers.put(payloadClass, headerRow);
                writeRow(outputStream, headerRow);
            }
            if (headerRow == null)
                return;
            Map<String, String> payloadMap = new HashMap<String, String>();
            payload.toMap(payloadMap);
            List<String> row = makeRow(headerRow, payloadMap);
            writeRow(outputStream, row);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // before i really knew about bufferedwriters
    protected static void writeRow(OutputStream outputStream, List<String> list) {
        try {
            outputStream.write(TSVUtil.toTSV(list).getBytes());
            outputStream.write(NEWLINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Selects the named fields from the map */
    protected static List<String> makeRow(List<String> names, Map<String, String> data) {
        List<String> result = new ArrayList<String>();
        for (String name : names) {
            result.add(data.get(name));
        }
        return result;
    }

    protected String makeFileName(Class<?> payloadClass) {
        return TSVUtil.makeFileName(this.basename, payloadClass);
    }
}
