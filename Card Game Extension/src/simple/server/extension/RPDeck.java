package simple.server.extension;

import java.util.List;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RPEntityInterface.class, position = 1002)
public class RPDeck extends RPEntity {

    public static final String DECK_NAME = "deck_name", PAGES = "pages",
            HAND = "hand", WINS = "wins", LOSES = "loses", DRAWS = "draws",
            VERSION = "version", RECORD = "record", CLASS_NAME = "deck";

    public RPDeck() {
    }

    public RPDeck(String name, List<RPCard> cards, List<RPCard> hand) {
        setRPClass(CLASS_NAME);
        put("type", CLASS_NAME);
        put(DECK_NAME, name);
        addSlot(PAGES);
        for (RPCard card : cards) {
            getSlot(PAGES).add(card);
        }
        addSlot(HAND);
        for (RPCard card : hand) {
            getSlot(HAND).add(card);
        }
        update();
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
    }

    private void increaseRecord(String type) {
        switch (type) {
            case LOSES:
                put(RECORD, type,
                        String.valueOf(getLoses() + 1));
                break;
            case WINS:
                put(RECORD, type,
                        String.valueOf(getWins() + 1));
                break;
            case DRAWS:
                put(RECORD, type,
                        String.valueOf(getDraws() + 1));
                break;
            default:
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
    
    public int getVersion(){
        return getInt(VERSION);
    }

    @Override
    public void generateRPClass() {
        RPClass entity = new RPClass(CLASS_NAME);
        entity.isA("entity");

        /**
         * Deck name
         */
        entity.addAttribute(DECK_NAME, Definition.Type.STRING);
        /**
         * RPCards
         */
        entity.addRPSlot(PAGES, -1, Definition.PRIVATE);

        /**
         * Starting hand
         */
        entity.addRPSlot(HAND, -1, Definition.PRIVATE);

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
