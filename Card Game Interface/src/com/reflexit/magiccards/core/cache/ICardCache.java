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
     * Get the game this cache is for
     *
     * @return Game name
     */
    String getGameName();

    /**
     * Create the URL to retrieve the image of the card
     *
     * @param card Card
     * @param set Set
     * @return URL
     * @throws MalformedURLException
     * @throws CannotDetermineSetAbbriviation
     */
    URL createRemoteImageURL(ICard card, Editions.Edition set) throws MalformedURLException, CannotDetermineSetAbbriviation;

    /**
     * Create the URL to retrieve the image of the set
     *
     * @param editionAbbr
     * @param rarity
     * @return URL
     * @throws MalformedURLException
     */
    URL createSetImageRemoteURL(String editionAbbr, String rarity) throws MalformedURLException;

    /**
     * Create the URL to retrieve the image of the card for this set
     *
     * @param card
     * @param editionAbbr
     * @param upload
     * @return
     * @throws IOException
     */
    URL createSetImageURL(ICard card, String editionAbbr, boolean upload) throws IOException;

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
    public boolean loadCardImageOffline(ICard card, Editions.Edition set,
            boolean forceUpdate) throws IOException, CannotDetermineSetAbbriviation;

    /**
     * Get the task to update the cache
     *
     * @return
     */
    public Runnable getCacheTask();

    /**
     * Load card image offline
     *
     * @param card Card to load
     * @param set Set to load the card for
     * @param forceUpdate force updating cache
     * @return true if successful
     * @throws IOException
     * @throws CannotDetermineSetAbbriviation
     */
    public boolean loadCardImageOffline(ICard card, ICardSet set, boolean forceUpdate) throws IOException, CannotDetermineSetAbbriviation;

    /**
     * Get current cache directory
     *
     * @return
     */
    public File getCacheLocationFile();

    /**
     * Set current cache directory
     *
     * @param aCacheDir the cacheDir to set
     */
    public void setCacheDir(File aCacheDir);

    /**
     * Get icon for set
     *
     * @param set Set to look icon for
     * @return Icon or null if none
     * @throws IOException
     */
    public Image getSetIcon(ICardSet set) throws IOException;

    /**
     * Get path to a set's icon.
     *
     * @param set Set to get the path for
     * @return path to a set's icon
     */
    public String getSetIconPath(ICardSet set);

    /**
     * Get the icon for the game
     *
     * @param game Game to get the icon for
     * @return Icon or null if none
     * @throws IOException
     */
    public Image getGameIcon(ICardGame game) throws IOException;

    /**
     * Get path to a game's icon.
     *
     * @return path to a game's icon
     */
    public String getGameIconPath();

    /**
     * Get path to card image
     *
     * @param card Card to get image of
     * @param set Card's set
     * @return Path to the file
     * @throws CannotDetermineSetAbbriviation
     */
    public String createLocalImageFilePath(ICard card, ICardSet set) throws CannotDetermineSetAbbriviation;

    /**
     * Get the image of a card (it is downloaded if not there)
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
    public File getCardImage(ICard card, ICardSet set, URL url, boolean remote, boolean forceRemote) throws IOException, CannotDetermineSetAbbriviation;
}
