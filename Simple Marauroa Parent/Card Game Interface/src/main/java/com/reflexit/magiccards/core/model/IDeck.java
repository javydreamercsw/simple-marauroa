package com.reflexit.magiccards.core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A deck of cards
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public interface IDeck {

    /**
     * Deck contents
     */
    List<ICard> deck = new ArrayList<ICard>();
    /**
     * Used cards
     */
    List<ICard> used = new ArrayList<ICard>();
    /**
     * Cards in hand
     */
    List<ICard> hand = new ArrayList<ICard>();

    /**
     * Get decks contents
     *
     * @return decks contents
     */
    public List<ICard> getCards();

    /**
     * Get used cards contents
     *
     * @return used cards contents
     */
    public List<ICard> getUsedCards();

    /**
     * Ditch first card of that type
     *
     * @param type Type to ditch
     * @param random true if cards to be selected from random places on deck.
     * @param amount cards to be ditched
     * @return Cards ditched
     */
    public List<ICard> ditch(Class<? extends ICardType> type, boolean random, int amount);

    /**
     * Ditch first card of that type
     *
     * @param type Type to ditch
     * @return Card ditched
     */
    public ICard ditch(Class<? extends ICardType> type);

    /**
     * Ditch bottom card
     *
     * @return Card ditched
     */
    public ICard ditchBottom();

    /**
     * Ditch x amount of cards
     *
     * @param amount amount to ditch
     * @param random true if cards to be selected from random places on deck.
     * False otherwise.
     * @return Ditched cards
     */
    public List<ICard> ditch(int amount, boolean random);

    /**
     * Ditch 1 card
     *
     * @param random true if cards to be selected from random places on deck.
     * False otherwise.
     * @return Ditched cards
     */
    public ICard ditch(boolean random);

    /**
     * Ditch x amount of cards. Equivalent to ditch(x, false)
     *
     * @param amount amount to ditch
     * @return Ditched cards
     */
    public List<ICard> ditch(int amount);

    /**
     * Ditch 1 card. Equivalent to ditch(1, false)
     *
     * @return Ditched cards
     */
    public ICard ditch();

    /**
     * Ditch first card of that type
     *
     * @param type Type to draw
     * @return Card drawn
     */
    public ICard draw(Class<? extends ICardType> type);

    /**
     * Draw top card. Same as draw(false)
     *
     * @return Card drawn
     */
    public ICard draw();

    /**
     * Draw top card
     *
     * @param random true if cards to be selected from random places on deck.
     * @return Card drawn
     */
    public ICard draw(boolean random);

    /**
     * Draw bottom card
     *
     * @return Card drawn
     */
    public ICard drawBottom();

    /**
     * Draw x amount of cards
     *
     * @param amount amount to draw
     * @param random true if cars to be selected from random places on deck.
     * False otherwise.
     * @return Drawn cards
     */
    public List<ICard> draw(int amount, boolean random);

    /**
     * Draw x amount of cards. Equivalent to draw(x, false)
     *
     * @param amount amount to ditch
     * @return Drawn cards
     */
    public List<ICard> draw(int amount);

    /**
     * Shuffle the deck
     */
    public void shuffle();

    /**
     * Amount of pages left in deck
     *
     * @return size
     */
    public int getSize();

    /**
     * Amount of pages in used pile
     *
     * @return size
     */
    public int getUsedPileSize();

    /**
     * Get deck name
     *
     * @return deck name
     */
    public String getName();
}
