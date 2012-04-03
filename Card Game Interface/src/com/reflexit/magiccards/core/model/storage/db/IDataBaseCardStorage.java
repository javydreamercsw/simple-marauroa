package com.reflexit.magiccards.core.model.storage.db;

import com.reflexit.magiccards.core.model.*;
import com.reflexit.magiccards.core.model.storage.IStorage;
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
     * @throws DBException
     */
    public void nativeQuery(String query) throws DBException;

    /**
     * Execute a native query (with results)
     *
     * @param query
     * @return results
     * @throws DBException
     */
    public List<Object> namedQuery(String query) throws DBException;

    /**
     * Execute a native query (with results)
     *
     * @param query
     * @param parameters
     * @return results
     * @throws DBException
     */
    public List<Object> namedQuery(String query, HashMap<String, Object> parameters) throws DBException;

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
     * @throws DBException
     */
    public void createAttributes(String type) throws DBException;

    /**
     * Create a card type in the database.
     *
     * @param type card type
     * @return Created card type
     * @throws DBException
     */
    public ICardType createCardType(String type) throws DBException;

    /**
     * Create a card
     *
     * @param type CardType
     * @param name Card name
     * @param text Card text
     * @return Created card
     * @throws DBException
     */
    public ICard createCard(ICardType type, String name, byte[] text) throws DBException;

    /**
     * Add attribute to card
     *
     * @param card Card to add attribute to
     * @param attr Attribute to add
     * @param value Value to set
     * @return CardHasCardAttribute
     * @throws DBException
     */
    public ICardHasCardAttribute addAttributeToCard(ICard card, ICardAttribute attr, String value) throws DBException;

    /**
     * Create a card set
     *
     * @param game Game the set is from
     * @param name Name of the set
     * @param abbreviation Abbreviation of the set
     * @param released Release date
     * @return Created CardSet
     * @throws DBException
     */
    public ICardSet createCardSet(IGame game, String name, String abbreviation, Date released) throws DBException;

    /**
     * Add cards to set
     *
     * @param cards Cards to add
     * @param cs CardSet to be added to
     * @throws DBException
     */
    public void addCardsToSet(List<ICard> cards, ICardSet cs) throws DBException;

    /**
     * Add card to set
     *
     * @param card Card to add
     * @param set CardSet to be added to
     * @throws DBException
     */
    public void addCardToSet(ICard card, ICardSet set) throws DBException;

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
     * @throws DBException
     */
    public ICardCollectionType createCardCollectionType(String name) throws DBException;

    /**
     * Create a card collection
     *
     * @param type Collection type
     * @param name Collection name
     * @return Created Card Collection
     * @throws DBException
     */
    public ICardCollection createCardCollection(ICardCollectionType type, String name) throws DBException;

    /**
     * Add cards to a collection
     *
     * @param cards Cards to add
     * @param collection Collection to add the pages to
     * @return Updated CardCollection
     * @throws DBException
     */
    public ICardCollection addCardsToCollection(HashMap<ICard, Integer> cards, ICardCollection collection) throws DBException;

    /**
     * Remove cards from collection
     *
     * @param cards Cards to remove
     * @param collection Collection to remove the pages from
     * @return Updated CardCollection
     * @throws DBException
     */
    public ICardCollection removeCardsFromCollection(HashMap<ICard, Integer> cards, ICardCollection collection) throws DBException;

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
     * @throws DBException
     */
    public List<Object> createdQuery(String query) throws DBException;

    /**
     * Execute a created query
     *
     * @param query
     * @param parameters
     * @return results
     * @throws DBException
     */
    public List<Object> createdQuery(String query, HashMap<String, Object> parameters) throws DBException;

    /**
     * Get CardAttribute from database
     *
     * @param attr attribute name
     * @return CardAttribute
     * @throws DBException
     */
    public ICardAttribute getCardAttribute(String attr) throws DBException;

    /**
     * Add a set of attributes to a card (Map<Attribute Type, Attribute name>)
     *
     * @param card Card to add attributes to
     * @param attributes Set of attributes
     * @throws DBException
     */
    public void addAttributesToCard(ICard card, Map<String, String> attributes) throws DBException;

    /**
     * Create a an attribute if needed
     *
     * @param attr attribute name
     * @param value Attribute value
     * @throws DBException
     */
    public void createAttributeIfNeeded(String attr, String value) throws DBException;

    /**
     * Get a map of attributes for a card
     *
     * @param name card's name
     * @return map of attributes for a card
     * @throws DBException
     */
    public Map<String, String> getAttributesForCard(String name) throws DBException;

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
     *
     * @param name game name
     * @return created game
     * @throws DBException
     */
    public IGame createGame(String name) throws DBException;

    /**
     * Initialize the database
     *
     * @throws DBException
     */
    public void initialize() throws DBException;

    /**
     * Check if set exists
     *
     * @param name set name
     * @return true if exists
     */
    public boolean cardSetExists(String name);

    /**
     * Check if card exists
     *
     * @param name card name
     * @return true if exists
     */
    public boolean cardExists(String name);

    /**
     * Check if game exists
     *
     * @param name game name
     * @return true if exists
     */
    public boolean gameExists(String name);

    /**
     * Get the cards for the current game
     *
     * @param set
     * @return List of cards
     */
    public List<ICard> getCardsForSet(ICardSet set);

    /**
     * Get all games
     *
     * @return List of games
     */
    public List<IGame> getGames();

    /**
     * Get sets for game
     *
     * @param game game to get sets for
     * @return list of sets
     */
    public List<ICardSet> getSetsForGame(IGame game);

    /**
     * Get cards for game
     *
     * @param game game to get cards for
     * @return list of cards
     */
    public List<ICard> getCardsForGame(IGame game);

    /**
     * Check if card is already in set
     *
     * @param set Set to check on
     * @param card Card to check
     * @return true if already part of set.
     */
    public boolean setHasCard(ICardSet set, ICard card);

    /**
     * Add a DataBaseStateListener
     *
     * @param listener DataBaseStateListener
     */
    public void addDataBaseStateListener(DataBaseStateListener listener);

    /**
     * Remove a DataBaseStateListener
     *
     * @param listener DataBaseStateListener
     */
    public void removeDataBaseStateListener(DataBaseStateListener listener);

    /**
     * Get the required information to connect to the database
     *
     * @return required information to connect to the database
     */
    public Map<String, String> getConnectionSettings();
}
