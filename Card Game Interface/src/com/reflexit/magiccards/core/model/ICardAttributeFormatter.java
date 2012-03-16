package com.reflexit.magiccards.core.model;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ICardAttributeFormatter {

    /**
     * Get game for this formatter
     *
     * @return game for this formatter
     */
    public ICardGame getGame();

    /**
     * Format a card's attribute for output
     *
     * @param value value to format
     * @return formatted object
     */
    public Object format(String value);
}
