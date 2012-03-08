package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.CachedImageNotFoundException;
import com.reflexit.magiccards.core.CannotDetermineSetAbbriviation;
import com.reflexit.magiccards.core.model.CardFileUtils;
import com.reflexit.magiccards.core.model.Editions;
import com.reflexit.magiccards.core.model.Editions.Edition;
import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardSet;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractCardCache implements ICardCache {

    private static boolean caching;
    private static boolean loading;
    private static final ArrayList<ICard> cardImageQueue = new ArrayList<ICard>();
    private static File cacheDir;
    private String name;

    public static void setCahchingEnabled(boolean enabled) {
        caching = enabled;
    }

    public static void setLoadingEnabled(boolean enabled) {
        loading = enabled;
    }

    public static boolean isLoadingEnabled() {
        return loading;
    }

    public static void setCachingEnabled(boolean enabled) {
        caching = enabled;
    }

    /**
     * @return the cardImageQueue
     */
    public static ArrayList<ICard> getCardImageQueue() {
        return cardImageQueue;
    }

    /**
     * @return the caching
     */
    public static boolean isCachingEnabled() {
        return caching;
    }

    @Override
    public URL createSetImageURL(ICard card, String editionAbbr, boolean upload) throws IOException {
        String rarity = Lookup.getDefault().lookup(IDataBaseCardStorage.class).getCardAttribute(card, "rarity");
        if (editionAbbr == null) {
            return null;
        }
        String path = createLocalSetImageFilePath(editionAbbr, rarity);
        File file = new File(path);
        URL localUrl = file.toURI().toURL();
        if (upload == false) {
            return localUrl;
        }
        if (file.exists()) {
            return localUrl;
        }
        try {
            URL url = createSetImageRemoteURL(editionAbbr, rarity);
            if (url == null) {
                return null;
            }
            InputStream st = url.openStream();
            CardFileUtils.saveStream(st, file);
            st.close();
        } catch (IOException e1) {
            throw e1;
        }
        return localUrl;
    }

    public static String createLocalImageFilePath(ICard card, ICardSet set) throws CannotDetermineSetAbbriviation {
        String editionName = set.getName();
        Editions editions = Editions.getInstance();
        Edition cset = editions.getEditionByName(editionName);
        if (cset == null) {
            throw new CannotDetermineSetAbbriviation(cset);
        }
        String editionAbbr = cset.getBaseFileName();
        int cardId = card.getCardId();
        File loc = getCacheLocationFile();
        String locale = "EN";
        String part = set.getGameName() + System.getProperty("file.separator") + "Cards"
                + System.getProperty("file.separator") + editionAbbr + System.getProperty("file.separator")
                + locale + System.getProperty("file.separator") + "Card" + cardId + ".jpg";
        String file = new File(loc, part).getPath();
        return file;
    }

    public static String createLocalSetImageFilePath(String editionAbbr, String rarity) throws MalformedURLException {
        File loc = getCacheLocationFile();
        String part = "Sets/" + editionAbbr + "-" + rarity + ".jpg";
        String file = new File(loc, part).getPath();
        return file;
    }

    /**
     * Download and save card image, if not already saved
     *
     * @param card
     * @param set
     * @param url
     * @param remote
     * @param forceRemote
     * @return
     * @throws IOException
     * @throws CannotDetermineSetAbbriviation
     */
    public static File downloadAndSaveImage(ICard card, ICardSet set, URL url, boolean remote, boolean forceRemote) throws IOException, CannotDetermineSetAbbriviation {
        String path = AbstractCardCache.createLocalImageFilePath(card, set);
        File file = new File(path);
        if (forceRemote == false && file.exists()) {
            return file;
        }
        if (!remote) {
            throw new CachedImageNotFoundException("Cannot find cached image for " + card.getName());
        }
        InputStream st = null;
        try {
            st = url.openStream();
        } catch (IOException e) {
            throw new IOException("Cannot connect: " + e.getMessage());
        }
        File file2 = new File(path + ".part");
        CardFileUtils.saveStream(st, file2);
        st.close();
        if (file2.exists()) {
            file2.renameTo(file);
            if (!file.exists()) {
                throw new IOException("failed to rename into " + file.toString());
            }
            return file;
        }
        throw new FileNotFoundException(file.toString());
    }

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
    @Override
    public boolean loadCardImageOffline(ICard card, ICardSet set, boolean forceUpdate) throws IOException, CannotDetermineSetAbbriviation {
        String path = createLocalImageFilePath(card, set);
        File file = new File(path);
        if (file.exists() && forceUpdate == false) {
            return true;
        }
        if (!isLoadingEnabled()) {
            throw new CachedImageNotFoundException("Cannot find cached image for " + card.getName());
        }
        AbstractCardCache.getCardImageQueue().add(card);
        return false;
    }

    /**
     * Check if is already cached
     *
     * @param card card to check
     * @param set
     * @return true or false
     * @throws CannotDetermineSetAbbriviation
     */
    public static boolean isImageCached(ICard card, ICardSet set) throws CannotDetermineSetAbbriviation {
        String path = createLocalImageFilePath(card, set);
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private static File getCacheLocationFile() {
        return cacheDir;
    }

    /**
     * @param aCacheDir the cacheDir to set
     */
    public static void setCacheDir(File aCacheDir) {
        cacheDir = aCacheDir;
        cacheDir.mkdirs();
    }

    /**
     * @return the name
     */
    @Override
    public String getGameName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setGameName(String name) {
        this.name = name;
    }
}
