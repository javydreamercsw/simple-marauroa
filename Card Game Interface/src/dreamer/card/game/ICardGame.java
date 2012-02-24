/*
 * Represents a card game
 */
package dreamer.card.game;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardGame {
    /**
     * The game's name
     * @return game's name
     */
    public String getName();
    
    /**
     * Initialize the system for this game
     */
    public void init();
    
    /**
     * Update the card database
     */
    public void updateDatabase();
}
