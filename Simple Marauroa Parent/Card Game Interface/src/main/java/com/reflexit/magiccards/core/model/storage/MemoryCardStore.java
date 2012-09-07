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

import com.reflexit.magiccards.core.model.ICardCountable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * ArrayList based implementation for AbstractCardStore
 *
 * @param <T>
 * @author Alena
 *
 */
public class MemoryCardStore<T> extends AbstractCardStoreWithStorage<T>
        implements ICardCountable, IStorage<T> {

    /**
     * creates empty card store
     */
    public MemoryCardStore() {
        super((IStorage<T>) new MemoryCardStorage<T>(), false);
    }

    @Override
    public void setMergeOnAdd(final boolean v) {
        this.mergeOnAdd = v;
    }

    @Override
    public boolean getMergeOnAdd() {
        return this.mergeOnAdd;
    }

    @Override
    public T getCard(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<T> getCards(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getCount() {
        int count = 0;
        boolean countable = false;
        for (T card : getStorage()) {
            if (card instanceof ICardCountable) {
                count += ((ICardCountable) card).getCount();
                countable = true;
            }
        }
        if (countable) {
            return count;
        }
        return size();
    }

    @Override
    public void save() {
        getStorage().save();
    }

    @Override
    public boolean isNeedToBeSaved() {
        return getStorage().isNeedToBeSaved();
    }

    @Override
    public void autoSave() {
        getStorage().autoSave();
    }

    @Override
    public void load() {
        getStorage().load();
    }

    @Override
    public boolean isLoaded() {
        return getStorage().isLoaded();
    }

    @Override
    public String getGameName() {
        return getStorage().getName();
    }

    @Override
    public Collection<T> getCards() {
        ArrayList<T> cards = new ArrayList<T>();
        for (Iterator<T> it = getStorage().iterator(); it.hasNext();) {
            cards.add(it.next());
        }
        return cards;
    }
}