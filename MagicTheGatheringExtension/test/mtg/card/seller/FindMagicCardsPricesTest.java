/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mtg.card.seller;

import java.net.URL;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import mtg.card.IMagicCard;
import mtg.card.MagicCard;
import mtg.card.MagicCardFilter;
import mtg.card.MagicFilteredCardStore;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.*;
import simple.server.extension.card.storage.ICardStore;
import simple.server.extension.card.storage.MemoryCardStorage;
import simple.server.extension.card.storage.MemoryCardStore;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class FindMagicCardsPricesTest {

    public FindMagicCardsPricesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
     * Test of getName method, of class FindMagicCardsPrices.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        FindMagicCardsPrices instance = new FindMagicCardsPrices();
        String result = instance.getName();
        assertTrue(result != null);
    }

    /**
     * Test of getURL method, of class FindMagicCardsPrices.
     */
    @Test
    public void testGetURL() {
        System.out.println("getURL");
        FindMagicCardsPrices instance = new FindMagicCardsPrices();
        URL result = instance.getURL();
        assertTrue(result != null);
    }

    /**
     * Test of updateStore method, of class FindMagicCardsPrices.
     */
    @Test
    public void testUpdateStore_3args() throws Exception {
        System.out.println("updateStore");
        try {
            FindMagicCardsPrices prices = new FindMagicCardsPrices();
            MagicFilteredCardStore<IMagicCard> fstore = new MagicFilteredCardStore<IMagicCard>() {

                MemoryCardStore<MemoryCardStorage<IMagicCard>> store = new MemoryCardStore<MemoryCardStorage<IMagicCard>>();

                @Override
                public ICardStore getCardStore() {
                    return store;
                }
            };
            MagicCard card = new MagicCard();
            card.setSet("Time Spiral");
            card.setName("Amrou Scout");
            fstore.getCardStore().add(card);
            fstore.update(new MagicCardFilter());
            prices.updateStore(fstore);
            Iterator iterator = fstore.iterator();
            while (iterator.hasNext()) {
                MagicCard temp = (MagicCard) iterator.next();
                System.out.println(temp.toString());
                System.out.println("Prize: $" + temp.getDbPrice());
            }
        } catch (Exception ex) {
            Logger.getLogger(FindMagicCardsPrices.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
