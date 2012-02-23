package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.Game;
import dreamer.card.game.storage.database.persistence.controller.GameJpaController;
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

    @Override
    public void init() {
        GameJpaController controller = new GameJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
        parameters.put("name", getName());
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            if (result.isEmpty()) {
                controller.create(new Game(getName()));
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.ALL,
                        "Created game: " + getName() + " on the database!");
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //Create attributes
            for (Entry<String, List<String>> entry : attribs.entrySet()) {
                Lookup.getDefault().lookup(IDataBaseManager.class).createAttributes(entry.getKey(), entry.getValue());
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
