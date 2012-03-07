package simple.server.extension.card;

import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardType;
import com.reflexit.magiccards.core.model.IDeck;
import java.util.*;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
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

    public RPDeck(String name) {
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        put("name", name);
        update();
    }

    public RPDeck(String name, List<RPCard> cards, List<RPCard> hand) {
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

    private void increaseRecord(String type) {
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
    public void addLoss() {
        increaseRecord(LOSES);
    }

    /**
     * Add a win to the record. Added to the current version.
     */
    public void addWin() {
        increaseRecord(WINS);
    }

    /**
     * Add a draw to the record. Added to the current version.
     */
    public void addDraw() {
        increaseRecord(DRAWS);
    }

    /**
     * Get wins
     *
     * @return wins
     */
    public int getWins() {
        return Integer.valueOf(get(RECORD, WINS));
    }

    /**
     * Get wins
     *
     * @return wins
     */
    public int getLoses() {
        return Integer.valueOf(get(RECORD, LOSES));
    }

    /**
     * Get wins
     *
     * @return wins
     */
    public int getDraws() {
        return Integer.valueOf(get(RECORD, DRAWS));
    }

    public void increaseVersion() {
        put(VERSION, getVersion() + 1);
    }

    public int getVersion() {
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

    public final void addToHand(RPCard card) {
        getSlot(HAND).add(card);
    }

    public final void addToDeck(RPCard card) {
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
    public List<ICard> ditch(Class<? extends ICardType> type, boolean random, int amount) {
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
                for (final Iterator i = getSlot(PAGES).iterator(); i.hasNext();) {
                    int toRemove = indices.remove(rand.nextInt(indices.size()));
                    if (count == toRemove) {
                        i.remove();
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
            ditchCard(card);
        }
        return ditched;
    }

    @Override
    public ICard ditch(Class<? extends ICardType> type) {
        int count = 0;
        for (final Iterator it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            if (card instanceof Lookup.Provider) {
                if (((IMarauroaCard) card).getLookup().lookup(type) != null) {
                    ditchCard(card);
                    return card;
                }
            } else if (card.getClass().isInstance(type)) {
                ditchCard(card);
                return card;
            }
            count++;
            System.out.println("Check number: " + count);
        }
        return null;
    }

    @Override
    public ICard ditchBottom() {
        for (final Iterator it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            if (!it.hasNext()) {
                ditchCard(card);
                return card;
            }
        }
        return null;
    }

    @Override
    public List<ICard> ditch(int amount, boolean random) {
        ArrayList<ICard> ditched = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            ditched.add(ditch(random));
        }
        return ditched;
    }

    private void ditchCard(RPCard ditched) {
        used.add(ditched);
        getSlot(PAGES).remove(ditched.getID());
        getSlot(DISCARD_PILE).add(ditched);
    }

    @Override
    public ICard ditch(boolean random) {
        int index_to_ditch = (random ? rand.nextInt(deck.size()) : 0), i = 0;
        RPCard ditched = null;
        for (Iterator<RPObject> it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            if (i == index_to_ditch) {
                ditched = card;
                break;
            }
        }
        if (ditched != null) {
            ditchCard(ditched);
        }
        return ditched;
    }

    @Override
    public List<ICard> ditch(int amount) {
        ArrayList<ICard> ditched = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            ditched.add(ditch());
        }
        return ditched;
    }

    @Override
    public ICard ditch() {
        return ditch(false);
    }

    @Override
    public ICard draw(Class<? extends ICardType> type) {
        for (Iterator<RPObject> it = getSlot(PAGES).iterator(); it.hasNext();) {
            RPCard card = (RPCard) it.next();
            if (card.getClass().isInstance(type)) {
                ditchCard(card);
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
    public ICard draw(boolean random) {
        ICard drawn;
        if (random) {
            drawn = deck.remove(rand.nextInt(deck.size()));
        } else {
            drawn = deck.remove(0);
        }
        hand.add(drawn);
        return drawn;
    }

    @Override
    public ICard drawBottom() {
        RPCard ditched = (RPCard) deck.remove(deck.size() - 1);
        used.add(ditched);
        return ditched;
    }

    @Override
    public List<ICard> draw(int amount, boolean random) {
        ArrayList<ICard> drawn = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            drawn.add(draw(random));
        }
        return drawn;
    }

    @Override
    public List<ICard> draw(int amount) {
        ArrayList<ICard> drawn = new ArrayList<ICard>();
        for (int i = 0; i < amount; i++) {
            drawn.add(draw());
        }
        return drawn;
    }

    @Override
    public void shuffle() {
        java.util.Collections.shuffle(deck);
    }

    @Override
    public int getSize() {
        return getSlot(PAGES).size();
    }

    @Override
    public int getUsedPileSize() {
        return getSlot(DISCARD_PILE).size();
    }
}
