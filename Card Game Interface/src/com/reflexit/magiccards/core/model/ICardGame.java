/*
 * Represents a card game
 */
package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.cache.ICardCache;
import java.awt.Image;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardGame extends IGame {

    /**
     * Initialize the system for this game
     */
    public void init();

    /**
     * Gets the Runnable responsible for updating this game.
     *
     * @return Runnable responsible for updating this game
     */
    public Runnable getUpdateRunnable();

    /**
     * Get cache implementations
     *
     * @return List of implementations
     */
    public List<ICardCache> getCardCacheImplementations();

    /**
     * Get cache implementations
     *
     * @return List of implementations
     */
    public List<IGameDataManager> getGameDataManagerImplementations();

    /**
     * Get the game specific back card icon
     *
     * @return game special back card icon or null if not defined
     */
    public Image getBackCardIcon();

    /**
     * Get the game specific game icon
     *
     * @return game specific game icon or null if not defined
     */
    public Image getGameIcon();

    /**
     * Get attribute formatters for this game
     *
     * @return attribute formatters for this game
     */
    public List<ICardAttributeFormatter> getGameCardAttributeFormatterImplementations();
}
