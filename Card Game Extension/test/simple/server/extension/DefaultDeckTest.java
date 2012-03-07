package simple.server.extension;

import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.junit.Assert.*;
import org.junit.*;
import org.openide.util.Lookup;
import simple.server.core.entity.RPEntityInterface;
import simple.server.extension.card.IMarauroaCard;
import simple.server.extension.card.RPDeck;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com>
 */
public class DefaultDeckTest {

    private static RPDeck instance;
    private static int deckSize = 100, ditchCount = 0, drawCount = 0,
            interfaceCounter = 0, interfaceIndex = -1;
    private IMarauroaCard card;
    private List<ICard> list;

    public DefaultDeckTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        for (RPEntityInterface entity : Lookup.getDefault().lookupAll(RPEntityInterface.class)) {
            entity.generateRPClass();
        }
        instance = new RPDeck("test");
        Random rand = new Random();
        for (int i = 0; i < deckSize; i++) {
            if (rand.nextBoolean()) {
                instance.addToDeck(new DefaultCard());
            } else {
                instance.addToDeck(new DefaultCard2());
                if (interfaceIndex < 0) {
                    interfaceIndex = i + 1;
                }
                interfaceCounter++;
            }
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
     * Test of getCards method, of class DefaultDeck.
     */
    @Test
    public void testDeck() {
        System.out.println("getCards");
        assertTrue(instance.getSize() == deckSize);

        System.out.println("getUsedCards");
        assertTrue(instance.getUsedPileSize() == ditchCount);

        System.out.println("ditch");
        Class<? extends ICardType> type = DefaultType2.class;
        IMarauroaCard ditchedCard = (IMarauroaCard) instance.ditch(type);
        assertTrue(ditchedCard == null || ditchedCard instanceof DefaultType2);
        cardDitched();
        assertTrue(instance.getUsedPileSize() == ditchCount);
        assertTrue(instance.getSize() == deckSize);
        updateInterfaceIndex();

        System.out.println("ditchBottom");
        IMarauroaCard toDitch = (IMarauroaCard) instance.getCards().get(instance.getCards().size() - 1);
        assertEquals(toDitch, instance.ditchBottom());
        cardDitched();

        System.out.println("ditch");
        int ditch = 2;
        List<ICard> ditched = instance.ditch(ditch, false);
        cardDitched(ditch);
        assertTrue(ditched.size() == ditch);

        System.out.println("ditch");
        ditch = 5;
        List result = instance.ditch(ditch);
        cardDitched(ditch);
        assertTrue(result.size() == ditch);

        System.out.println("ditch");
        toDitch = (IMarauroaCard) instance.getCards().get(0);
        card = (IMarauroaCard) instance.ditch(false);
        cardDitched();
        assertEquals(toDitch, card);

        System.out.println("ditch");
        toDitch = (IMarauroaCard) instance.getCards().get(0);
        card = (IMarauroaCard) instance.ditch();
        cardDitched();
        assertEquals(toDitch, card);

        System.out.println("draw");
        updateInterfaceIndex();
        toDitch = (IMarauroaCard) instance.getCards().get(interfaceIndex);
        card = (IMarauroaCard) instance.draw(DefaultType2.class);
        assertEquals(toDitch, card);
        updateInterfaceIndex();
        interfaceCounter--;
        cardDrawn();

        System.out.println("draw");
        IMarauroaCard toDraw = (IMarauroaCard) instance.getCards().get(0);
        card = (IMarauroaCard) instance.draw();
        assertEquals(toDraw, card);
        cardDrawn();

        System.out.println("draw");
        toDraw = (IMarauroaCard) instance.getCards().get(0);
        card = (IMarauroaCard) instance.draw(false);
        assertEquals(toDraw, card);
        cardDrawn();

        System.out.println("drawBottom");
        toDraw = (IMarauroaCard) instance.getCards().get(instance.getSize() - 1);
        card = (IMarauroaCard) instance.drawBottom();
        assertEquals(toDraw, card);
        cardDrawn();

        System.out.println("draw");
        int draw = 5;
        list = instance.draw(draw, false);
        assertTrue(result.size() == draw);
        cardDrawn(draw);

        System.out.println("draw");
        draw = 5;
        list = instance.draw(draw);
        cardDrawn(draw);
        assertTrue(result.size() == draw);

        System.out.println("getSize");
        assertEquals(deckSize, instance.getSize());

        System.out.println("getUsedPileSize");
        assertEquals(ditchCount, instance.getUsedPileSize());

        System.out.println("shuffle");
        ArrayList<ICard> current = (ArrayList<ICard>) instance.getCards();
        instance.shuffle();
        assertTrue(current.size() == instance.getCards().size());
        updateInterfaceIndex();
    }

    private void cardDitched() {
        increaseDitch();
        decreaseDeckSize();
    }

    private void cardDrawn() {
        decreaseDeckSize();
        drawCount++;
    }

    private void cardDrawn(int drawn) {
        for (int i = 0; i < drawn; i++) {
            cardDrawn();
        }
    }

    private void cardDitched(int times) {
        for (int i = 0; i < times; i++) {
            cardDitched();
        }
    }

    private void increaseDitch() {
        ditchCount++;
    }

    private void decreaseDeckSize() {
        deckSize--;
    }

    private void updateInterfaceIndex() {
        //Get next index
        for (int i = 0; i < instance.getSize(); i++) {
            if (instance.getCards().get(i) instanceof DefaultType2) {
                interfaceIndex = i;
                break;
            }
        }
    }
}
