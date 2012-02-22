/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game;

import dreamer.card.game.storage.IDataBaseManager;
import dreamer.card.game.storage.database.persistence.Card;
import dreamer.card.game.storage.database.persistence.CardAttribute;
import dreamer.card.game.storage.database.persistence.CardHasCardAttribute;
import dreamer.card.game.storage.database.persistence.CardType;
import dreamer.card.game.storage.database.persistence.controller.CardHasCardAttributeJpaController;
import dreamer.card.game.storage.database.persistence.controller.CardJpaController;
import dreamer.card.game.storage.database.persistence.controller.CardTypeJpaController;
import dreamer.card.game.storage.database.persistence.controller.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.*;
import org.openide.util.Lookup;
import static junit.framework.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class DefaultCardGameTest {

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
        System.out.println("init");
        HashMap parameters = new HashMap();
        DefaultCardGame instance = new DefaultCardGameImpl();
        parameters.put("name", instance.getName());
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            assertTrue(result.isEmpty());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
        instance.init();
        try {
            List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Game.findByName", parameters);
            assertFalse(result.isEmpty());
        } catch (Exception ex) {
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
            super.init();
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
            HashMap parameters = new HashMap();
            try {
                parameters.put("name", "rarity");
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
                assertFalse(result.isEmpty());
            } catch (Exception ex) {
                fail();
            }
            try {
                parameters.put("name", "creature");
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttributeType.findByName", parameters);
                assertFalse(result.isEmpty());
            } catch (Exception ex) {
                fail();
            }
            System.out.println("Check the attributes");
            for (String rarity : rarities) {
                try {
                    parameters.put("name", rarity);
                    List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                } catch (Exception ex) {
                    fail();
                }
            }
            for (String attr : creatureAttribs) {
                try {
                    parameters.put("name", attr);
                    List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                } catch (Exception ex) {
                    fail();
                }
            }
            System.out.println("Create card type");
            CardType sampleType = Lookup.getDefault().lookup(IDataBaseManager.class).createCardType("sample");
            System.out.println("Create a card");
            Card card = Lookup.getDefault().lookup(IDataBaseManager.class).createCard(sampleType, "Sample", "Sample body text".getBytes());
            try {
                parameters.put("name", card.getName());
                List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("Card.findByName", parameters);
                assertFalse(result.isEmpty());
                Card temp = (Card) result.get(0);
                assertTrue(temp.getName().equals(card.getName()));
                assertTrue(temp.getText().equals(card.getText()));
            } catch (Exception ex) {
                fail();
            }
            System.out.println("Add an attribute to a card");
            for (String rarity : rarities) {
                try {
                    parameters.put("name", rarity);
                    List result = Lookup.getDefault().lookup(IDataBaseManager.class).namedQuery("CardAttribute.findByName", parameters);
                    assertFalse(result.isEmpty());
                    Lookup.getDefault().lookup(IDataBaseManager.class).addAttributeToCard(card,
                            (CardAttribute) result.get(0), "test");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail();
                }
            }
            System.out.println("Check attributes");
            for (CardHasCardAttribute chca : card.getCardHasCardAttributeList()) {
                assertTrue(chca.getValue().equals("test"));
            }
            System.out.println("Create set");

        }
    }
}
