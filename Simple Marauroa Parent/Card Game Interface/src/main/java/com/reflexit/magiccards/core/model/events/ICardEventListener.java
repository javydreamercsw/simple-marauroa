package com.reflexit.magiccards.core.model.events;

public interface ICardEventListener {

    /**
     * Handle card event.
     *
     * @param event Event to handle
     */
    public void handleEvent(CardEvent event);
}
