package com.reflexit.magiccards.core.model;

import java.awt.Component;
import org.openide.util.Lookup.Provider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface IGameDataManager extends Provider {

    /**
     * Set the game for this manager
     *
     * @param game
     */
    public void setGame(ICardGame game);

    /**
     * Get the game for this manager
     *
     * @return Game
     */
    public ICardGame getGame();

    /**
     * Component to be displayed in the GUI
     *
     * @return
     */
    Component getComponent();
    
    /**
     * Load the data
     */
    public void load();
}
