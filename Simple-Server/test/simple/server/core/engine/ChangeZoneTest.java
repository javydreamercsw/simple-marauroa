package simple.server.core.engine;

import marauroa.common.game.IRPZone.ID;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import simple.SimpleServerTest;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ChangeZoneTest extends SimpleServerTest{

    private static ClientObject player1;

    @BeforeClass
    public static void setUpClass() throws Exception {
        init();
        // load item configurations to handle money and other items
        SimpleSingletonRepository.getEntityManager();
        player1 = new ClientObject(ClientObject.createEmptyZeroLevelPlayer("player1"));
        MockSimpleRPWorld.get().addRPZone(new SimpleRPZone("zone1"));
        MockSimpleRPWorld.get().addRPZone(new SimpleRPZone("zone2"));
    }

    /**
     * Test of getGameName method, of class SimpleDatabase.
     *
     * @throws Exception
     */
    @Test
    public void changeZone() {
        try {
            System.out.println("changeZone");
            assertTrue(MockSimpleRPWorld.get().hasRPZone(new ID("zone1")));
            assertTrue(MockSimpleRPWorld.get().hasRPZone(new ID("zone2")));
            assertFalse(MockSimpleRPWorld.get().hasRPZone(new ID("zone3")));
            player1.put("zoneid", "zone2");
            MockSimpleRPWorld.get().add(player1);
            MockSimpleRPWorld.get().changeZone(new ID("zone1"), player1);
            assertTrue(((SimpleRPZone) MockSimpleRPWorld.get().getRPZone(new ID("zone1"))).has(player1.getID()));
            MockSimpleRPWorld.get().changeZone(new ID("zone2"), player1);
            assertFalse(((SimpleRPZone) MockSimpleRPWorld.get().getRPZone(new ID("zone1"))).has(player1.getID()));
            assertTrue(((SimpleRPZone) MockSimpleRPWorld.get().getRPZone(new ID("zone1"))).isEmpty());
            assertTrue(((SimpleRPZone) MockSimpleRPWorld.get().getRPZone(new ID("zone2"))).has(player1.getID()));
            assertFalse(((SimpleRPZone) MockSimpleRPWorld.get().getRPZone(new ID("zone2"))).isEmpty());
        } catch (Exception e) {
            fail();
        }
    }
}
