//package simple.server.extension;
//
//import static org.junit.Assert.assertTrue;
//import org.junit.*;
//import org.openide.util.Lookup;
//
///**
// *
// * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
// */
//public class DiceRollerTest {
//
//    public DiceRollerTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of roll method, of class DiceRoller.
//     */
//    @Test
//    public void testRoll() {
//        System.out.println("roll");
//        String diceExp = "1d12";
//        float roll = getDiceRoler().roll(diceExp);
//        assertTrue(roll > 0 && roll < 12);
//        diceExp = "1d12+1";
//        roll = getDiceRoler().roll(diceExp);
//        assertTrue(roll > 1 && roll <= 13);
//        diceExp = "3d12";
//        roll = getDiceRoler().roll(diceExp);
//        assertTrue(roll >= 3 && roll <= 12 * 3);
//        diceExp = "3d12+1";
//        roll = getDiceRoler().roll(diceExp);
//        assertTrue(roll >= 3 + 1 && roll <= 12 * 3 + 1);
//        diceExp = "1d12-1";
//        roll = getDiceRoler().roll(diceExp);
//        assertTrue(roll >= 0 && roll <= 11);
//        diceExp = "3d12-1";
//        roll = getDiceRoler().roll(diceExp);
//        assertTrue(roll >= 3 - 1 && roll <= 12 * 3 - 1);
//    }
//
//    IDiceRoller getDiceRoler() {
//        return Lookup.getDefault().lookup(IDiceRoller.class);
//    }
//}
