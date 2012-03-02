package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.model.ICardEventManager;
import com.reflexit.magiccards.core.model.ICardSet;
import com.reflexit.magiccards.core.model.IMergeable;
import java.util.Collection;

public interface ICardStore<T> extends ICardSet<T>, IMergeable<T>,
        ICardEventManager<T>, IStorageContainer<T> {

    public String getComment();

    public boolean isVirtual();

    public T getCard(int id);

    public Collection<T> getCards(int id);

    public boolean addAll(Collection<? extends T> cards);

    public boolean add(T card);

    public boolean remove(T o);

    public boolean removeAll(Collection<? extends T> list);

    public boolean removeAll();

    public int size();

    public boolean contains(T o);
}