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
import java.util.Map;
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

    /**
     * Execute a native query (no results)
     *
     * @param query
     * @throws Exception
     */
    public void nativeQuery(String query) throws Exception;

    /**
     * Execute a native query (with results)
     *
     * @param query
     * @return results
     * @throws Exception
     */
    public List<Object> namedQuery(String query) throws Exception;

    /**
     * Execute a native query (with results)
     *
     * @param query
     * @param parameters
     * @return results
     * @throws Exception
     */
    public List<Object> namedQuery(String query, HashMap<String, Object> parameters) throws Exception;

    /**
     * Close the database
     */
    public void close();

    /**
     * Set specific Persistence Unit
     *
     * @param pu new persistence unit
     */
    public void setPU(String pu);

    /**
     * Create attributes in the database
     *
     * @param type attribute type
     * @throws Exception
     */
    public void createAttributes(String type) throws Exception;

    /**
     * Create a card type in the database.
     *
     * @param type card type
     * @return Created card type
     * @throws Exception
     */
    public CardType createCardType(String type) throws Exception;

    /**
     * Create a card
     *
     * @param type CardType
     * @param name Card name
     * @param text Card text
     * @return Created card
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public Card createCard(CardType type, String name, byte[] text) throws PreexistingEntityException, Exception;

    /**
     * Add attribute to card
     *
     * @param card Card to add attribute to
     * @param attr Attribute to add
     * @return CardHasCardAttribute
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public CardHasCardAttribute addAttributeToCard(Card card, CardAttribute attr) throws PreexistingEntityException, Exception;

    /**
     * Create a card set
     *
     * @param game Game the set is from
     * @param name Name of the set
     * @param abbreviation Abbreviation of the set
     * @param released Release date
     * @return Created CardSet
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public CardSet createCardSet(Game game, String name, String abbreviation, Date released) throws PreexistingEntityException, Exception;

    /**
     * Add cards to set
     *
     * @param cards Cards to add
     * @param cs CardSet to be added to
     * @throws NonexistentEntityException
     * @throws Exception
     */
    public void addCardsToSet(List<Card> cards, CardSet cs) throws NonexistentEntityException, Exception;

    /**
     * Print the cards in a set
     *
     * @param cs CardSet
     * @return String listing the pages in a set
     */
    public String printCardsInSet(CardSet cs);

    /**
     * Create a card collection type (deck, owned pages, etc)
     *
     * @param name collection's name
     * @return the created collection type
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public CardCollectionType createCardCollectionType(String name) throws PreexistingEntityException, Exception;

    /**
     * Create a card collection
     *
     * @param type Collection type
     * @param name Collection name
     * @return Created Card Collection
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public CardCollection createCardCollection(CardCollectionType type, String name) throws PreexistingEntityException, Exception;

    /**
     * Add cards to a collection
     *
     * @param cards Cards to add
     * @param collection Collection to add the pages to
     * @return Updated CardCollection
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public CardCollection addCardsToCollection(HashMap<Card, Integer> cards, CardCollection collection) throws PreexistingEntityException, Exception;

    /**
     * Remove cards from collection
     *
     * @param cards Cards to remove
     * @param collection Collection to remove the pages from
     * @return Updated CardCollection
     * @throws PreexistingEntityException
     * @throws Exception
     */
    public CardCollection removeCardsFromCollection(HashMap<Card, Integer> cards, CardCollection collection) throws PreexistingEntityException, Exception;

    /**
     * Print Card Collection contents
     *
     * @param cc Card Collection to print
     * @return String listing the pages in a collection
     */
    public String printCardsCollection(CardCollection cc);

    /**
     * Check if attribute exists in data base
     *
     * @param attr
     * @return true if exists
     */
    public boolean attributeExists(String attr);

    /**
     * Execute a created query
     *
     * @param query
     * @return Results
     * @throws Exception
     */
    public List<Object> createdQuery(String query) throws Exception;

    /**
     * Execute a created query
     *
     * @param query
     * @param parameters
     * @return results
     * @throws Exception
     */
    public List<Object> createdQuery(String query, HashMap<String, Object> parameters) throws Exception;

    /**
     * Get CardAttribute from database
     *
     * @param attr attribute name
     * @return CardAttribute
     * @throws Exception
     */
    public CardAttribute getCardAttribute(String attr) throws Exception;

    /**
     * Add a set of attributes to a card (Map<Attribute Type, Attribute name>)
     *
     * @param card Card to add attributes to
     * @param attributes Set of attributes
     * @throws Exception
     */
    public void addAttributesToCard(Card card, Map<String, String> attributes) throws Exception;

    /**
     * Create a an attribute if needed
     *
     * @param attr attribute name
     * @param value Attribute value
     * @throws Exception
     */
    public void createAttributeIfNeeded(String attr, String value) throws Exception;

    /**
     * Get a map of attributes for a card
     * @param name card's name
     * @return map of attributes for a card
     * @throws Exception
     */
    public Map<String, String> getAttributesForCard(String name) throws Exception;

    /**
     * Get a map of attributes for a card
     * @param card Card
     * @return map of attributes for a card
     */
    public Map<String, String> getAttributesForCard(Card card);
}
