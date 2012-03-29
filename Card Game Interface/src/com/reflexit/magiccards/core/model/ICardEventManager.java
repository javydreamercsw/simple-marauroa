package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.model.events.ICardEventListener;

public interface ICardEventManager<ICard> {

    /**
     * Add a listener
     *
     * @param lis listener
     */
    public void addListener(ICardEventListener lis);

    /**
     * Remove a listener
     *
     * @param lis listener
     */
    public void removeListener(ICardEventListener lis);

    /**
     * Card values were updated
     *
     * @param card
     */
    public void update(ICard card);
}