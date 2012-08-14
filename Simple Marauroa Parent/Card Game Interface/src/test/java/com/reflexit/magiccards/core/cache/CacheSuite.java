package com.reflexit.magiccards.core.cache;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({com.reflexit.magiccards.core.cache.CacheDataTest.class, com.reflexit.magiccards.core.cache.AbstractCardCacheTest.class})
public class CacheSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}
