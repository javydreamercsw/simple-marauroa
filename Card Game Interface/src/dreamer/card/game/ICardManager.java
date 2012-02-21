package dreamer.card.game;

import java.util.List;

/**
 * Common card game management
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface ICardManager {

    /**
     * Get a list of decks
     *
     * @return
     */
    List<IDeck> getDecks();

    /**
     * Load decks
     */
    void loadDecks();

    /**
     * Check loading status
     *
     * @return true if loaded, false otherwise.
     */
    boolean isDeckLoaded();

    /**
     * Selected deck (in use)
     *
     * @return
     */
    IDeck getDeck();

    /**
     * Get the component used to display the deck hand
     *
     * @return component used to display the deck hand
     */
    Object getHandComponent();

    /**
     * Get the component used to display the deck contents
     *
     * @return component used to display the deck contents
     */
    Object getDeckComponent();
}
