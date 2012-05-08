/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * Transforms input records to output records.
 * 
 * A ProcessNode uses a reader to obtain records of type T, and then uses a writer to produce
 * records of type U.
 * 
 * So for example, a reader or writer might work with a file of a specific type, or the database.
 * 
 * TODO: make this a special case of a MultiReaderProcessNode.
 * 
 * @author joel
 * 
 */
public abstract class ProcessNode<T, U> {
    /** I don't think this needs to be big but it doesn't hurt */
    private static final int WRITE_QUEUE = 100000;
    /** The input */
    private RecordReader<T> reader;
    /** The output */
    private RecordWriter<U> writer;

    /** How many records we have read */
    private int inCount = 0;
    /** How many records we have written */
    private int outCount = 0;
    /** The maximum number of input records to read */
    private int inLimit = 0;
    /** The maximum number of output records to write */
    private int outLimit = 0;
    /** Log every N records */
    private int progressCount = 100;
    /** A convenience timer */
    private long time;

    private WriteQueue<U> q;

    private Thread writerThread;

    public static class WriteQueue<U> implements Runnable {
        public BlockingQueue<U> q;
        private RecordWriter<U> w;

        public WriteQueue(BlockingQueue<U> q, RecordWriter<U> w) {
            this.q = q;
            this.w = w;
        }

        /** Writes the contents of the queue until interrupted, then drains the queue and exits */
        public void run() {
            try {
                while (true) {
                    this.w.write(this.q.take());
                }
            } catch (InterruptedException e) {
                // interrupt when there are no more to add; now just write out the contents and
                // finish.
                Logger.getLogger(ProcessNode.class).info("Interrupted as expected"); //$NON-NLS-1$
            }
            try {
                // try drain?
                Logger.getLogger(ProcessNode.class).info("Draining... "); //$NON-NLS-1$
                int progressSize = this.q.size() / 10 + 1;
                while (!(this.q.isEmpty())) {
                    if ((this.q.size() % progressSize) == 0) {
                        Logger.getLogger(ProcessNode.class).info("queue size: " + this.q.size()); //$NON-NLS-1$
                    }
                    this.w.write(this.q.take());
                }
                Logger.getLogger(ProcessNode.class).info("... done!"); //$NON-NLS-1$
            } catch (InterruptedException e) {
                Logger.getLogger(ProcessNode.class).error("Interrupted again"); //$NON-NLS-1$
                e.printStackTrace();
            }
        }
    }

    /**
     * Construct a ProcessNode with the specified reader, writer, and limits.
     * 
     * @param reader
     *            produces input records
     * @param writer
     *            consumes output records
     * @param inLimit
     *            stop after consuming this many input records zero == no limit
     * @param outLimit
     *            stop after producing this many output records. zero == no limit
     * 
     */
    public ProcessNode(RecordReader<T> reader, RecordWriter<U> writer, int inLimit, int outLimit) {
        setReader(reader);
        setWriter(writer);
        setInLimit(inLimit);
        setOutLimit(outLimit);
        this.q = new WriteQueue<U>(new ArrayBlockingQueue<U>(WRITE_QUEUE), writer);
    }

    /**
     * Read records until handleRecord() has had enough, or we hit the input or output limit.
     */
    public void run() {
        Logger.getLogger(ProcessNode.class).info(//
            "run with inlimit: " + getInLimit() // //$NON-NLS-1$
                + "  outlimit: " + getOutLimit()); //$NON-NLS-1$
        open();
        start();
        work();
        done();
        close();
    }

    /**
     * Alternatively, clients can regulate their own reader/writer opening/closing by calling the
     * sequence, open, start, work, done, close, on their own. If you know what you're doing. For
     * example, if the readers and writers are opened and closed outside the scope of the node, you
     * don't need to call open() and close() at all. and if your node doesn't implement start() and
     * done(), you obviously don't need to call them. :-)
     */

    public void work() {
        T currentRecord = null;
        while (true) {
            if (getInLimit() > 0 && getInCount() >= getInLimit())
                break;
            if (getOutLimit() > 0 && getOutCount() >= getOutLimit())
                break;
            currentRecord = input();
            if (currentRecord == null)
                break;
            if (!handleRecord(currentRecord))
                break;
        }
    }

    /** Call this before you call work(). */
    public void open() {
        try {
            if (getReader() != null)
                getReader().open();
        } catch (InitializationException ex) {
            ex.printStackTrace();
            return;
        }
        // TODO: move this to the queue
        if (getWriter() != null)
            getWriter().open();

        this.writerThread = new Thread(this.q);
        this.writerThread.start();
    }

    /** Call this after you call work(). */
    public void close() {
        if (getReader() != null)
            getReader().close();

        if (this.writerThread != null) {
            Logger.getLogger(ProcessNode.class).info(
                "Draining queue: " + String.valueOf(this.q.q.size())); //$NON-NLS-1$
            this.writerThread.interrupt();
            try {
                this.writerThread.join();
                Logger.getLogger(ProcessNode.class).info(
                    "... Done draining: " + String.valueOf(this.q.q.size())); //$NON-NLS-1$

            } catch (InterruptedException e) {
                Logger.getLogger(ProcessNode.class).error("Interrupted while draining"); //$NON-NLS-1$
                e.printStackTrace();
            }
        }

        if (getWriter() != null)
            getWriter().close();
    }

    /**
     * Called before the first record. Use this to do whatever setup you need.
     */
    protected void start() {
        // default does nothing
    }

    /**
     * Called once for each record. return true if you want more records.
     */
    protected abstract boolean handleRecord(T record);

    /**
     * Obtain a record from the reader. Updates the input record count.
     * 
     * @return the record, or null if there are no more records.
     */
    protected T input() {
        T inputRecord = getReader().read();
        if (inputRecord == null)
            return null;
        setInCount(getInCount() + 1);
        if (getProgressCount() > 0 && getInCount() % getProgressCount() == 0) {
            doProgress();
        }
        return inputRecord;
    }

    protected synchronized void doProgress() {
        Logger.getLogger(ProcessNode.class).info("input records: " + getInCount()); //$NON-NLS-1$
        long newTime = System.currentTimeMillis();
        Logger.getLogger(ProcessNode.class).info(
            "duration: " + String.valueOf(((double) newTime - (double) getTime()) / 1000)); //$NON-NLS-1$
        setTime(newTime);
        Logger.getLogger(ProcessNode.class).info("write queue: " + String.valueOf(this.q.q.size())); //$NON-NLS-1$
    }

    /**
     * Output a row to the writer. Updates the output record count. Probably don't need to override
     * this.
     */
    protected void output(U payload) {
        incrementOutCount();
        try {
            // this uses ReentrantLock internally, so not synchronized.
            if (!this.q.q.offer(payload, 1, TimeUnit.HOURS)) {
                Logger.getLogger(ProcessNode.class).error("failed to write!"); //$NON-NLS-1$
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected synchronized void incrementOutCount() {
        setOutCount(getOutCount() + 1);
    }

    /**
     * Called after the last (in the file, or because we exceeded the configured number of records)
     * record. Use this to flush aggregates, or whatever.
     */
    protected void done() {
        // default does nothing
    }

    protected RecordReader<T> getReader() {
        return this.reader;
    }

    public void setReader(RecordReader<T> reader) {
        this.reader = reader;
    }

    protected RecordWriter<U> getWriter() {
        return this.writer;
    }

    protected void setWriter(RecordWriter<U> writer) {
        this.writer = writer;
    }

    /**
     * Input record count.
     * 
     * @return how many times input() produced a non-null return value.
     */
    public int getInCount() {
        return this.inCount;
    }

    protected void setInCount(int inCount) {
        this.inCount = inCount;
    }

    /**
     * Output record count.
     * 
     * @return how many times output() has been called.
     */
    public int getOutCount() {
        return this.outCount;
    }

    protected void setOutCount(int outCount) {
        this.outCount = outCount;
    }

    protected int getInLimit() {
        return this.inLimit;
    }

    protected void setInLimit(int inLimit) {
        this.inLimit = inLimit;
    }

    protected int getOutLimit() {
        return this.outLimit;
    }

    protected void setOutLimit(int outLimit) {
        this.outLimit = outLimit;
    }

    protected int getProgressCount() {
        return this.progressCount;
    }

    /**
     * We log progress every N input records. This is N.
     * 
     * @param progressCount
     */
    public void setProgressCount(int progressCount) {
        this.progressCount = progressCount;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
