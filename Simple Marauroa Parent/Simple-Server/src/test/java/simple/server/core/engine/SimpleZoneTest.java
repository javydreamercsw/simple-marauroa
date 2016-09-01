package simple.server.core.engine;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.Entity;
import simple.server.core.entity.npc.NPC;
import simple.test.AbstractSystemTest;
import simple.test.TestPlayer;

/**
 * Test the custom methods of SimpleZone.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class SimpleZoneTest extends AbstractSystemTest {

    private IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);

    @Test
    public void testZoneCreationDeletion() {
        //Add two players
        TestPlayer p1 = getTestPlayer("Player 1");
        TestPlayer p2 = getTestPlayer("Player 2");
        //Add NPC
        RPObject p3 = new NPC(new RPObject());
        p3.put(Entity.NAME, "NPC");
        ISimpleRPZone d = world.getDefaultZone();
        p3.put(simple.server.core.entity.Entity.ZONE_ID,
                d.getID().getID());
        world.add(p3);
        d = world.getDefaultZone();
        assertEquals(2, d.getPlayers().size());
        assertEquals(1, d.getNPCS().size());
        //Create a new zone
        String zoneName = UUID.randomUUID().toString();
        world.addZone(zoneName);
        ISimpleRPZone zone = world.getRPZone(zoneName);
        assertEquals(0, zone.getPlayers().size());
        assertEquals(0, zone.getNPCS().size());
        //Move players to new zone
        world.changeZone(zone.getID(), p1);
        world.changeZone(zone.getID(), p2);
        world.changeZone(zone.getID(), p3);
        d = world.getDefaultZone();
        assertEquals(0, d.getPlayers().size());
        assertEquals(0, d.getNPCS().size());
        zone = world.getRPZone(zoneName);
        assertEquals(2, zone.getPlayers().size());
        assertEquals(1, zone.getNPCS().size());
        try {
            //Delete zone, everyone should move back to default
            world.removeRPZone(zone.getID());
            d = world.getDefaultZone();
            assertEquals(2, d.getPlayers().size());
            assertEquals(0, d.getNPCS().size());
            zone = world.getRPZone(zoneName);
            assertNull(zone);
            //Attempt to delete the default zone
            world.removeRPZone(d.getID());
            d = world.getDefaultZone();
            assertNotNull(d);
            assertEquals(2, d.getPlayers().size());
            assertEquals(0, d.getNPCS().size());
        } catch (Exception ex) {
            Logger.getLogger(SimpleZoneTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
