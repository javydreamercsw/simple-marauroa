package simple.server.extension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import marauroa.common.Log4J;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.*;
import org.openide.util.Lookup;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class RPDeckTest {

    private DummyObject test;

    public RPDeckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Log4J.init("simple/server/log4j.properties");
        //Register RPClasses
        for (RPEntityInterface x : Lookup.getDefault().lookupAll(RPEntityInterface.class)) {
            x.generateRPClass();
        }
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of generateRPClass method, of class RPDeck.
     */
    @Test
    public void testDeckSystem() {
        test = new DummyObject();
        //Generate dummy player
        test.generateRPClass();
        //Create cards
        RPCard card = new RPCard(getClass());
        assertTrue(card.has(RPCard.CARD_ID));
        assertTrue(card.has(RPCard.CLASS));
        assertTrue(card.has(RPCard.CREATION_DATE));
        assertTrue(card.has(RPCard.TIMES_TRADED));
        assertTrue(card.has(RPCard.TRADABLE));

        RPCard card2 = new RPCard(DummyObject.class);
        assertTrue(card2.has(RPCard.CARD_ID));
        assertTrue(card2.has(RPCard.CLASS));
        assertTrue(card2.has(RPCard.CREATION_DATE));
        assertTrue(card2.has(RPCard.TIMES_TRADED));
        assertTrue(card2.has(RPCard.TRADABLE));

        assertTrue(card.getTimesTraded() == 0);
        card.increaseTimesTraded();
        assertTrue(card.getTimesTraded() == 1);
        card.put(RPCard.TRADABLE, "false");
        try {
            card.increaseTimesTraded();
            fail("Shouldn't be able to increase times traded!");
        } catch (RuntimeException e) {
            //Do nothing, it should fail
        }
        assertTrue(!card.get(RPCard.CARD_ID).equals(card2.get(RPCard.CARD_ID)));
        //Prepare test deck contents
        int deckSize = 50, handSize = 5;
        List<RPCard> body = new ArrayList<>();
        for (int i = 0; i < deckSize; i++) {
            body.add(card);
        }
        List<RPCard> hand = new ArrayList<>();
        for (int i = 0; i < handSize; i++) {
            hand.add(card2);
        }
        //Create a deck
        RPDeck deck = new RPDeck("test deck", body, hand);
        assertTrue(deck.getSlot(RPDeck.PAGES).size() == deckSize);
        for (Iterator<RPObject> it = deck.getSlot(RPDeck.PAGES).iterator(); it.hasNext();) {
            RPObject obj = it.next();
            assertTrue(obj instanceof RPCard);
            assertTrue(obj.get(RPCard.CLASS_NAME).equals(card.get(RPCard.CLASS_NAME)));
        }
        assertTrue(deck.getSlot(RPDeck.HAND).size() == handSize);
        for (Iterator<RPObject> it = deck.getSlot(RPDeck.HAND).iterator(); it.hasNext();) {
            RPObject obj = it.next();
            assertTrue(obj instanceof RPCard);
            assertTrue(obj.get(RPCard.CLASS_NAME).equals(card2.get(RPCard.CLASS_NAME)));
        }
        assertTrue(deck.getWins() == 0);
        assertTrue(deck.getLoses() == 0);
        assertTrue(deck.getDraws() == 0);
        deck.addWin();
        assertTrue(deck.getWins() == 1);
        deck.addDraw();
        assertTrue(deck.getDraws() == 1);
        deck.addLoss();
        assertTrue(deck.getLoses() == 1);
        assertTrue(deck.getVersion() == 1);
        deck.increaseVersion();
        assertTrue(deck.getVersion() == 2);
        assertTrue(test.hasSlot(CardGameExtension.DECK));
        test.getSlot(CardGameExtension.DECK).add(deck);
        assertTrue(test.getSlot(CardGameExtension.DECK).has(deck.getID()));
    }

    private class DummyObject extends ClientObject {

        public DummyObject() {
            update();
        }

        @Override
        public void generateRPClass() {
            RPClass entity = new RPClass("test_client_object");
            entity.isA("client_object");
            extendClass(entity);
            assertTrue(entity.hasDefinition(Definition.DefinitionClass.RPSLOT, CardGameExtension.DECK));
        }
    }
}
