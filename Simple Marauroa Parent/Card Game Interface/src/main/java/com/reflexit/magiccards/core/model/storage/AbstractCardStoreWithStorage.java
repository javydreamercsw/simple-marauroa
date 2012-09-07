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

import java.util.Collection;
import java.util.Iterator;

/**
 * @param <T>
 * @author Alena
 *
 */
public abstract class AbstractCardStoreWithStorage<T>
        extends AbstractCardStore<T> implements ICardStore<T>,
        IStorageContainer<T> {

    protected IStorage<T> storage;
    protected boolean wrapped;

    /**
     *
     * @param storage
     * @param wrapped
     */
    public AbstractCardStoreWithStorage(final IStorage<T> storage,
            boolean wrapped) {
        super();
        this.storage = storage;
        this.wrapped = wrapped;
    }

    @Override
    public IStorage<T> getStorage() {
        return storage;
    }

    @Override
    protected synchronized boolean doAddAll(Collection<? extends T> list) {
        if (wrapped) {
            return super.doAddAll(list);
        } else {
            return storage.addAll(list);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return storage.iterator();
    }

    @Override
    public boolean doRemoveAll() {
        if (wrapped) {
            return super.doRemoveAll();
        } else {
            return storage.removeAll();
        }
    }

    @Override
    public boolean doRemoveAll(Collection<? extends T> list) {
        if (wrapped) {
            return super.doRemoveAll(list);
        } else {
            return storage.removeAll(list);
        }
    }

    @Override
    public int size() {
        return storage.size();
    }

    @Override
    protected boolean doAddCard(T card) {
        return storage.add(card);
    }

    @Override
    protected void doInitialize() throws Exception {
        storage.load();
    }

    @Override
    protected boolean doRemoveCard(T card) {
        return storage.remove(card);
    }

    @Override
    public String getComment() {
        return storage.getComment();
    }

    @Override
    public String getName() {
        return storage.getName();
    }

    @Override
    public boolean isVirtual() {
        return storage.isVirtual();
    }

    public boolean contains(T card) {
        return storage.contains(card);
    }

    @Override
    public boolean remove(T o) {
        return storage.remove(o);
    }

    @Override
    public boolean add(T card) {
        return storage.add(card);
    }
}