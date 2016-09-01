package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.model.ICardEventManager;
import com.reflexit.magiccards.core.model.ICardSet;
import com.reflexit.magiccards.core.model.IMergeable;
import java.util.Collection;
import java.util.Iterator;

public interface ICardStore<T> extends IMergeable<T>,
        ICardEventManager<T>, IStorageContainer<T> {

    /**
     * Get comments.
     *
     * @return comments
     */
    public String getComment();

    /**
     * Virtual vs. physical.
     *
     * @return true if virtual
     */
    public boolean isVirtual();

    /**
     * Get Card.
     *
     * @param id card id
     * @return card with specified id
     */
    public T getCard(int id);

    /**
     * Get cards for specified id.
     *
     * @param id card id
     * @return cards with specified id
     */
    public Collection<T> getCards(int id);

    /**
     * Add a collection of cards.
     *
     * @param cards cards to add
     * @return true if successful
     */
    public boolean addAll(Collection<? extends T> cards, ICardSet set);

    /**
     * Add card.
     *
     * @param card card to add
     * @return true if successful
     */
    public boolean add(T card, ICardSet set);

    /**
     * Remove card.
     *
     * @param o card to remove
     * @return true if successful
     */
    public boolean remove(T o, ICardSet set);

    /**
     * Remove cards.
     *
     * @param list cards to remove
     * @return true if successful
     */
    public boolean removeAll(Collection<? extends T> list, ICardSet set);

    /**
     * Remove all cards.
     *
     * @return true if successful
     */
    public boolean removeAll(ICardSet set);

    /**
     * Store size.
     *
     * @return size
     */
    public int size();

    /**
     * Contains card.
     *
     * @param o card to check
     * @return true if contained
     */
    public boolean contains(T o);

    public Iterator<T> iterator();
}