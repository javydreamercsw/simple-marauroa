package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.model.CardGroup;
import com.reflexit.magiccards.core.model.ICardCountable;
import com.reflexit.magiccards.core.model.ICardFilter;

public interface IFilteredCardStore<T> extends Iterable<T>, ICardCountable {

    /**
     * Update based on filter.
     *
     * @param filter filter
     * @throws Exception
     */
    public void update(ICardFilter filter) throws Exception;

    /**
     * Get filter.
     *
     * @return Filter
     */
    public ICardFilter getFilter();

    /**
     * Get card store.
     *
     * @return
     */
    public ICardStore getCardStore();

    /**
     * Size of filtered list.
     *
     * @return
     */
    public int getSize();

    /**
     * Elements in filtered list.
     *
     * @return
     */
    public Object[] getElements();

    /**
     * Returns given element in filtered list.
     *
     * @param index
     * @return
     */
    public Object getElement(int index);

    /**
     * Return top level cards group if grouping is enabled or null if not
     * enabled.
     *
     * @return CardGroup[]
     */
    public CardGroup[] getCardGroups();

    /**
     * Get CardGroup.
     *
     * @param index Index of CardGroup
     * @return CardGroup
     */
    public CardGroup getCardGroup(int index);

    /**
     * Contains card?
     *
     * @param card Card to check
     * @return True if contained
     */
    public boolean contains(T card);
}