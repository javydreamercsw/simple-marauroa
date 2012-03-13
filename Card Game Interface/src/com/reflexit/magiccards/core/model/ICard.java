package com.reflexit.magiccards.core.model;

/**
 * Represents a card
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface ICard extends Comparable {

    /**
     * Card name
     *
     * @return Card name
     */
    public String getName();

    /**
     * Get object by field
     *
     * @param field
     * @return Object matching criteria
     */
    Object getObjectByField(ICardField field);

    /**
     * Get the card's ID
     *
     * @return card ID
     */
    public int getCardId();
}
