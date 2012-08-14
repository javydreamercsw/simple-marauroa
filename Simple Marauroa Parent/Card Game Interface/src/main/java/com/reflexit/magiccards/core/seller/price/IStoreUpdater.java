package com.reflexit.magiccards.core.seller.price;

import com.reflexit.magiccards.core.model.ISellableCard;
import com.reflexit.magiccards.core.model.storage.ICardStore;
import com.reflexit.magiccards.core.model.storage.IFilteredCardStore;
import java.io.IOException;

public interface IStoreUpdater {

    /**
     * Update cards (from iterable) in a given store
     *
     * @param store - card store (used to save updates and fire the events)
     * @param iterable - if not null - used to get exact card list, if null
     * store iterator is used
     * @param size - size of the cards (for iterable)
     * @throws IOException
     */
    public void updateStore(ICardStore<ISellableCard> store,
            Iterable<ISellableCard> iterable, int size)
            throws IOException;

    /**
     * Update cards in a given store
     *
     * @param store - card store (used to save updates and fire the events)
     * @throws IOException
     */
    public void updateStore(IFilteredCardStore<ISellableCard> store) throws IOException;
}
