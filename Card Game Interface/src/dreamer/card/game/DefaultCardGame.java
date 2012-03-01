package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.cache.ICardCache;
import dreamer.card.game.storage.database.persistence.CardCollectionType;
import dreamer.card.game.storage.database.persistence.Game;
import dreamer.card.game.storage.database.persistence.controller.GameJpaController;
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
        GameJpaController controller = new GameJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
        parameters.put("name", getName());
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            if (result.isEmpty()) {
                controller.create(new Game(getName()));
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.FINE, "Created game: {0} on the database!", getName());
            }
            for (String attr : attribs) {
                Lookup.getDefault().lookup(IDataBaseManager.class).createAttributes(attr);
            }
            for (Iterator<String> it = collectionTypes.iterator(); it.hasNext();) {
                String type = it.next();
                Lookup.getDefault().lookup(IDataBaseManager.class).createCardCollectionType(type);
            }
            for (Iterator<Entry<String, String>> it = collections.entrySet().iterator(); it.hasNext();) {
                Entry<String, String> entry = it.next();
                parameters.put("name", entry.getKey());
                CardCollectionType type = (CardCollectionType) Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardCollectionType.findByName", parameters).get(0);
                Lookup.getDefault().lookup(IDataBaseManager.class).createCardCollection(type, entry.getValue());
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<ICardCache> getCardCacheImplementations() {
        ArrayList<ICardCache> caches = new ArrayList<ICardCache>();
        for (ICardCache cache : Lookup.getDefault().lookupAll(ICardCache.class)) {
            if(cache.getGameName().equals(getName())){
                caches.add(cache);
            }
        }
        return caches;
    }
}
