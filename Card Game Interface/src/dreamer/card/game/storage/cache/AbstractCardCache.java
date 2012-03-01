package dreamer.card.game.storage.cache;

import dreamer.card.game.CardFileUtils;
import dreamer.card.game.Editions;
import dreamer.card.game.Editions.Edition;
import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardSet;
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
    private static final ArrayList<Card> cardImageQueue = new ArrayList<Card>();
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
    public static ArrayList<Card> getCardImageQueue() {
        return cardImageQueue;
    }

    /**
     * @return the caching
     */
    public static boolean isCachingEnabled() {
        return caching;
    }

    @Override
    public URL createSetImageURL(Card card, boolean upload) throws IOException {
        String edition = card.getCardSetList().get(0).getName();
        String editionAbbr = Editions.getInstance().getAbbrByName(edition);
        String rarity = Lookup.getDefault().lookup(IDataBaseManager.class).getCardAttribute(card, "rarity");
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

    public static String createLocalImageFilePath(Card card, CardSet set) throws CannotDetermineSetAbbriviation {
        String editionName = set.getName();
        Editions editions = Editions.getInstance();
        Edition cset = editions.getEditionByName(editionName);
        if (set == null) {
            throw new CannotDetermineSetAbbriviation(set);
        }
        String editionAbbr = cset.getBaseFileName();
        int cardId = card.getCardPK().getId();
        File loc = getCacheLocationFile();
        String locale = "EN";
        String part = "Cards/" + editionAbbr + "/" + locale + "/Card" + cardId + ".jpg";
        String file = new File(loc, part).getPath();
        return file;
    }

    public static String createLocalSetImageFilePath(String editionAbbr, String rarity) throws MalformedURLException {
        File loc = getCacheLocationFile();
        String part = "Sets/" + editionAbbr + "-" + rarity + ".jpg";
        String file = new File(loc, part).getPath();
        return file;
    }

//    private static Job cardImageLoadingJob = new Job("Loading card images") {
//        {
//            setSystem(true);
//        }
//
//        @Override
//        protected IStatus run(IProgressMonitor monitor) {
//            while (true) {
//                IMagicCard card = null;
//                synchronized (cardImageQueue) {
//                    if (cardImageQueue.size() > 0) {
//                        card = cardImageQueue.iterator().next();
//                        cardImageQueue.remove(card);
//                    } else {
//                        return Status.OK_STATUS;
//                    }
//                }
//                synchronized (card) {
//                    try {
//                        downloadAndSaveImage(card, isLoadingEnabled(), true);
//                    } catch (Exception e) {
//                        continue;
//                    } finally {
//                        card.notifyAll();
//                    }
//                }
//            }
//        }
//    };
//    private static void queueImageLoading(Card card) {
//        synchronized (getCardImageQueue()) {
//            if (!cardImageQueue.contains(card)) {
//                getCardImageQueue().add(card);
//            }
//        }
//        cardImageLoadingJob.schedule(0);
//    }
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
    public static File downloadAndSaveImage(Card card, CardSet set, URL url, boolean remote, boolean forceRemote) throws IOException, CannotDetermineSetAbbriviation {
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
    public boolean loadCardImageOffline(Card card, CardSet set, boolean forceUpdate) throws IOException, CannotDetermineSetAbbriviation {
        String path = createLocalImageFilePath(card, set);
        File file = new File(path);
        if (file.exists() && forceUpdate == false) {
            return true;
        }
        if (!isLoadingEnabled()) {
            throw new CachedImageNotFoundException("Cannot find cached image for " + card.getName());
        }
//TODO        AbstractCardCache.queueImageLoading(card);
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
    public static boolean isImageCached(Card card, CardSet set) throws CannotDetermineSetAbbriviation {
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
