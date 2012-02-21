package dreamer.card.game;

/**
 * Sellable cards
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ISellableCard extends ICard {

    /**
     * Set the card's price
     *
     * @param price
     */
    public void setDbPrice(float price);

    /**
     * Get the card's price
     */
    public float getDbPrice();
}
