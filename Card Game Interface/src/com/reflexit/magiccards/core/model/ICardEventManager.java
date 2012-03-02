package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.model.events.ICardEventListener;

public interface ICardEventManager<ICard> {

    public void addListener(ICardEventListener lis);

    public void removeListener(ICardEventListener lis);

    /**
     * card values were updated
     *
     * @param card
     */
    public void update(ICard card);
}