/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game.storage;

import dreamer.card.game.storage.database.persistence.*;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.Date;
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
    public void createAttributes(String type, List<String> values) throws Exception ;
    
    /**
     * Create a card type in the database.
     * @param type card type
     * @return Created card type
     */
    public CardType createCardType(String type)throws  Exception ;
    
    /**
     * Create a card
     * @param type CardType
     * @param name Card name
     * @param text Card text
     * @return Created card
     */
    public Card createCard(CardType type, String name, byte[] text)throws PreexistingEntityException, Exception ;
    
    /**
     * Add attribute to card
     * @param card Card to add attribute to
     * @param attr Attribute to add
     * @param value Value of attribute
     */
    public void addAttributeToCard(Card card, CardAttribute attr, String value)throws PreexistingEntityException, Exception ;
    
    /**
     * Create a card set
     * @param game Game the set is from
     * @param name Name of the set
     * @param abbreviation Abbreviation of the set
     * @param released Release date
     * @return Created CardSet
     */
    public CardSet createCardSet(Game game, String name, String abbreviation, Date released) throws PreexistingEntityException, Exception ;
    
    /**
     * Add cards to set
     * @param cards Cards to add
     * @param cs CardSet to be added to
     */
    public void addCardsToSet(List<Card> cards, CardSet cs) throws NonexistentEntityException, Exception;
    
    /**
     * Print the cards in a set
     * @param cs CardSet
     * @return String listing the pages in a set
     */
    public String printCardsInSet(CardSet cs);
}
