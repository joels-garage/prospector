/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author joel
 */
public class BoundedPriorityQueue<T> implements Serializable, Iterable<T>, Collection<T>, Queue<T> {
    private static final long serialVersionUID = 1L;
    private final PriorityQueue<T> pq;
    private final int maxCapacity;

    public BoundedPriorityQueue(int maxCapacity, Comparator<? super T> c) {
        this.pq = new PriorityQueue<T>(maxCapacity, c);
        this.maxCapacity = maxCapacity;
    }

    public boolean add(T e) {
        return offer(e);
    }

    public boolean addAll(Collection<? extends T> c) {
        boolean result = false;
        for (T t : c) {
            result = getPq().add(t) && result;
        }
        return result;
    }

    public void clear() {
        getPq().clear();
    }

    public Comparator<? super T> comparator() {
        return getPq().comparator();
    }

    public boolean contains(Object o) {
        return getPq().contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return getPq().containsAll(c);
    }

    public T element() {
        return getPq().element();
    }

    @Override
    public boolean equals(Object obj) {
        return getPq().equals(obj);
    }

    @Override
    public int hashCode() {
        return getPq().hashCode();
    }

    public boolean isEmpty() {
        return getPq().isEmpty();
    }

    public Iterator<T> iterator() {
        return getPq().iterator();
    }

    public boolean offer(T e) {
        if (getPq().size() < getMaxCapacity())
            return getPq().add(e);
        if (getPq().comparator().compare(e, getPq().peek()) < 0)
            return true;
        getPq().poll();
        getPq().add(e);
        return true;
    }

    public T peek() {
        return getPq().peek();
    }

    public T poll() {
        return getPq().poll();
    }

    public T remove() {
        return getPq().remove();
    }

    public boolean remove(Object o) {
        return getPq().remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return getPq().removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return getPq().retainAll(c);
    }

    public int size() {
        return getPq().size();
    }

    public Object[] toArray() {
        return getPq().toArray();
    }

    public <S> S[] toArray(S[] a) {
        return getPq().toArray(a);
    }

    @Override
    public String toString() {
        return getPq().toString();
    }

    //
    //

    public PriorityQueue<T> getPq() {
        return this.pq;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }
}
