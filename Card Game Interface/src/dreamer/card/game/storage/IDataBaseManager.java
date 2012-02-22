/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage;

import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardAttribute;
import dreamer.card.game.storage.database.persistence.CardType;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IDataBaseManager {

    /**
     * @return the Entity Manager Factory
     */
    public EntityManagerFactory getEntityManagerFactory();

    /**
     * @return the Entity Manager
     */
    public EntityManager getEntityManager();
    
    public void nativeQuery(String query) throws Exception;

    public List<Object> namedQuery(String query) throws Exception;

    public List<Object> namedQuery(String query, HashMap<String, Object> parameters) throws Exception;
    
    public void close();
    
    /**
     * Set specific Persistence Unit
     * @param pu new persistence unit
     */
    public void setPU(String pu);
    
    /**
     * Create attributes in the database
     * @param type attribute type
     * @param values list of possible values
     */
    public void createAttributes(String type, List<String> values);
    
    /**
     * Create a card type in the database.
     * @param type card type
     * @return Created card type
     */
    public CardType createCardType(String type);
    
    /**
     * Create a card
     * @param type CardType
     * @param name Card name
     * @param text Card text
     * @return Created card
     */
    public Card createCard(CardType type, String name, byte[] text);
    
    /**
     * Add attribute to card
     * @param card Card to add attribute to
     * @param attr Attribute to add
     * @param value Value of attribute
     */
    public void addAttributeToCard(Card card, CardAttribute attr, String value);
}
