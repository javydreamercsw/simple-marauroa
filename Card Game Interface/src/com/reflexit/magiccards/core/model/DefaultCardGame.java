package com.reflexit.magiccards.core.model;

import com.reflexit.magiccards.core.cache.ICardCache;
import com.reflexit.magiccards.core.model.storage.IDataBaseCardStorage;
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
        Lookup.getDefault().lookup(IDataBaseCardStorage.class).createGame(getName());
        HashMap parameters = new HashMap();
        try {
            for (String attr : attribs) {
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createAttributes(attr);
            }
            for (Iterator<String> it = collectionTypes.iterator(); it.hasNext();) {
                String type = it.next();
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createCardCollectionType(type);
            }
            for (Iterator<Entry<String, String>> it = collections.entrySet().iterator(); it.hasNext();) {
                Entry<String, String> entry = it.next();
                parameters.put("name", entry.getKey());
                ICardCollectionType type = (ICardCollectionType) Lookup.getDefault().lookup(IDataBaseCardStorage.class).namedQuery("CardCollectionType.findByName", parameters).get(0);
                Lookup.getDefault().lookup(IDataBaseCardStorage.class).createCardCollection(type, entry.getValue());
            }
        } catch (Exception ex) {
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
}
