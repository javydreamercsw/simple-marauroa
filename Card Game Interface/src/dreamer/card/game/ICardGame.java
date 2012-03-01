/*
 * Represents a card game
 */
package dreamer.card.game;

import dreamer.card.game.storage.cache.ICardCache;
import java.util.List;

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
     * 
     * @return
     */
    public Runnable getUpdateRunnable();
    
    /**
     * Get cache implementations
     * @return List of implementations
     */
    public List<ICardCache> getCardCacheImplementations();
}
