package simple.server.core.engine;

import marauroa.common.game.IRPZone.ID;
import static org.junit.Assert.*;
import org.junit.Test;
import simple.SimpleServerT;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.mock.MockSimpleRPWorld;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class ChangeZoneTest extends SimpleServerT {

    private static ClientObject player1;

    /**
     * Test of getGameName method, of class SimpleDatabase.
     *
     */
    @Test
    public void changeZone() {
        try {
            System.out.println("changeZone");
            SimpleSingletonRepository.getEntityManager();
            player1 = new ClientObject(ClientObject.createEmptyZeroLevelPlayer("player1"));
            MockSimpleRPWorld.get().addRPZone(new SimpleRPZone("zone1"));
            MockSimpleRPWorld.get().addRPZone(new SimpleRPZone("zone2"));
            assertTrue(MockSimpleRPWorld.get().hasRPZone(new ID("zone1")));
            assertTrue(MockSimpleRPWorld.get().hasRPZone(new ID("zone2")));
            assertFalse(MockSimpleRPWorld.get().hasRPZone(new ID("zone3")));
            //Add to zone2
            assertEquals(0, MockSimpleRPWorld.get().getRPZone("zone2").size());
            MockSimpleRPWorld.get().getRPZone("zone2").add(player1);
            assertTrue(MockSimpleRPWorld.get()
                    .getRPZone(new ID("zone2")).has(player1.getID()));
            assertEquals(1, MockSimpleRPWorld.get().getRPZone("zone2").size());
            //Change zone to zone 1
            assertEquals(0, MockSimpleRPWorld.get().getRPZone("zone1").size());
            MockSimpleRPWorld.get().changeZone(new ID("zone1"), player1);
            assertEquals(0, MockSimpleRPWorld.get().getRPZone("zone2").size());
            assertEquals(1, MockSimpleRPWorld.get().getRPZone("zone1").size());
            assertTrue(MockSimpleRPWorld.get()
                    .getRPZone(new ID("zone1")).has(player1.getID()));
            //Change zone to zone 2
            MockSimpleRPWorld.get().changeZone(new ID("zone2"), player1);
            assertEquals(1, MockSimpleRPWorld.get().getRPZone("zone2").size());
            assertEquals(0, MockSimpleRPWorld.get().getRPZone("zone1").size());
            assertFalse(MockSimpleRPWorld.get()
                    .getRPZone(new ID("zone1")).has(player1.getID()));
            assertTrue(((ISimpleRPZone) MockSimpleRPWorld.get()
                    .getRPZone(new ID("zone1"))).isEmpty());
            assertTrue(MockSimpleRPWorld.get()
                    .getRPZone(new ID("zone2")).has(player1.getID()));
            assertFalse(((ISimpleRPZone) MockSimpleRPWorld.get()
                    .getRPZone(new ID("zone2"))).isEmpty());
        }
        catch (NullPointerException e) {
            //Expected when adding player
        }
        catch (Exception e) {
            fail();
        }
    }
}
