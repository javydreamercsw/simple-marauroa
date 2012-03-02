package com.reflexit.magiccards.core.model;

/**
 * Sellable cards
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ISellableCard extends ICardWithImages {

    /**
     * Set the card's price
     *
     * @param price
     */
    public void setDbPrice(float price);

    /**
     * Get the card's price
     * @return 
     */
    public float getDbPrice();
}
