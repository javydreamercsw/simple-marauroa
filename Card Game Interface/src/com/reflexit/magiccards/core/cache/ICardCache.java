package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.CannotDetermineSetAbbriviation;
import com.reflexit.magiccards.core.model.Editions;
import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardSet;
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
     * @param card Card to load
     * @param set Set to load the card for
     * @param forceUpdate force updating cache
     * @return true if successful
     * @throws IOException
     * @throws CannotDetermineSetAbbriviation
     */
    public boolean loadCardImageOffline(ICard card, ICardSet set, boolean forceUpdate) throws IOException, CannotDetermineSetAbbriviation;
}
