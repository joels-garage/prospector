/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.dataprocessing.nodes;

import com.joelsgarage.dataprocessing.ProcessNode;
import com.joelsgarage.dataprocessing.RecordReader;
import com.joelsgarage.dataprocessing.RecordWriter;

/**
 * This is a process node with side inputs. Previously I tried a version of this idea with multiple
 * scanners, but realized that to actually make use of multiple scanning would require previous
 * sorting, and I didn't have that, nor did I need it. Instead, what I needed was one big scan and
 * several small tables. But the side input tables don't *really* have to be contained in memory;
 * they could just be cached lookups. So that's what this does; it does one main scan and provides
 * primary-key or foreign-key fetch access to cached side inputs. These side inputs are assumed to
 * be single-valued for keys used to fetch them; for "nested scan" kinds of things, the side input
 * should be expanded to return an iterator rather than an entity. But I don't need that yet.
 * 
 * TODO: support multivalued lookups, i.e. real inner join.
 * 
 * TODO: finish the implementation of this class.  :-)
 * 
 * @author joel
 * 
 * @param <K>
 *            key type for side inputs
 * @param <V>
 *            value type for side inputs
 * @param <T>
 *            value type for main scan input
 * @param <U>
 *            value type for output
 */
public abstract class SideInputProcessNode<K, V, T, U> extends ProcessNode<T, U> {

	public SideInputProcessNode(RecordReader<T> reader, RecordWriter<U> writer, int inLimit,
		int outLimit) {
		super(reader, writer, inLimit, outLimit);
	}

}
