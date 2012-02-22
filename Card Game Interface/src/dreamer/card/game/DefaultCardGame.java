/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.CardAttribute;
import dreamer.card.game.storage.database.persistence.CardAttributeType;
import dreamer.card.game.storage.database.persistence.Game;
import dreamer.card.game.storage.database.persistence.controller.CardAttributeJpaController;
import dreamer.card.game.storage.database.persistence.controller.CardAttributeTypeJpaController;
import dreamer.card.game.storage.database.persistence.controller.GameJpaController;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class DefaultCardGame implements ICardGame {

    @Override
    public void init() {
        GameJpaController controller = new GameJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
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
    }
    
    protected void createAttributes(String type, List<String> values){
        CardAttributeTypeJpaController catController = new CardAttributeTypeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
        HashMap parameters = new HashMap();
        String value = type;
        parameters.put("name", value);
        CardAttributeType attrType = null;
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
            if (result.isEmpty()) {
                attrType = new CardAttributeType(value);
                catController.create(attrType);
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.ALL,
                        "Created attribute type: " + value + " on the database!");
            } else {
                attrType = (CardAttributeType) result.get(0);
            }
        } catch (Exception ex) {
            Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Add Card Attributes
        for (String rarity : values) {
            CardAttributeJpaController caController = new CardAttributeJpaController(Lookup.getDefault().lookup(IDataBaseManager.class).getEntityManagerFactory());
            parameters.clear();
            try {
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                if (result.isEmpty()) {
                    CardAttribute attr = new CardAttribute(attrType.getId());
                    attr.setName(rarity);
                    caController.create(attr);
                    Logger.getLogger(DefaultCardGame.class.getName()).log(Level.ALL,
                            "Created attribute: " + rarity + " on the database!");
                }
            } catch (Exception ex) {
                Logger.getLogger(DefaultCardGame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
