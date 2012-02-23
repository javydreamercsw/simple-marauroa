package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.CardCollectionType;
import dreamer.card.game.storage.database.persistence.Game;
import dreamer.card.game.storage.database.persistence.controller.GameJpaController;
import java.util.ArrayList;
import java.util.HashMap;
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

    protected static HashMap<String, List<String>> attribs = new HashMap<String, List<String>>();
    protected static ArrayList<String> collectionTypes = new ArrayList<String>();
    protected static HashMap<String, String> collections = new HashMap<String, String>();

    @Override
    public void init() {
        GameJpaController controller = new GameJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
        parameters.put("name", getName());
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            if (result.isEmpty()) {
                controller.create(new Game(getName()));
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.FINE,
                        "Created game: " + getName() + " on the database!");
            }

            //Create attributes
            for (Entry<String, List<String>> entry : attribs.entrySet()) {
                Lookup.getDefault().lookup(IDataBaseManager.class).createAttributes(entry.getKey(), entry.getValue());
            }

            //Create default Collection Types
            for (String type : collectionTypes) {
                Lookup.getDefault().lookup(IDataBaseManager.class).createCardCollectionType(type);
            }

            //Create default Collections
            for (Entry<String, String> entry : collections.entrySet()) {
                parameters.put("name", entry.getKey());
                CardCollectionType type = (CardCollectionType) Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardCollectionType.findByName", parameters).get(0);
                Lookup.getDefault().lookup(IDataBaseManager.class).createCardCollection(type, entry.getValue());
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
