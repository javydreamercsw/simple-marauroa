package simple.server.core.engine;

import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.event.SimpleRPEvent;
import simple.server.core.event.api.IRPEvent;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class EventTest extends AbstractSystemTest {

    @Test
    public void testEvents() {
        System.out.println("Test Events");
        for (IRPEvent event : Lookup.getDefault().lookupAll(IRPEvent.class)) {
            System.out.println("Checking event: " + event.getName());
            assertTrue(event.has(SimpleRPEvent.EVENT_ID));
        }
    }
}
