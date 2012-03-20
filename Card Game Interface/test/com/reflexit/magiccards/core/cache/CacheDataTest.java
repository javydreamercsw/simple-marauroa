package com.reflexit.magiccards.core.cache;

import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardField;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CacheDataTest {

    public CacheDataTest() {
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
     * Test of add method, of class CacheData.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        ICard card = new ICard() {
            @Override
            public String getName() {
                return "test";
            }

            @Override
            public Object getObjectByField(ICardField field) {
                return "test";
            }

            @Override
            public int getCardId() {
                return 1;
            }

            @Override
            public String getSetName() {
                return "test";
            }

            @Override
            public void setSetName(String set) {
            }

            @Override
            public int compareTo(Object o) {
                return o.equals(this) ? 0 : -1;
            }
        };
        CacheData instance = new CacheData();
        instance.add(card);
        assertEquals(instance.next(), card);
    }

    /**
     * Test of toCacheAmount method, of class CacheData.
     */
    @Test
    public void testToCacheAmount() {
        System.out.println("toCacheAmount");
        CacheData instance = new CacheData();
        int expResult = 0;
        int result = instance.toCacheAmount();
        assertEquals(expResult, result);
    }

    /**
     * Test of cachedAmount method, of class CacheData.
     */
    @Test
    public void testCachedAmount() {
        System.out.println("cachedAmount");
        CacheData instance = new CacheData();
        int expResult = 0;
        int result = instance.cachedAmount();
        assertEquals(expResult, result);
    }
}
