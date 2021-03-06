package com.reflexit.magiccards.core.model;

import org.openide.util.Lookup;

/**
 * Represents a card
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface ICard extends Comparable, Lookup.Provider {

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
    public String getCardId();

    /**
     * Get card's set
     *
     * @return card's set
     */
    public String getSetName();

    /**
     * Set card's set
     *
     * @param set card's set
     */
    public void setSetName(String set);

    /**
     * Get the card's type.
     *
     * @return card's type.
     */
    public ICardType getCardType();
}
