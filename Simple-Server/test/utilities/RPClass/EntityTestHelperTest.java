package utilities.RPClass;

import marauroa.common.game.RPClass;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class EntityTestHelperTest {

    @Test
    public void testGenerateRPClasses() {
        EntityTestHelper.generateRPClasses();
        assertTrue(RPClass.hasRPClass("entity"));
    }
}
