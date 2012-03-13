package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.CachedImageNotFoundException;
import com.reflexit.magiccards.core.CannotDetermineSetAbbriviation;
import com.reflexit.magiccards.core.model.Editions.Edition;
import com.reflexit.magiccards.core.model.*;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractCardCache implements ICardCache {

    private static boolean caching;
    private static boolean loading;
    private static File cacheDir;
    private String name;
    private static final Logger LOG = Logger.getLogger(AbstractCardCache.class.getName());

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
     * @return the caching
     */
    public static boolean isCachingEnabled() {
        return caching;
    }

    public AbstractCardCache(String name) {
        this.name = name;
        setLoadingEnabled(true);
        setCachingEnabled(true);
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

    public String createLocalImageFilePath(ICard card, ICardSet set) throws CannotDetermineSetAbbriviation {
        String editionName = set.getName();
        Editions editions = Editions.getInstance();
        Edition cset = editions.getEditionByName(editionName);
        if (cset == null) {
            throw new CannotDetermineSetAbbriviation(cset);
        }
        String editionAbbr = cset.getBaseFileName();
        Integer cardId = Integer.valueOf(Lookup.getDefault().lookup(IDataBaseCardStorage.class).getCardAttribute(card, "CardId"));
        File loc = getCacheLocationFile();
        String locale = "EN";
        String part = set.getGameName() + System.getProperty("file.separator") + "Cards"
                + System.getProperty("file.separator") + editionAbbr + System.getProperty("file.separator")
                + locale + System.getProperty("file.separator") + "Card" + cardId + ".jpg";
        String file = new File(loc, part).getPath();
        return file;
    }

    public String createLocalSetImageFilePath(String editionAbbr, String rarity) throws MalformedURLException {
        File loc = getCacheLocationFile();
        String part = "Sets/" + editionAbbr + "-" + rarity + ".jpg";
        String file = new File(loc, part).getPath();
        return file;
    }

    public boolean cardImageExists(ICard card, ICardSet set) {
        try {
            return new File(createLocalImageFilePath(card, set)).exists();
        } catch (CannotDetermineSetAbbriviation ex) {
            Logger.getLogger(AbstractCardCache.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
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
    public File downloadAndSaveImage(ICard card, ICardSet set, URL url, boolean remote, boolean forceRemote) throws IOException, CannotDetermineSetAbbriviation {
        String path = createLocalImageFilePath(card, set);
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
        Lookup.getDefault().lookup(ICacheData.class).add(card);
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
    public boolean isImageCached(ICard card, ICardSet set) throws CannotDetermineSetAbbriviation {
        String path = createLocalImageFilePath(card, set);
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    @Override
    public File getCacheLocationFile() {
        return cacheDir;
    }

    /**
     * @param aCacheDir the cacheDir to set
     */
    @Override
    public void setCacheDir(File aCacheDir) {
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
     * Download image from URL
     *
     * @param url url to download file from
     * @param dest File to store the image to
     * @param overwrite overwrite file if found
     * @return Downloaded image
     * @throws IOException
     */
    protected Image downloadImageFromURL(URL url, File dest, boolean overwrite) throws IOException {
        InputStream st = null;
        if (!dest.exists() || overwrite) {
            LOG.log(Level.FINE, "Downloading file from: {0}", url);
            try {
                st = url.openStream();
            } catch (IOException e) {
                throw new IOException("Cannot connect: " + e.getMessage());
            }
            File file2 = new File(dest.getAbsolutePath() + ".part");
            CardFileUtils.saveStream(st, file2);
            st.close();
            if (file2.exists()) {
                file2.renameTo(dest);
                if (!dest.exists()) {
                    throw new IOException("failed to rename into " + dest.toString());
                }
            }
        }
        if (dest.exists()) {
            return (new ImageIcon(dest.toURI().toURL(), "icon")).getImage();
        }
        throw new FileNotFoundException(dest.toString());
    }

    @Override
    public String getSetIconPath(ICardSet set) {
        File loc = getCacheLocationFile();
        Edition edition = Editions.getInstance().getEditionByName(set.getName());
        String locale = "EN";
        String part = set.getGameName() + System.getProperty("file.separator") + "Sets"
                + System.getProperty("file.separator") + edition.getMainAbbreviation() + System.getProperty("file.separator")
                + locale + System.getProperty("file.separator") + edition.getMainAbbreviation() + ".jpg";
        return new File(loc, part).getPath();
    }

    @Override
    public String getGameIconPath() {
        File loc = getCacheLocationFile();
        String part = getGame().getName() + System.getProperty("file.separator")
                + "game.jpg";
        return new File(loc, part).getPath();
    }

    private ICardGame getGame() {
        for (ICardGame game : Lookup.getDefault().lookupAll(ICardGame.class)) {
            if (game.getName().equals(getGameName())) {
                return game;
            }
        }
        return null;
    }
}
