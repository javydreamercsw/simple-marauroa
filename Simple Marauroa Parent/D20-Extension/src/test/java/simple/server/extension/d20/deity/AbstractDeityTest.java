package simple.server.extension.d20.deity;

import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractDeityTest {

    @Test
    public void testDeity() {
        System.out.println("Test deities");
        Lookup.getDefault().lookupAll(Deity.class).forEach((deity) -> {
            System.out.println("Analyzing deity: " 
                    + deity.getCharacteristicName());
            assertEquals(1, deity.getExclusiveClasses().size());
            assertEquals(0, deity.getRequirements().size());
            assertEquals(0, deity.getOpponentRequirements().size());
        });
    }
}
