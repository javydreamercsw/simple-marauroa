/**
 * *****************************************************************************
 * Copyright (c) 2008 Alena Laskavaia. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alena Laskavaia - initial API and implementation
 * *****************************************************************************
 */
package com.reflexit.magiccards.core.model.storage;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * ArrayList based implementation for AbstractCardStore
 *
 * @param <T>
 * @author Alena
 *
 */
public class MemoryCardStorage<T> extends AbstractStorage<T> {

    protected final List<T> list = 
            Collections.synchronizedList(new ArrayList<T>());

    @Override
    public Iterator<T> iterator() {
        synchronized (list) {
            ArrayList x = new ArrayList(list);
            return x.iterator();
        }
    }

    @Override
    public int size() {
        return this.getList().size();
    }

    @Override
    public boolean doRemoveCard(T card) {
        return this.getList().remove(card);
    }

    @Override
    public boolean doAddCard(T card) {
        return this.getList().add(card);
    }

    /**
     * Update card.
     *
     * @param card card to update
     * @return true if updated.
     */
    protected boolean doUpdate(T card) {
        return true;
    }

    /**
     * @return the list
     */
    public Collection<T> getList() {
        return this.list;
    }

    /**
     * Set list.
     *
     * @param list list to set
     */
    protected void doSetList(List<T> list) {
        list.clear();
        list.addAll(Collections.synchronizedList(list));
    }

    @Override
    public void clearCache() {
        list.clear();
        setLoaded(false);
    }

    @Override
    protected void doLoad() {
        // nothing
    }

    @Override
    protected void doSave() throws FileNotFoundException {
        // nothing
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getName() {
        return "mem";
    }

    @Override
    public boolean isVirtual() {
        return true;
    }

    @Override
    public boolean add(T card) {
        return list.add(card);
    }

    @Override
    public boolean addAll(Collection toAdd) {
        return list.addAll(toAdd);
    }

    @Override
    public boolean removeAll(Collection<? extends T> toRemove) {
        return list.removeAll(toRemove);
    }

    @Override
    public boolean contains(T card) {
        return list.contains(card);
    }

    @Override
    public boolean remove(T card) {
        return list.remove(card);
    }
}