package com.reflexit.magiccards.core.model.storage;

import com.reflexit.magiccards.core.DummyCard;
import com.reflexit.magiccards.core.DummySet;
import com.reflexit.magiccards.core.model.ICardSet;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MemoryCardStorageTest {

    public MemoryCardStorageTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of iterator method, of class MemoryCardStorage.
     */
    @Test
    public void testIterator() {
        System.out.println("iterator");
        MemoryCardStorage instance = new MemoryCardStorage();
        Iterator result = instance.iterator();
        assertTrue(result != null);
    }

    /**
     * Test of size method, of class MemoryCardStorage.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        ICardSet set = new DummySet("Set #1");
        ICardSet set2 = new DummySet("Set #2");
        DummyCard card1 = new DummyCard("Card #1");
        DummyCard card2 = new DummyCard("Card #2");
        DummyCard card3 = new DummyCard("Card #3");
        MemoryCardStorage instance = new MemoryCardStorage();
        assertEquals(0, instance.size());
        instance.add(card1, set);
        assertEquals(1, instance.size());
        instance.add(card2, set);
        assertEquals(2, instance.size());
        instance.add(card3, set2);
        assertEquals(3, instance.size());
        assertEquals(true, instance.contains(card1, set));
        assertEquals(false, instance.contains(card1, set2));
        assertEquals(true, instance.contains(card1));
        instance.remove(card1, set);
        assertEquals(false, instance.contains(card1, set));
        assertEquals(false, instance.contains(card1));
        assertEquals(2, instance.size());
        instance.remove(card1, set2);
        assertEquals(2, instance.size());
        instance.clearCache();
        assertEquals(0, instance.size());
    }
}
