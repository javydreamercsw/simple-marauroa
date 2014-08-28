package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.cache.ICardCache;
import com.reflexit.magiccards.core.model.storage.db.DBException;
import com.reflexit.magiccards.core.model.storage.db.DataBaseStateListener;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class DefaultCardGame implements ICardGame,
        DataBaseStateListener {

    protected static final List<String> attribs = new ArrayList<String>();
    protected static final ArrayList<String> collectionTypes
            = new ArrayList<String>();
    protected static final HashMap<String, String> collections
            = new HashMap<String, String>();
    private static final Logger LOG
            = Logger.getLogger(DefaultCardGame.class.getName());

    @Override
    public void init() {
        //Games auto register themselves
        Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                .addDataBaseStateListener(this);
    }

    @Override
    public void initialized() {
        HashMap parameters = new HashMap();
        try {
            synchronized (attribs) {
                //Create game attributes
                for (String attr : attribs) {
                    Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                            .createAttributes(attr);
                }
            }
            //Create default collection types
            synchronized (collectionTypes) {
                for (String type : collectionTypes) {
                    Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                            .createCardCollectionType(type);
                }
            }
            //Create default Collections
            synchronized (collections) {
                for (Entry<String, String> entry : collections.entrySet()) {
                    parameters.put("name", entry.getKey());
                    ICardCollectionType type
                            = (ICardCollectionType) Lookup.getDefault()
                            .lookup(IDataBaseCardStorage.class)
                            .namedQuery("CardCollectionType.findByName",
                                    parameters).get(0);
                    Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                            .createCardCollection(type, entry.getValue());
                }
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<ICardCache> getCardCacheImplementations() {
        ArrayList<ICardCache> caches = new ArrayList<ICardCache>();
        for (ICardCache cache : Lookup.getDefault().lookupAll(ICardCache.class)) {
            if (cache.getGameName().equals(getName())) {
                caches.add(cache);
            }
        }
        return caches;
    }

    @Override
    public Runnable getUpdateRunnable() {
        for (ICardCache icache : getCardCacheImplementations()) {
            if (icache.getGameName().equals(getName())) {
                return icache.getCacheTask();
            }
        }
        return null;
    }

    @Override
    public Image getGameIcon() {
        try {
            return Lookup.getDefault().lookup(ICardCache.class)
                    .getGameIcon((ICardGame) this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public IGameDataManager getGameDataManagerImplementation() {
        for (IGameDataManager dm : Lookup.getDefault().lookupAll(IGameDataManager.class)) {
            if (dm.getGame().getName().equals(getName())) {
                return dm;
            }
        }
        return null;
    }

    @Override
    public List<ICardAttributeFormatter> getGameCardAttributeFormatterImplementations() {
        ArrayList<ICardAttributeFormatter> formatters
                = new ArrayList<ICardAttributeFormatter>();
        for (ICardAttributeFormatter formatter : Lookup.getDefault()
                .lookupAll(ICardAttributeFormatter.class)) {
            if (formatter.getGame().getName().equals(getName())) {
                formatters.add(formatter);
            }
        }
        return formatters;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ICardSet> getGameCardSets() {
        try {
            HashMap parameters = new HashMap();
            parameters.put("name", getName());
            List result = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                    .namedQuery("Game.findByName", parameters);
            if (result.isEmpty()) {
                throw new RuntimeException("Unable to find game " + getName()
                        + " in database!");
            }
            parameters.clear();
            return Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                    .getSetsForGame((IGame) result.get(0));
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return new ArrayList<ICardSet>();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getColumns() {
        ArrayList<String> columns = new ArrayList<String>();
        try {
            columns.add("Name");
            columns.add("Set");
            HashMap parameters = new HashMap();
            parameters.put("game", getName());
            List result = Lookup.getDefault().lookup(IDataBaseCardStorage.class)
                    .createdQuery(
                            "select distinct chca.cardAttribute from "
                            + "CardHasCardAttribute chca, Card c, CardSet cs, Game g"
                            + " where cs.game =g and g.name =:game and cs member of c.cardSetList"
                            + " and chca.card =c order by chca.cardAttribute.name",
                            parameters);
            for (Object obj : result) {
                ICardAttribute attr = (ICardAttribute) obj;
                if (!columns.contains(attr.getName())) {
                    columns.add(attr.getName());
                }
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return columns;
    }

    // This method returns a buffered image with the contents of an image
    public BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge
                = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            if (width > 0 || height > 0) {
                bimage = gc.createCompatibleImage(
                        image.getWidth(null), image.getHeight(null), 
                        transparency);
            }
        } catch (HeadlessException e) {
            // The system does not have a screen
            LOG.log(Level.SEVERE,
                    "The system does not have a screen", e);
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    public boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            LOG.log(Level.SEVERE,
                    null, e);
            return false;
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm == null ? false : cm.hasAlpha();
    }
}
