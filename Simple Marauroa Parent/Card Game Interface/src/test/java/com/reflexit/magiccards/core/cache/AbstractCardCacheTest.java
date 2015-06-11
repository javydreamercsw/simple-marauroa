package com.reflexit.magiccards.core.cache;

import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class AbstractCardCacheTest {
    
    public AbstractCardCacheTest() {
    }

    /**
     * Test of setCahchingEnabled method, of class AbstractCardCache.
     */
    @Test
    public void testSetCahchingEnabled() {
        System.out.println("setCahchingEnabled");
        AbstractCardCache.setCachingEnabled(true);
        assertTrue(AbstractCardCache.isCachingEnabled());
        AbstractCardCache.setCachingEnabled(false);
        assertFalse(AbstractCardCache.isCachingEnabled());
    }

    /**
     * Test of setLoadingEnabled method, of class AbstractCardCache.
     */
    @Test
    public void testSetLoadingEnabled() {
        System.out.println("setLoadingEnabled");
        AbstractCardCache.setLoadingEnabled(true);
        assertTrue(AbstractCardCache.isLoadingEnabled());
        AbstractCardCache.setLoadingEnabled(false);
        assertFalse(AbstractCardCache.isLoadingEnabled());
    }
}
