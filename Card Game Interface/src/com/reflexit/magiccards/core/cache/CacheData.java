package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.model.ICard;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 * Data holding the cached and to be cached pages.
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = ICacheData.class)
public class CacheData implements ICacheData {

    private List<ICard> cachedCards = new ArrayList<ICard>();
    private List<ICard> cardsToCache = new ArrayList<ICard>();

    @Override
    public void add(ICard card) {
        synchronized (this) {
            if (!cachedCards.contains(card)) {
                cardsToCache.add(card);
            }
        }
    }

    /**
     * Get next card to cache. Can return null (if nothing to cache)
     *
     * @return
     */
    @Override
    public ICard next() {
        if (cardsToCache.isEmpty()) {
            return null;
        }
        synchronized (this) {
            // Need to check again if size has changed
            if (cardsToCache.size() > 0) {
                ICard card = cardsToCache.get(0);
                cardsToCache.remove(0);
                cachedCards.add(card);
                return card;
            }
            return null;
        }
    }

    @Override
    public int toCacheAmount() {
        synchronized (this) {
            return cardsToCache.size();
        }
    }

    @Override
    public int cachedAmount() {
        synchronized (this) {
            return cachedCards.size();
        }
    }
}
