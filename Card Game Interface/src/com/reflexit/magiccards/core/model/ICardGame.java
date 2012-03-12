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
public interface ICardGame {

    /**
     * The game name
     *
     * @return game name
     */
    public String getName();

    /**
     * Initialize the system for this game
     */
    public void init();

    /**
     * Gets the Runnable responsible for updating this game.
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
     * Get the game specific back card icon
     *
     * @return game special back card icon
     */
    public Image getBackCardIcon();
}
