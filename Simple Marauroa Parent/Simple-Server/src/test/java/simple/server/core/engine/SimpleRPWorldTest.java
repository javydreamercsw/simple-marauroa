package simple.server.core.engine;

import java.util.UUID;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.entity.npc.NPC;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;
import simple.test.AbstractSystemTest;
import simple.test.TestPlayer;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class SimpleRPWorldTest extends AbstractSystemTest {

    /**
     * Test of setDefaultZone method, of class SimpleRPWorld.
     */
    @Test
    public void testSetDefaultZone() {
        System.out.println("setDefaultZone");
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        String name = UUID.randomUUID().toString();
        assertFalse(instance.hasRPZone(name));
        instance.addZone(name);
        instance.setDefaultZone(instance.getZone(name));
        assertEquals(name, instance.getDefaultZone().getID().getID());
    }

    /**
     * Test of get method, of class SimpleRPWorld.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        assertNotNull(SimpleRPWorld.get());
    }

    /**
     * Test of deleteIfEmpty method, of class SimpleRPWorld.
     */
    @Test
    public void testDeleteIfEmpty() {
        System.out.println("deleteIfEmpty");
        String name = UUID.randomUUID().toString();
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        TestPlayer player = new TestPlayer(new RPObject());
        instance.addZone(name);
        instance.changeZone(name, player);//Move it to new zone
        instance.deleteIfEmpty(name);
        assertTrue(instance.hasRPZone(name));
        instance.changeZone(instance.getDefaultZone().getID().getID(), player);
        //Move it to default zone
        instance.deleteIfEmpty(name);
        assertTrue(instance.hasRPZone(name));
        //Mark it as deletable.
        ((ISimpleRPZone) instance.getZone(name)).setDeleteWhenEmpty(true);
        instance.deleteIfEmpty(name);
        assertFalse(instance.hasRPZone(name));
    }

    /**
     * Test of listZones method, of class SimpleRPWorld.
     */
    @Test
    public void testListZones() {
        System.out.println("listZones");
        String separator = "*";
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        StringBuilder result = instance.listZones(separator);
        System.out.println(result.toString());
        assertTrue(result.length() == 0);
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();
        instance.addZone(name1);
        result = instance.listZones(separator);
        System.out.println(result.toString());
        assertTrue(result.length() > 0);
        assertFalse(result.toString().contains(separator));
        assertTrue(result.toString().contains(name1));
        assertFalse(result.toString().contains(name2));
        instance.addZone(name2);
        result = instance.listZones(separator);
        System.out.println(result.toString());
        assertTrue(result.length() > 0);
        assertTrue(result.toString().contains(separator));
        assertTrue(result.toString().contains(name1));
        assertTrue(result.toString().contains(name2));
    }

    /**
     * Test of applyPrivateEvent method, of class SimpleRPWorld.
     */
    @Test
    public void testApplyPrivateEvent() {
        System.out.println("applyPrivateEvent");
        RPEvent event = new TextEvent("Test", "Me");
        String target = "";
        int delay = 0;
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        boolean expResult = false;
        boolean result = instance.applyPrivateEvent(target, event, delay);
        assertEquals(expResult, result);
        //Add the target
        TestPlayer p = new TestPlayer(new RPObject());
        TestPlayer p2 = new TestPlayer(new RPObject());
        TextEventListener l = new TextEventListener();
        TextEventListener l2 = new TextEventListener();
        p.registerListener(TextEvent.RPCLASS_NAME, l);
        p2.registerListener(TextEvent.RPCLASS_NAME, l2);
        target = p.getName();
        expResult = true;
        result = instance.applyPrivateEvent(target, event, delay);
        assertEquals(expResult, result);
        assertEquals(1, l.getCount());
        assertEquals(0, l2.getCount());
    }

    /**
     * Test of applyPublicEvent method, of class SimpleRPWorld.
     */
    @Test
    public void testApplyPublicEvent_RPEvent() {
        System.out.println("applyPublicEvent");
        RPEvent event = new TextEvent("Test", "Me");
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        //Add the target
        TestPlayer p = new TestPlayer(new RPObject());
        TestPlayer p2 = new TestPlayer(new RPObject());
        //Move player 2 to a different zone
        String zone = UUID.randomUUID().toString();
        instance.addZone(zone);
        instance.changeZone(zone, p2);
        TextEventListener l = new TextEventListener();
        TextEventListener l2 = new TextEventListener();
        p.registerListener(TextEvent.RPCLASS_NAME, l);
        p2.registerListener(TextEvent.RPCLASS_NAME, l2);
        boolean expResult = true;
        boolean result = instance.applyPublicEvent(event);
        assertEquals(expResult, result);
        assertEquals(1, l.getCount());
        assertEquals(1, l2.getCount());
    }

    /**
     * Test of applyPublicEvent method, of class SimpleRPWorld.
     */
    @Test
    public void testApplyPublicEvent_ISimpleRPZone_RPEvent() {
        System.out.println("applyPublicEvent");
        RPEvent event = new TextEvent("Test", "Me");
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        //Add the target
        TestPlayer p = new TestPlayer(new RPObject());
        TestPlayer p2 = new TestPlayer(new RPObject());
        //Move player 2 to a different zone
        String zone = UUID.randomUUID().toString();
        instance.addZone(zone);
        instance.changeZone(zone, p2);
        TextEventListener l = new TextEventListener();
        TextEventListener l2 = new TextEventListener();
        p.registerListener(TextEvent.RPCLASS_NAME, l);
        p2.registerListener(TextEvent.RPCLASS_NAME, l2);
        boolean expResult = true;
        boolean result = instance
                .applyPublicEvent((ISimpleRPZone) instance.getZone(zone), event);
        assertEquals(expResult, result);
        assertEquals(0, l.getCount());
        assertEquals(1, l2.getCount());
    }

    /**
     * Test of updateRPZoneDescription method, of class SimpleRPWorld.
     */
    @Test
    public void testUpdateRPZoneDescription() {
        System.out.println("updateRPZoneDescription");
        String zone = UUID.randomUUID().toString();
        String desc = "new desc";
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        instance.addZone(zone);
        assertEquals("", ((ISimpleRPZone) instance.getZone(zone)).getDescription());
        instance.updateRPZoneDescription(zone, desc);
        assertEquals(desc, ((ISimpleRPZone) instance.getZone(zone)).getDescription());
    }

    /**
     * Test of emptyZone method, of class SimpleRPWorld.
     */
    @Test
    public void testEmptyZone_IRPZone() {
        System.out.println("emptyZone");
        String zone = UUID.randomUUID().toString();
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        instance.addZone(zone);
        TestPlayer p = new TestPlayer(new RPObject());
        NPC npc = new NPC(new RPObject());
        instance.add(npc);
        instance.changeZone(zone, p);
        instance.changeZone(zone, npc);
        assertEquals(2, instance.getZone(zone).size());
        instance.emptyZone(instance.getZone(zone));
        assertEquals(0, instance.getZone(zone).size());
    }

    /**
     * Test of emptyZone method, of class SimpleRPWorld.
     */
    @Test
    public void testEmptyZone_IRPZoneID() {
        System.out.println("emptyZone");
        String zone = UUID.randomUUID().toString();
        SimpleRPWorld instance = (SimpleRPWorld) Lookup.getDefault()
                .lookup(IRPWorld.class);
        instance.addZone(zone);
        TestPlayer p = new TestPlayer(new RPObject());
        NPC npc = new NPC(new RPObject());
        instance.add(npc);
        instance.changeZone(zone, p);
        instance.changeZone(zone, npc);
        assertEquals(2, instance.getZone(zone).size());
        instance.emptyZone(instance.getZone(zone).getID());
        assertEquals(0, instance.getZone(zone).size());
    }

    private class TextEventListener implements RPEventListener<TextEvent> {

        public TextEventListener() {
        }
        private int count = 0;

        @Override
        public void onRPEvent(TextEvent event) {
            System.out.println(event);
            count++;
        }

        /**
         * @return the count
         */
        public int getCount() {
            return count;
        }
    }

    private class PrivateTextEventListener implements RPEventListener<PrivateTextEvent> {

        public PrivateTextEventListener() {
        }
        private int count = 0;

        @Override
        public void onRPEvent(PrivateTextEvent event) {
            System.out.println(event);
            count++;
        }

        /**
         * @return the count
         */
        public int getCount() {
            return count;
        }
    }
}
