package simple.server.extension.card.price;

import java.io.IOException;
import simple.server.extension.card.ISellableCard;
import simple.server.extension.card.storage.ICardStore;

public interface IStoreUpdator {

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
}
