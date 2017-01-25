package simple.server.core.engine;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.npc.NPC;
import simple.server.core.tool.Tool;
import simple.test.AbstractSystemTest;
import simple.test.TestPlayer;

/**
 * Test the custom methods of SimpleZone.
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SimpleZoneTest extends AbstractSystemTest {

    private final IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);

    @Test
    public void testZoneCreationDeletion() {
        //Add two players
        TestPlayer p1 = getTestPlayer("Player 1");
        TestPlayer p2 = getTestPlayer("Player 2");
        //Add NPC
        RPObject p3 = new RPObject();
        p3.put(Entity.NAME, "NPC");
        ISimpleRPZone d = (ISimpleRPZone) world.getDefaultZone();
        p3.put(simple.server.core.entity.Entity.ZONE_ID,
                d.getID().getID());
        world.add(p3);
        d = (ISimpleRPZone) world.getDefaultZone();
        assertEquals(2, d.getPlayers().size());
        assertEquals(1, d.getNPCS().size());
        //Create a new zone
        String zoneName = UUID.randomUUID().toString();
        world.addZone(zoneName);
        ISimpleRPZone zone = (ISimpleRPZone) world.getZone(zoneName);
        assertEquals(0, zone.getPlayers().size());
        assertEquals(0, zone.getNPCS().size());
        //Move players to new zone
        world.changeZone(zone.getID(), p1);
        world.changeZone(zone.getID(), p2);
        world.changeZone(zone.getID(), p3);
        d = (ISimpleRPZone) world.getDefaultZone();
        assertEquals(0, d.getPlayers().size());
        assertEquals(0, d.getNPCS().size());
        zone = (ISimpleRPZone) world.getZone(zoneName);
        assertEquals(2, zone.getPlayers().size());
        assertEquals(1, zone.getNPCS().size());
        try {
            //Delete zone, everyone should move back to default
            world.removeRPZone(zone.getID());
            d = (ISimpleRPZone) world.getDefaultZone();
            assertEquals(2, d.getPlayers().size());
            assertEquals(0, d.getNPCS().size());
            zone = (ISimpleRPZone) world.getZone(zoneName);
            assertNull(zone);
            //Attempt to delete the default zone
            world.removeRPZone(d.getID());
            d = (ISimpleRPZone) world.getDefaultZone();
            assertNotNull(d);
            assertEquals(2, d.getPlayers().size());
            assertEquals(0, d.getNPCS().size());
        } catch (Exception ex) {
            Logger.getLogger(SimpleZoneTest.class.getName())
                    .log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * This test checks that the objects are assigned the default zone if: 1)
     * None has been assigned 2) An invalid/non existing zone has been assigned.
     */
    @Test
    public void testAutomaticZoneAssignment() {
        RPObject o1 = new RPObject();
        Tool.setName(o1, "Test");
        assertEquals(0, world.getDefaultZone().size());
        world.add(o1);
        assertEquals(1, world.getDefaultZone().size());
        RPObject o2 = new RPObject();
        Tool.setName(o2, "Test2");
        o2.put(Entity.ZONE_ID, UUID.randomUUID().toString());
        assertEquals(1, world.getDefaultZone().size());
        world.add(o2);
        assertEquals(2, world.getDefaultZone().size());
    }

    @Test
    public void testChangingZone() {
        TestPlayer p1 = getTestPlayer("Player 1");
        NPC npc = new NPC(new RPObject(), "NPC");
        String name = "Test";
        world.add(npc);
        assertTrue(world.getDefaultZone().has(p1.getID()));
        assertTrue(world.getDefaultZone().has(npc.getID()));
        //Create new zone to move to
        world.addZone(name);
        IRPZone zone = world.getZone(name);
        //Move player
        world.changeZone(zone.getID(), p1);
        //Left lobby
        assertFalse(world.getDefaultZone().has(p1.getID()));
        assertTrue(world.getDefaultZone().has(npc.getID()));
        //Moved to new zone
        assertTrue(zone.has(p1.getID()));
        assertFalse(zone.has(npc.getID()));
        //Move NPC
        world.changeZone(zone.getID(), npc);
        //Left lobby
        assertFalse(world.getDefaultZone().has(p1.getID()));
        assertFalse(world.getDefaultZone().has(npc.getID()));
        //Moved to new zone
        assertTrue(zone.has(p1.getID()));
        assertTrue(zone.has(npc.getID()));
    }
}
