package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.CannotDetermineSetAbbriviation;
import com.reflexit.magiccards.core.model.Editions;
import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardGame;
import com.reflexit.magiccards.core.model.ICardSet;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface ICardCache {

    /**
     * Get the game this cache is for.
     *
     * @return Game name
     */
    String getGameName();

    /**
     * Create the URL to retrieve the image of the card.
     *
     * @param card Card
     * @param set Set
     * @return URL Remote image's URL
     * @throws MalformedURLException
     * @throws CannotDetermineSetAbbriviation
     */
    URL createRemoteImageURL(ICard card, Editions.Edition set)
            throws MalformedURLException, CannotDetermineSetAbbriviation;

    /**
     * Create the URL to retrieve the image of the set.
     *
     * @param editionAbbr Edition abbreviation
     * @param rarity Rarity
     * @return URL Set image URL
     * @throws MalformedURLException
     */
    URL createSetImageRemoteURL(String editionAbbr, String rarity)
            throws MalformedURLException;

    /**
     * Create the URL to retrieve the image of the card for this set.
     *
     * @param card Card to create set image for
     * @param editionAbbr Edition abbreviation
     * @param remote Return local (false) or remote URL (true)
     * @return
     * @throws IOException
     */
    URL createSetImageURL(ICard card, String editionAbbr, boolean remote)
            throws IOException;

    /**
     * Get card image or schedule a loading job if image not found. This image
     * is not managed - to be disposed by called. To get notified when job is
     * done loading, can wait on card object
     *
     * @param card
     * @param set
     * @param forceUpdate
     * @return true if card image exists, schedule update otherwise. If loading
     * is disabled and there is no cached image through an exception
     * @throws IOException
     * @throws CannotDetermineSetAbbriviation
     */
    boolean loadCardImageOffline(ICard card, Editions.Edition set,
            boolean forceUpdate) throws IOException,
            CannotDetermineSetAbbriviation;

    /**
     * Get the task to update the cache.
     *
     * @return
     */
    Runnable getCacheTask();

    /**
     * Load card image offline.
     *
     * @param card Card to load
     * @param set Set to load the card for
     * @param forceUpdate force updating cache
     * @return true if successful
     * @throws IOException
     * @throws CannotDetermineSetAbbriviation
     */
    boolean loadCardImageOffline(ICard card, ICardSet<ICard> set,
            boolean forceUpdate) throws IOException,
            CannotDetermineSetAbbriviation;

    /**
     * Get current cache directory.
     *
     * @return
     */
    File getCacheLocationFile();

    /**
     * Get icon for set.
     *
     * @param set Set to look icon for
     * @return Icon or null if none
     * @throws IOException
     */
    Image getSetIcon(ICardSet<ICard> set) throws IOException;

    /**
     * Get path to a set's icon.
     *
     * @param set Set to get the path for
     * @return path to a set's icon
     */
    String getSetIconPath(ICardSet<ICard> set);

    /**
     * Get the icon for the game.
     *
     * @param game Game to get the icon for
     * @return Icon or null if none
     * @throws IOException
     */
    Image getGameIcon(ICardGame game) throws IOException;

    /**
     * Get path to a game's icon.
     *
     * @return path to a game's icon
     */
    String getGameIconPath();

    /**
     * Get path to a game's folder.
     *
     * @return path to a game's folder
     */
    String getGamePath();

    /**
     * Get path to card image.
     *
     * @param card Card to get image of
     * @param set Card's set
     * @return Path to the file
     * @throws CannotDetermineSetAbbriviation
     */
    String createLocalImageFilePath(ICard card, ICardSet<ICard> set)
            throws CannotDetermineSetAbbriviation;

    /**
     * Get the image of a card (it is downloaded if not there).
     *
     * @param card Card to get image of
     * @param set Card's set
     * @param url URL to the image
     * @param remote Remote
     * @param forceRemote overwrite?
     * @return File with the image
     * @throws IOException
     * @throws CannotDetermineSetAbbriviation
     */
    File getCardImage(ICard card, ICardSet<ICard> set, URL url,
            boolean remote, boolean forceRemote) throws IOException,
            CannotDetermineSetAbbriviation;
}
