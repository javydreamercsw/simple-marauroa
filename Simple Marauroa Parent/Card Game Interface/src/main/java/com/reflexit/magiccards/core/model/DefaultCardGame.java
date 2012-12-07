package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.cache.ICardCache;
import com.reflexit.magiccards.core.model.storage.db.DBException;
import com.reflexit.magiccards.core.model.storage.db.DataBaseStateListener;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class DefaultCardGame implements ICardGame, DataBaseStateListener {

    protected static final List<String> attribs = new ArrayList<String>();
    protected static final ArrayList<String> collectionTypes = new ArrayList<String>();
    protected static final HashMap<String, String> collections = new HashMap<String, String>();
    private static final Logger LOG = Logger.getLogger(DefaultCardGame.class.getName());

    @Override
    public void init() {
        //Games auto register themselves
        Lookup.getDefault().lookup(IDataBaseCardStorage.class).addDataBaseStateListener(this);
    }

    @Override
    public void initialized() {
        HashMap parameters = new HashMap();
        try {
            synchronized (attribs) {
                //Create game attributes
                for (Iterator<String> it = attribs.iterator(); it.hasNext();) {
                    String attr = it.next();
                    Lookup.getDefault().lookup(IDataBaseCardStorage.class).createAttributes(attr);
                }
            }
            //Create default collection types
            synchronized (collectionTypes) {
                for (Iterator<String> it = collectionTypes.iterator(); it.hasNext();) {
                    String type = it.next();
                    Lookup.getDefault().lookup(IDataBaseCardStorage.class).createCardCollectionType(type);
                }
            }
            //Create default Collections
            synchronized (collections) {
                for (Iterator<Entry<String, String>> it = collections.entrySet().iterator(); it.hasNext();) {
                    Entry<String, String> entry = it.next();
                    parameters.put("name", entry.getKey());
                    ICardCollectionType type = (ICardCollectionType) Lookup.getDefault().lookup(IDataBaseCardStorage.class).namedQuery("CardCollectionType.findByName", parameters).get(0);
                    Lookup.getDefault().lookup(IDataBaseCardStorage.class).createCardCollection(type, entry.getValue());
                }
            }
        } catch (DBException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<ICardCache> getCardCacheImplementations() {
        ArrayList<ICardCache> caches = new ArrayList<ICardCache>();
        for (Iterator<? extends ICardCache> it = 
                Lookup.getDefault().lookupAll(ICardCache.class).iterator(); 
                it.hasNext();) {
            ICardCache cache = it.next();
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
            return Lookup.getDefault().lookup(ICardCache.class).getGameIcon((ICardGame) this);
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
        ArrayList<ICardAttributeFormatter> formatters = new ArrayList<ICardAttributeFormatter>();
        for (ICardAttributeFormatter formatter : Lookup.getDefault().lookupAll(ICardAttributeFormatter.class)) {
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
            List result = Lookup.getDefault().lookup(IDataBaseCardStorage.class).namedQuery("Game.findByName", parameters);
            if (result.isEmpty()) {
                throw new RuntimeException("Unable to find game " + getName() + " in database!");
            }
            parameters.clear();
            return Lookup.getDefault().lookup(IDataBaseCardStorage.class).getSetsForGame((IGame) result.get(0));
        } catch (DBException ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
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
            List result = Lookup.getDefault().lookup(IDataBaseCardStorage.class).createdQuery(
                    "select distinct chca.cardAttribute from "
                    + "CardHasCardAttribute chca, Card c, CardSet cs, Game g"
                    + " where cs.game =g and g.name =:game and cs member of c.cardSetList"
                    + " and chca.card =c order by chca.cardAttribute.name", parameters);
            for (Object obj : result) {
                ICardAttribute attr = (ICardAttribute) obj;
                if (!columns.contains(attr.getName())) {
                    columns.add(attr.getName());
                }
            }
        } catch (DBException ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return columns;
    }
}
