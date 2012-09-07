package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.model.ICard;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICacheData {

    /**
     * Add a card to the queue.
     *
     * @param card card to add
     */
    void add(ICard card);

    /**
     * Get next card to cache. Can return null (if nothing to cache).
     *
     * @return Next card to cache
     */
    ICard next();

    /**
     * Amount of cards pending caching.
     *
     * @return Amount of cards pending caching
     */
    int toCacheAmount();

    /**
     * Amount of cards cached.
     *
     * @return Amount of cards cached
     */
    int cachedAmount();
}
