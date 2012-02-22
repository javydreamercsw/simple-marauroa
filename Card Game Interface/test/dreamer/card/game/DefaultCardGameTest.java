/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dreamer.card.game;

import org.junit.*;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class DefaultCardGameTest {
    
    public DefaultCardGameTest() {
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
     * Test of init method, of class DefaultCardGame.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        DefaultCardGame instance = new DefaultCardGameImpl();
        instance.init();
    }

    public class DefaultCardGameImpl extends DefaultCardGame {

        @Override
        public String getName() {
            return "Test Game";
        }
    }
}
