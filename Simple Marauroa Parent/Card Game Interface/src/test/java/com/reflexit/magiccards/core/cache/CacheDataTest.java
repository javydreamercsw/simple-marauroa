package com.reflexit.magiccards.core.cache;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openide.util.Lookup;

import com.reflexit.magiccards.core.model.ICard;
import com.reflexit.magiccards.core.model.ICardField;
import com.reflexit.magiccards.core.model.ICardType;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class CacheDataTest {

    public CacheDataTest() {
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
            public String getCardId() {
                return "1";
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

            @Override
            public Lookup getLookup() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public ICardType getCardType() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
