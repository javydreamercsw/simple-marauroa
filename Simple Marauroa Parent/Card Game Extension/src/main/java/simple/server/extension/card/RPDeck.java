package simple.server.extension.card;

import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardType;
import com.reflexit.magiccards.core.model.IDeck;
import java.util.*;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1002)
public class RPDeck extends RPEntity implements IDeck {

    public static final String PAGES = "pages",
            HAND = "hand", WINS = "wins", LOSES = "loses", DRAWS = "draws",
            VERSION = "version", RECORD = "record", CLASS_NAME = "deck",
            DISCARD_PILE = "discard";
    protected Random rand = new Random();

    public RPDeck() {
    }

    public RPDeck(final String name) {
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        put("name", name);
        update();
    }

    public RPDeck(final String name, final List<RPCard> cards, final List<RPCard> hand) {
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        put("name", name);
        update();
        for (RPCard card : hand) {
            addToHand(card);
        }
        for (RPCard card : cards) {
            addToDeck(card);
        }
    }

    @Override
    public void update() {
        super.update();
        if (!has(VERSION)) {
            put(VERSION, 1);
        }
        if (!hasMap(RECORD)) {
            addMap(RECORD);
        }
        if (!getMap(RECORD).containsKey(LOSES)) {
            put(RECORD, LOSES, 0);
        }
        if (!getMap(RECORD).containsKey(DRAWS)) {
            put(RECORD, DRAWS, 0);
        }
        if (!getMap(RECORD).containsKey(WINS)) {
            put(RECORD, WINS, 0);
        }
        if (!hasSlot(PAGES)) {
            addSlot(PAGES);
        }
        if (!hasSlot(HAND)) {
            addSlot(HAND);
        }
        if (!hasSlot(DISCARD_PILE)) {
            addSlot(DISCARD_PILE);
        }
    }

    private void increaseRecord(final String type) {
        if (type.equals(LOSES)) {
            put(RECORD, type,
                    String.valueOf(getLoses() + 1));
        } else if (type.equals(WINS)) {
            put(RECORD, type,
                    String.valueOf(getWins() + 1));
        } else if (type.equals(DRAWS)) {
            put(RECORD, type,
                    String.valueOf(getDraws() + 1));
        } else {
            throw new RuntimeException("Tried to add an invalid record attribute: " + type);
        }
    }

    /**
     * Add a loss to the record. Added to the current version.
     */
    public final void addLoss() {
        increaseRecord(LOSES);
    }

    /**
     * Add a win to the record. Added to the current version.
     */
    public final void addWin() {
        increaseRecord(WINS);
    }

    /**
     * Add a draw to the record. Added to the current version.
     */
    public final void addDraw() {
        increaseRecord(DRAWS);
    }

    /**
     * Get wins
     *
     * @return wins
     */
    public final int getWins() {
        return Integer.valueOf(get(RECORD, WINS));
    }

    /**
     * Get loses
     *
     * @return loses
     */
    public final int getLoses() {
        return Integer.valueOf(get(RECORD, LOSES));
    }

    /**
     * Get draws
     *
     * @return draws
     */
    public final int getDraws() {
        return Integer.valueOf(get(RECORD, DRAWS));
    }

    public void increaseVersion() {
        put(VERSION, getVersion() + 1);
    }

    public final int getVersion() {
        return getInt(VERSION);
    }

    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(CLASS_NAME)) {
            RPClass entity = new RPClass(CLASS_NAME);
            entity.isA("entity");

            /**
             * RPCards
             */
            entity.addRPSlot(PAGES, -1, Definition.PRIVATE);

            /**
             * Starting hand
             */
            entity.addRPSlot(HAND, -1, Definition.PRIVATE);

            /**
             * Discard Pile
             */
            entity.addRPSlot(DISCARD_PILE, -1, Definition.PRIVATE);

            /**
             * Deck version. Starts at 1 and increases each time the deck is
             * modified
             */
            entity.addAttribute(VERSION, Definition.Type.INT);

            /**
             * Deck record
             */
            entity.addAttribute(RECORD, Definition.Type.MAP);
        }
    }

    public final void addToHand(final RPCard card) {
        getSlot(HAND).add(card);
    }

    public final void addToDeck(final RPCard card) {
        getSlot(PAGES).add(card);
    }

    public List<ICard> getHand() {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        for (Iterator<RPObject> it = getSlot(RPDeck.HAND).iterator(); it.hasNext();) {
            cards.add((RPCard) it.next());
        }
        return cards;
    }

    protected List<ICard> getDeck() {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        for (Iterator<RPObject> it = getSlot(RPDeck.PAGES).iterator(); it.hasNext();) {
            cards.add((RPCard) it.next());
        }
        return cards;
    }

    protected List<ICard> getDiscardPile() {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        for (Iterator<RPObject> it = getSlot(RPDeck.DISCARD_PILE).iterator(); it.hasNext();) {
            cards.add((RPCard) it.next());
        }
        return cards;
    }

    @Override
    public List<ICard> getCards() {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        cards.addAll(getDeck());
        return cards;
    }

    @Override
    public List<ICard> getUsedCards() {
        ArrayList<ICard> cards = new ArrayList<ICard>();
        cards.addAll(getDiscardPile());
        return cards;
    }

    @Override
    public List<ICard> ditch(final String slot, final Class<? extends ICardType> type, final boolean random, final int amount) {
        ArrayList<ICard> ditched = new ArrayList<ICard>();
        if (random) {
            ArrayList<Integer> indices = new ArrayList<Integer>();
            //Build a list of indices of this type
            int index = 0;
            for (final Iterator it = getSlot(PAGES).iterator(); it.hasNext();) {
                if (((IMarauroaCard) it).getLookup().lookup(type) != null) {
                    indices.add(index);
                }
                index++;
            }
            Collections.sort(indices);
            //Now randomly pick one from the list to be removed
            int remove = indices.size() < 0 ? 0
                    : (indices.size() < amount ? indices.size() : amount);
            int count = 0;
            for (int j = 0; j < remove; j++) {
                int toRemove = indices.remove(rand.nextInt(indices.size()));
                for (final Iterator i = getSlot(PAGES).iterator(); i.hasNext();) {
                    IMarauroaCard next = (IMarauroaCard) i.next();
                    if (count == toRemove) {
                        ditched.add(next);
                    }
                    count++;
                }
            }
        } else {
            for (int j = 0; j < amount; j++) {
                for (final Iterator i = getSlot(PAGES).iterator(); i.hasNext();) {
                    IMarauroaCard next = (IMarauroaCard) i.next();
                    if (next.getLookup().lookup(type) != null) {
                        ditched.add(next);
                        break;
                    }
                }
            }
        }
        for (Iterator<ICard> it = ditched.iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            ditchCard(slot, card);
        }
        return ditched;
    }

    @Override
    public ICard ditch(final String slot, final Class<? extends ICardType> type) {
        for (final Iterator it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            System.out.println(card.getName());
            if (!card.getLookup().lookupAll(type).isEmpty()) {
                System.out.println("Ditching: " + card.getName());
                ditchCard(slot, card);
                return card;
            }
        }
        return null;
    }

    @Override
    public ICard ditchBottom() {
        for (final Iterator it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            if (!it.hasNext()) {
                ditchCard(PAGES, card);
                return card;
            }
        }
        return null;
    }

    @Override
    public List<ICard> ditch(final String slot, final int amount, final boolean random) {
        ArrayList<ICard> ditched = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            ditched.add(ditch(slot, random));
        }
        return ditched;
    }

    private void ditchCard(final String slot, final RPCard ditched) {
        getSlot(slot).remove(ditched.getID());
        getSlot(DISCARD_PILE).add(ditched);
    }

    public void ditchFromHand() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ICard ditch(final String slot, final boolean random) {
        RPCard ditched = null;
        if (slot.equals(PAGES) || slot.equals(HAND)) {
            int index_to_ditch = (random ? rand.nextInt(getSlot(slot).size()) : 0), i = 0;
            if (index_to_ditch == 0) {
                ditched = (RPCard) getSlot(slot).getFirst();
            } else {
                for (Iterator<RPObject> it = getSlot(slot).iterator(); it.hasNext();) {
                    RPCard card = (RPCard) it.next();
                    if (i == index_to_ditch) {
                        ditched = card;
                        break;
                    }
                    i++;
                }
            }
            if (ditched != null) {
                ditchCard(slot, ditched);
            }
        }
        return ditched;
    }

    @Override
    public List<ICard> ditch(final String slot, final int amount) {
        ArrayList<ICard> ditched = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            ditched.add(ditch(slot));
        }
        return ditched;
    }

    @Override
    public ICard ditch(final String slot) {
        return ditch(slot, false);
    }

    @Override
    public ICard draw(final Class<? extends ICardType> type) {
        for (Iterator<RPObject> it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            if (card.getClass().isInstance(type) || type.isAssignableFrom(card.getClass())) {
                getSlot(PAGES).remove(card.getID());
                addToHand(card);
                return card;
            }
        }
        return null;
    }

    @Override
    public ICard draw() {
        return draw(false);
    }

    @Override
    public ICard draw(final boolean random) {
        ICard drawn = null;
        RPCard object = null;
        if (random) {
            int toRemove = rand.nextInt(getSlot(PAGES).size());
            int count = 0;
            for (final Iterator i = getSlot(PAGES).iterator(); i.hasNext();) {
                if (count == toRemove) {
                    object = (RPCard) i.next();
                    getSlot(PAGES).remove(object.getID());
                    break;
                }
                count++;
            }
        } else {
            object = (RPCard) getSlot(PAGES).getFirst();
            getSlot(PAGES).remove(object.getID());
        }
        if (object != null) {
            drawn = object;
            addToHand((RPCard) drawn);
        }
        return drawn;
    }

    @Override
    public ICard drawBottom() {
        RPCard drawn = null;
        for (final Iterator i = getSlot(PAGES).iterator(); i.hasNext();) {
            drawn = (RPCard) i.next();
            if (!i.hasNext()) {
                getSlot(PAGES).remove(drawn.getID());
                break;
            }
        }
        addToHand(drawn);
        return drawn;
    }

    @Override
    public List<ICard> draw(final int amount, final boolean random) {
        ArrayList<ICard> drawn = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            drawn.add(draw(random));
        }
        return drawn;
    }

    @Override
    public List<ICard> draw(final int amount) {
        ArrayList<ICard> drawn = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            drawn.add(draw());
        }
        return drawn;
    }

    @Override
    public void shuffle() {
        List<ICard> deckCards = getDeck();
        java.util.Collections.shuffle(deckCards);
        getSlot(PAGES).clear();
        for (Iterator<ICard> it = deckCards.iterator(); it.hasNext();) {
            ICard card = it.next();
            getSlot(PAGES).add((RPCard) card);
        }
    }

    @Override
    public int getSize() {
        return getSlot(PAGES).size();
    }

    @Override
    public int getUsedPileSize() {
        return getSlot(DISCARD_PILE).size();
    }

    public void sortHand() {
        sortHand(null);
    }

    public void sortHand(final Comparator comp) {
        List<ICard> handCards = getHand();
        if (comp == null) {
            java.util.Collections.sort(handCards);
        } else {
            java.util.Collections.sort(handCards, comp);
        }
        getSlot(HAND).clear();
        for (Iterator<ICard> it = handCards.iterator(); it.hasNext();) {
            ICard card = it.next();
            getSlot(HAND).add((RPCard) card);
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && getClass() == obj.getClass() && obj instanceof RPDeck) {
            RPDeck rpDeck = (RPDeck) obj;
            result = ((RPObject) obj).equals((RPObject) this);
            if (result) {
                result = result && rpDeck.getCards().equals(getCards())
                        && rpDeck.getDeck().equals(getDeck())
                        && rpDeck.getDiscardPile().equals(getDiscardPile())
                        && rpDeck.getDraws() == getDraws()
                        && rpDeck.getHand().equals(getHand())
                        && rpDeck.getLoses() == getLoses()
                        && rpDeck.getVersion() == getVersion()
                        && rpDeck.getWins() == getWins();
            }
        }
        return result;
    }
}
