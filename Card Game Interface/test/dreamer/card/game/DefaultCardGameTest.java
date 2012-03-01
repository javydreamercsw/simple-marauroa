/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.*;
import dreamer.card.game.storage.database.persistence.controller.exceptions.NonexistentEntityException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.*;
import org.junit.*;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class DefaultCardGameTest {

    private static final Logger LOG = Logger.getLogger(DefaultCardGameTest.class.getName());
    private static Game game;

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
     * Test the database.
     */
    @Test
    public void testDatabase() {
        try {
            HashMap parameters = new HashMap();
            DefaultCardGame instance = new DefaultCardGameImpl();
            parameters.put("name", instance.getName());
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            assertTrue(result.isEmpty());
            instance.init();
            result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            assertFalse(result.isEmpty());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    public static class DefaultCardGameImpl extends DefaultCardGame {

        @Override
        public String getName() {
            return "Test Game";
        }

        static {
            attribs.add("rarity");
            attribs.add("creature");

            attribs.add("power");
            attribs.add("toughness");

            collectionTypes.add("Deck");
            collectionTypes.add("Collection");

            collections.put("Collection", "My Pages");
        }

        @Override
        public void init() {
            try {
                super.init();
                HashMap parameters = new HashMap();
                parameters.put("name", getName());
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
                game = (Game) result.get(0);
                System.out.println("Check types");
                parameters.put("name", "rarity");
                assertFalse(Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters).isEmpty());
                parameters.put("name", "creature");
                assertFalse(Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters).isEmpty());
                System.out.println("Check the attributes");
                for (Iterator<String> it = attribs.iterator(); it.hasNext();) {
                    String rarity = it.next();
                    parameters.put("name", rarity);
                    assertFalse(Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters).isEmpty());
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
                assertTrue(Arrays.equals(temp.getText(), card.getText()));
                System.out.println("Add an attribute to a card");
                for (Iterator<String> it = attribs.iterator(); it.hasNext();) {
                    String rarity = it.next();
                    parameters.put("name", rarity);
                    result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                    Lookup.getDefault().lookup(IDataBaseManager.class).addAttributeToCard(card,
                            (CardAttribute) result.get(0));
                }
                System.out.println("Check attributes");
                for (Iterator<CardHasCardAttribute> it = card.getCardHasCardAttributeList().iterator(); it.hasNext();) {
                    CardHasCardAttribute chca = it.next();
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
                String set1 = Lookup.getDefault().lookup(IDataBaseManager.class).printCardsInSet(cs1);
                String set2 = Lookup.getDefault().lookup(IDataBaseManager.class).printCardsInSet(cs2);
                assertFalse(set1.isEmpty());
                assertFalse(set2.isEmpty());
                assertTrue(set1.length() < set2.length());
                System.out.println(set1);
                System.out.println(set2);
                System.out.println("Check Collection Type");
                for (Iterator<String> it = collectionTypes.iterator(); it.hasNext();) {
                    String type = it.next();
                    parameters.put("name", type);
                    assertFalse(Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardCollectionType.findByName", parameters).isEmpty());
                }
                System.out.println("Check Collections");
                for (Iterator<Entry<String, String>> it = collections.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<String, String> entry = it.next();
                    parameters.put("name", entry.getValue());
                    assertFalse(Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardCollection.findByName", parameters).isEmpty());
                }
                System.out.println("Adding pages to collection");
                parameters.put("name", "My Pages");
                CardCollection collection = (CardCollection) Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardCollection.findByName", parameters).get(0);
                HashMap<Card, Integer> addToCollection = new HashMap<Card, Integer>();
                int totalPages = 3, toRemove = 1;
                addToCollection.put(temp, totalPages);
                assertTrue(collection.getCardCollectionHasCardList().isEmpty());
                collection = Lookup.getDefault().lookup(IDataBaseManager.class).addCardsToCollection(addToCollection, collection);
                assertTrue(collection.getCardCollectionHasCardList().size() == 1);
                assertTrue(collection.getCardCollectionHasCardList().get(0).getAmount() == totalPages);
                System.out.println("Removing pages from collection");
                addToCollection.put(temp, toRemove);
                collection = Lookup.getDefault().lookup(IDataBaseManager.class).removeCardsFromCollection(addToCollection, collection);
                assertTrue(collection.getCardCollectionHasCardList().get(0).getAmount() == (totalPages - toRemove));
                System.out.println("Print collection");
                String collectionList = Lookup.getDefault().lookup(IDataBaseManager.class).printCardsCollection(collection);
                assertFalse(collectionList.isEmpty());
                System.out.println(collectionList);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }

        @Override
        public Runnable getUpdateRunnable() {
            //Do nothing
            return null;
        }
    }
}
