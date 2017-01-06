package simple.common;

import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SizeLimitedArrayTest {

    /**
     * Test of add method, of class SizeLimitedArray.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        SizeLimitedArray<Integer> instance = new SizeLimitedArray<>();
        assertTrue(instance.add(0));
        for (int i = 1; i <= 100; i++) {
            instance.add(i);
            assertEquals(i <= 10, instance.contains(1));
        }
        assertEquals(10, instance.size());
        instance = new SizeLimitedArray<>(100);
        for (int i = 1; i <= 100; i++) {
            instance.add(i);
            assertTrue(instance.contains(1));
        }
        assertEquals(100, instance.size());
        instance.add(101);
        assertFalse(instance.contains(1));
        assertEquals(100, instance.size());
    }

    /**
     * Test of addAll method, of class SizeLimitedArray.
     */
    @Test
    public void testAddAll_Collection() {
        System.out.println("addAll");
        Collection collectionToAdd = Arrays.asList(1, 2, 3);
        SizeLimitedArray<Integer> instance = new SizeLimitedArray();
        assertEquals(0, instance.size());
        instance.addAll(collectionToAdd);
        assertEquals(collectionToAdd.size(), instance.size());
        collectionToAdd = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        try {
            instance.addAll(collectionToAdd);
            fail();
        } catch (IllegalStateException e) {
            //Expected
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test of isEmpty method, of class SizeLimitedArray.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        SizeLimitedArray<String> instance = new SizeLimitedArray();
        assertTrue(instance.isEmpty());
        instance.add("");
        assertFalse(instance.isEmpty());
    }

    /**
     * Test of contains method, of class SizeLimitedArray.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        int val = 1;
        SizeLimitedArray<Integer> instance = new SizeLimitedArray();
        for (int i = 1; i <= 15; i++) {
            instance.add(i);
            assertEquals(i <= 10, instance.contains(val));
        }
    }
}
