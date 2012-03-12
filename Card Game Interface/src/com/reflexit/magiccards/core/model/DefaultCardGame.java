package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.cache.ICardCache;
import com.reflexit.magiccards.core.model.storage.db.DBException;
import com.reflexit.magiccards.core.model.storage.db.IDataBaseCardStorage;
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
public abstract class DefaultCardGame implements ICardGame {

    protected static final List<String> attribs = new ArrayList<String>();
    protected static final ArrayList<String> collectionTypes = new ArrayList<String>();
    protected static final HashMap<String, String> collections = new HashMap<String, String>();

    @Override
    public void init() {
        HashMap parameters = new HashMap();
        try {
            parameters.put("name", getName());
            if (Lookup.getDefault().lookup(IDataBaseCardStorage.class).namedQuery("Game.findByName", parameters).isEmpty()) {
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createGame(getName());
            }
        } catch (DBException ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //Create game attributes
            for (Iterator<String> it = attribs.iterator(); it.hasNext();) {
                String attr = it.next();
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createAttributes(attr);
            }
            //Create default collection types
            for (Iterator<String> it = collectionTypes.iterator(); it.hasNext();) {
                String type = it.next();
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createCardCollectionType(type);
            }
            //Create default Collections
            for (Iterator<Entry<String, String>> it = collections.entrySet().iterator(); it.hasNext();) {
                Entry<String, String> entry = it.next();
                parameters.put("name", entry.getKey());
                ICardCollectionType type = (ICardCollectionType) Lookup.getDefault().lookup(IDataBaseCardStorage.class).namedQuery("CardCollectionType.findByName", parameters).get(0);
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createCardCollection(type, entry.getValue());
            }
        } catch (DBException ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
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
}
