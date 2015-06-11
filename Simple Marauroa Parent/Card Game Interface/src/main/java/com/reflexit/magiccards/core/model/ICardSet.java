package com.reflexit.magiccards.core.model;

import java.util.Collection;

public interface ICardSet<ICard> extends Iterable<ICard> {

    /**
     * Card set's name
     * @return Card set's name
     */
    public String getName();

    /**
     * Card set's game name
     * @return Card set's game name
     */
    public ICardGame getCardGame();
    
    /**
     * Cards within the set
     * @return Cards within the set
     */
    public Collection<ICard> getCards();
}