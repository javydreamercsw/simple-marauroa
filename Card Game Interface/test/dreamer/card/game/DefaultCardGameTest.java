/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.*;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.*;
import org.openide.util.Lookup;
import static junit.framework.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class DefaultCardGameTest {

    private Game game;

    public DefaultCardGameTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        Lookup.getDefault().lookup(IDataBaseManager.class).setPU("Card_Game_Interface_TestPU");
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
     * Test of init method, of class DefaultCardGame.
     */
    @Test
    public void testInit() {
        try {
            System.out.println("init");
            HashMap parameters = new HashMap();
            DefaultCardGame instance = new DefaultCardGameImpl();
            parameters.put("name", instance.getName());
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            assertTrue(result.isEmpty());
            instance.init();
            result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            assertFalse(result.isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public class DefaultCardGameImpl extends DefaultCardGame {

        private ArrayList<String> rarities = new ArrayList<String>();
        private ArrayList<String> creatureAttribs = new ArrayList<String>();

        @Override
        public String getName() {
            return "Test Game";
        }

        @Override
        public void init() {
            try {
                super.init();
                HashMap parameters = new HashMap();
                parameters.put("name", getName());
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
                game = (Game) result.get(0);
                rarities.add("rarity.common");
                rarities.add("rarity.uncommon");
                rarities.add("rarity.rare");
                rarities.add("rarity.mythic.rare");
                rarities.add("rarity.land");

                creatureAttribs.add("power");
                creatureAttribs.add("toughness");
                Lookup.getDefault().lookup(IDataBaseManager.class).createAttributes("rarity", rarities);
                Lookup.getDefault().lookup(IDataBaseManager.class).createAttributes("creature", creatureAttribs);
                System.out.println("Check types");
                parameters.put("name", "rarity");
                result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
                assertFalse(result.isEmpty());
                parameters.put("name", "creature");
                result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
                assertFalse(result.isEmpty());
                System.out.println("Check the attributes");
                for (String rarity : rarities) {
                    parameters.put("name", rarity);
                    result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                }
                for (String attr : creatureAttribs) {
                    parameters.put("name", attr);
                    result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                }
                System.out.println("Create card type");
                CardType sampleType = Lookup.getDefault().lookup(IDataBaseManager.class).createCardType("sample");
                System.out.println("Create a card");
                Card card = Lookup.getDefault().lookup(IDataBaseManager.class).createCard(sampleType, "Sample", "Sample body text".getBytes());
                Card card2 = Lookup.getDefault().lookup(IDataBaseManager.class).createCard(sampleType, "Sample2", "Sample body text".getBytes());
                parameters.put("name", card.getName());
                result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Card.findByName", parameters);
                assertFalse(result.isEmpty());
                Card temp = (Card) result.get(0);
                assertTrue(temp.getName().equals(card.getName()));
                assertTrue(temp.getText().equals(card.getText()));
                System.out.println("Add an attribute to a card");
                for (String rarity : rarities) {
                    parameters.put("name", rarity);
                    result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                    Lookup.getDefault().lookup(IDataBaseManager.class).addAttributeToCard(card,
                            (CardAttribute) result.get(0), "test");
                }
                System.out.println("Check attributes");
                for (CardHasCardAttribute chca : card.getCardHasCardAttributeList()) {
                    assertTrue(chca.getValue().equals("test"));
                }
                System.out.println("Create set");
                CardSet cs1 = Lookup.getDefault().lookup(IDataBaseManager.class).createCardSet(game, "Test Set", "TS", new Date());
                assertTrue(cs1 != null);
                CardSet cs2 = Lookup.getDefault().lookup(IDataBaseManager.class).createCardSet(game, "Test Set2", "TS2", new Date());
                assertTrue(cs2 != null);
                System.out.println("Add card to set");
                ArrayList<Card> cards = new ArrayList<Card>();
                cards.add(card);
                try {
                    Lookup.getDefault().lookup(IDataBaseManager.class).addCardsToSet(cards, cs1);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(DefaultCardGameTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail();
                }
                assertTrue(cs1.getCardList().size() == cards.size());
                cards.add(card2);
                try {
                    Lookup.getDefault().lookup(IDataBaseManager.class).addCardsToSet(cards, cs2);
                } catch (NonexistentEntityException ex) {
                    Logger.getLogger(DefaultCardGameTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail();
                }
                assertTrue(cs2.getCardList().size() == cards.size());
                System.out.println("Print Sets");
                String set1=Lookup.getDefault().lookup(IDataBaseManager.class).printCardsInSet(cs1);
                String set2=Lookup.getDefault().lookup(IDataBaseManager.class).printCardsInSet(cs2);
                assertFalse(set1.isEmpty());
                assertFalse(set2.isEmpty());
                assertTrue(set1.length() < set2.length());
                System.out.println(set1);
                System.out.println(set2);
                System.out.println("Create Collections");
                
            } catch (Exception ex) {
                Logger.getLogger(DefaultCardGameTest.class.getName()).log(Level.SEVERE, null, ex);
                fail();
            }
        }
    }
}
