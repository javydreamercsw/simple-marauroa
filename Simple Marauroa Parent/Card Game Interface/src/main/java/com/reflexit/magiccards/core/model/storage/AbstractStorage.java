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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <T>
 * @author Alena
 *
 */
public abstract class AbstractStorage<T> implements IStorage<T> {

    private boolean loaded = false;
    private boolean autocommit = true;
    private boolean needToSave = false;

    /**
     *
     */
    public AbstractStorage() {
        super();
    }

    @Override
    public synchronized void load() {
        if (isLoaded()) {
            return;
        }
        clearCache();
        setLoaded(true);
        doLoad();
    }

    public synchronized void reload() {
        setLoaded(false);
        load();
    }

    public synchronized void unload() {
        setLoaded(false);
        clearCache();
    }

    public abstract void clearCache();

    protected abstract void doLoad();

    @Override
    public synchronized void save() {
        try {
            doSave();
            setNeedToSave(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AbstractStorage.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean add(T card, ICardSet set) {
        load();
        boolean modified;
        synchronized (this) {
            modified = doAddCard(card, set);
            if (modified) {
                autoSave();
            }
        }
        return modified;
    }

    protected abstract void doSave() throws FileNotFoundException;

    @Override
    public boolean addAll(Collection<? extends T> list, ICardSet set) {
        load();
        boolean modified = false;
        synchronized (this) {
            for (T element : list) {
                if (doAddCard(element, set)) {
                    modified = true;
                }
            }
            if (modified) {
                autoSave();
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<? extends T> list, ICardSet set) {
        load();
        boolean modified = false;
        synchronized (this) {
            for (Object element : list) {
                if (doRemoveCard((T) element, set)) {
                    modified = true;
                }
            }
            if (modified) {
                autoSave();
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(ICardSet set) {
        load();
        boolean modified = false;
        synchronized (this) {
            for (T element : this) {
                if (doRemoveCard(element, set)) {
                    modified = true;
                }
            }
            if (modified) {
                autoSave();
            }
        }
        return modified;
    }

    @Override
    public boolean contains(T card, ICardSet set) {
        synchronized (this) {
            for (T element : this) {
                if (element.equals(card)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected abstract boolean doAddCard(T card, ICardSet set);

    @Override
    public void setAutoCommit(boolean value) {
        this.autocommit = value;
    }

    @Override
    public boolean remove(T card, ICardSet set) {
        load();
        synchronized (this) {
            if (!doRemoveCard(card, set)) {
                return false;
            }
            autoSave();
        }
        return true;
    }

    protected abstract boolean doRemoveCard(T card, ICardSet set);

    @Override
    public boolean isAutoCommit() {
        return this.autocommit;
    }

    @Override
    public void autoSave() {
        if (isAutoCommit()) {
            save();
        } else {
            setNeedToSave(true);
        }
    }

    @Override
    public boolean isNeedToBeSaved() {
        return needToSave;
    }

    protected void setNeedToSave(boolean value) {
        needToSave = value;
    }

    protected void setLoaded(boolean value) {
        this.loaded = value;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
