package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.model.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @param <T>
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IDataBaseCardStorage<T> extends IStorage<T> {

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
    public ICardType createCardType(String type) throws Exception;

    /**
     * Create a card
     *
     * @param type CardType
     * @param name Card name
     * @param text Card text
     * @return Created card
     * @throws Exception
     */
    public ICard createCard(ICardType type, String name, byte[] text) throws Exception;

    /**
     * Add attribute to card
     *
     * @param card Card to add attribute to
     * @param attr Attribute to add
     * @return CardHasCardAttribute
     * @throws Exception
     */
    public ICardHasCardAttribute addAttributeToCard(ICard card, ICardAttribute attr) throws Exception;

    /**
     * Create a card set
     *
     * @param game Game the set is from
     * @param name Name of the set
     * @param abbreviation Abbreviation of the set
     * @param released Release date
     * @return Created CardSet
     * @throws Exception
     */
    public ICardSet createCardSet(IGame game, String name, String abbreviation, Date released) throws Exception;

    /**
     * Add cards to set
     *
     * @param cards Cards to add
     * @param cs CardSet to be added to
     * @throws Exception
     */
    public void addCardsToSet(List<ICard> cards, ICardSet cs) throws Exception;

    /**
     * Print the cards in a set
     *
     * @param cs CardSet
     * @return String listing the pages in a set
     */
    public String printCardsInSet(ICardSet cs);

    /**
     * Create a card collection type (deck, owned pages, etc)
     *
     * @param name collection's name
     * @return the created collection type
     * @throws Exception
     */
    public ICardCollectionType createCardCollectionType(String name) throws Exception;

    /**
     * Create a card collection
     *
     * @param type Collection type
     * @param name Collection name
     * @return Created Card Collection
     * @throws Exception
     */
    public ICardCollection createCardCollection(ICardCollectionType type, String name) throws Exception;

    /**
     * Add cards to a collection
     *
     * @param cards Cards to add
     * @param collection Collection to add the pages to
     * @return Updated CardCollection
     * @throws Exception
     */
    public ICardCollection addCardsToCollection(HashMap<ICard, Integer> cards, ICardCollection collection) throws Exception;

    /**
     * Remove cards from collection
     *
     * @param cards Cards to remove
     * @param collection Collection to remove the pages from
     * @return Updated CardCollection
     * @throws Exception
     */
    public ICardCollection removeCardsFromCollection(HashMap<ICard, Integer> cards, ICardCollection collection) throws Exception;

    /**
     * Print Card Collection contents
     *
     * @param cc Card Collection to print
     * @return String listing the pages in a collection
     */
    public String printCardsCollection(ICardCollection cc);

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
    public ICardAttribute getCardAttribute(String attr) throws Exception;

    /**
     * Add a set of attributes to a card (Map<Attribute Type, Attribute name>)
     *
     * @param card Card to add attributes to
     * @param attributes Set of attributes
     * @throws Exception
     */
    public void addAttributesToCard(ICard card, Map<String, String> attributes) throws Exception;

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
     *
     * @param name card's name
     * @return map of attributes for a card
     * @throws Exception
     */
    public Map<String, String> getAttributesForCard(String name) throws Exception;

    /**
     * Get a map of attributes for a card
     *
     * @param card Card
     * @return map of attributes for a card
     */
    public Map<String, String> getAttributesForCard(ICard card);

    /**
     * Set data base connection properties.
     *
     * @param dataBaseProperties the DataBase Properties to set
     */
    public void setDataBaseProperties(Map<String, String> dataBaseProperties);

    /**
     * Check if card type exists
     *
     * @param name type name
     * @return true if it exists, false otherwise
     */
    public boolean cardTypeExists(String name);

    /**
     * Get the card's attribute value.
     *
     * @param card Card to check
     * @param name Attribute name
     * @return Value or null if not found
     */
    public String getCardAttribute(ICard card, String name);

    /**
     * Create a game in the database
     * @param name game name
     * @return created game 
     */
    public IGame createGame(String name);
}
