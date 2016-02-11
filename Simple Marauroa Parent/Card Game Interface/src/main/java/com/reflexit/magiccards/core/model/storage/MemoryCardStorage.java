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

import com.reflexit.magiccards.core.model.ICardSet;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Map.Entry;

/**
 * ArrayList based implementation for AbstractCardStore
 *
 * @param <T>
 * @author Alena
 *
 */
public class MemoryCardStorage<T> extends AbstractStorage<T> {

    protected final Map<ICardSet, List<T>> memory
            = new HashMap<ICardSet, List<T>>();

    @Override
    public Iterator<T> iterator() {
        synchronized (memory) {
            ArrayList x = new ArrayList();
            for (Entry<ICardSet, List<T>> entry : memory.entrySet()) {
                x.addAll(entry.getValue());
            }
            return x.iterator();
        }
    }

    @Override
    public int size() {
        int size = 0;
        for (Entry<ICardSet, List<T>> entry : memory.entrySet()) {
            size += entry.getValue().size();
        }
        return size;
    }

    @Override
    public boolean doRemoveCard(T card, ICardSet set) {
        return this.memory.get(set).remove(card);
    }

    @Override
    public boolean doAddCard(T card, ICardSet set) {
        if (!memory.containsKey(set)) {
            memory.put(set, Collections.synchronizedList(new ArrayList<T>()));
        }
        return this.memory.get(set).add(card);
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
    public Collection<T> getList(ICardSet set) {
        return this.memory.get(set);
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
        memory.clear();
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
    public boolean add(T card, ICardSet set) {
        if (!memory.containsKey(set)) {
            memory.put(set, Collections.synchronizedList(new ArrayList<T>()));
        }
        return this.memory.get(set).add(card);
    }

    @Override
    public boolean addAll(Collection toAdd, ICardSet set) {
        if (!memory.containsKey(set)) {
            memory.put(set, Collections.synchronizedList(new ArrayList<T>()));
        }
        return this.memory.get(set).addAll(toAdd);
    }

    @Override
    public boolean removeAll(Collection<? extends T> toRemove, ICardSet set) {
        return this.memory.get(set).removeAll(toRemove);
    }

    @Override
    public boolean contains(T card, ICardSet set) {
        return this.memory.get(set).contains(card);
    }

    @Override
    public boolean remove(T card, ICardSet set) {
        return this.memory.get(set).remove(card);
    }

    public boolean contains(T card) {
        for (Entry<ICardSet, List<T>> entry : memory.entrySet()) {
            if (entry.getValue().contains(card)) {
                return true;
            }
        }
        return false;
    }
}
