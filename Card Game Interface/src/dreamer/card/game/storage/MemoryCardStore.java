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
package dreamer.card.game.storage;

import dreamer.card.game.ICardCountable;
import java.util.Collection;

/**
 * ArrayList based implementation for AbstractCardStore
 *
 * @author Alena
 *
 */
public class MemoryCardStore<T> extends AbstractCardStoreWithStorage<T> implements ICardStore<T>, ICardCountable, IStorage<T> {

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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isNeedToBeSaved() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void autoSave() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void load() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isLoaded() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}