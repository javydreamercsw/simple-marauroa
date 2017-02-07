package simple.server.extension;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.common.SizeLimitedArray;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.ISimpleRPZone;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.SimpleRPEvent;
import simple.test.AbstractSystemTest;
import simple.test.TestPlayer;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class ZoneExtensionTest extends AbstractSystemTest {

    private static final Logger LOG
            = Logger.getLogger(ZoneExtensionTest.class.getName());
    private final IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);

    /**
     * Test of onRPObjectAddToZone method, of class ZoneExtension.
     */
    @Test
    public void testOnRPObjectAddToZone() {
        System.out.println("onRPObjectAddToZone");
        RPEventListenerImpl listener = new RPEventListenerImpl();
        HashMap<String, RPEventListener> listeners = new HashMap<>();
        listeners.put(ZoneEvent.RPCLASS_NAME, listener);
        getTestPlayer(UUID.randomUUID().toString(), listeners);
        assertEquals(2, listener.getCount());
    }

    /**
     * Test of onAction method, of class ZoneExtension.
     */
    @Test
    public void testListPlayersAction() {
        System.out.println("List Players Action");
        TestPlayer player = new TestPlayer(new RPObject());
        RPAction action = new RPAction();
        RPEventListenerImpl listener = new RPEventListenerImpl();
        world.registerMonitor(player, ZoneEvent.RPCLASS_NAME,
                listener);
        ZoneExtension instance = new ZoneExtension();
        action.put(ZoneExtension.OPERATION, ZoneEvent.LISTPLAYERS);
        action.put(WellKnownActionConstant.TYPE, ZoneEvent.RPCLASS_NAME);
        action.put(ZoneEvent.FIELD,
                Lookup.getDefault().lookup(IRPWorld.class)
                        .getDefaultZone().getID().getID());
        instance.onAction(player, action);
        assertEquals(1, listener.getCount());
    }

    /**
     * Test of onAction method, of class ZoneExtension.
     */
    @Test
    public void testJoinZoneAction() {
        try {
            System.out.println("Join Zone Action");
            TestPlayer player = new TestPlayer(new RPObject());
            RPAction action = new RPAction();
            RPEventListenerImpl listener = new RPEventListenerImpl();
            String room = UUID.randomUUID().toString();
            assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
            action.put(WellKnownActionConstant.TYPE, ZoneEvent.RPCLASS_NAME);
            action.put(ZoneEvent.ROOM, room);
            action.put(ZoneExtension.OPERATION, ZoneEvent.ADD);
            ZoneExtension instance = new ZoneExtension();
            //Create room
            world.registerMonitor(player, ZoneEvent.RPCLASS_NAME,
                    listener);
            instance.onAction(player, action);
            assertEquals(0, Lookup.getDefault().lookup(IRPWorld.class)
                    .getZone(room).size());
            //Join Zone
            action.put(ZoneExtension.OPERATION, ZoneEvent.JOIN);
            instance.onAction(player, action);
            Thread.sleep(1000);
            assertEquals(1, ((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                    .getZone(room)).getPlayers().size());
            try {
                //Cleanup
                Lookup.getDefault().lookup(IRPWorld.class).removeRPZone(room);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of onAction method, of class ZoneExtension.
     */
    @Test
    public void testListZonesAction() {
        System.out.println("List Zones Action");
        TestPlayer player = new TestPlayer(new RPObject());
        RPAction action = new RPAction();
        RPEventListenerImpl listener = new RPEventListenerImpl();
        world.registerMonitor(player, ZoneEvent.RPCLASS_NAME,
                listener);
        action.put(WellKnownActionConstant.TYPE, ZoneEvent.RPCLASS_NAME);
        action.put(ZoneExtension.OPERATION, ZoneEvent.LISTZONES);
        ZoneExtension instance = new ZoneExtension();
        instance.onAction(player, action);
        assertEquals(1, listener.getCount());
        String room = UUID.randomUUID().toString();
        assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        action.put(ZoneEvent.ROOM, room);
        action.put(ZoneExtension.OPERATION, ZoneEvent.ADD);
        //Create room
        instance.onAction(player, action);
        action.put(ZoneExtension.OPERATION, ZoneEvent.LISTZONES);
        instance.onAction(player, action);
        try {
            //Cleanup
            Lookup.getDefault().lookup(IRPWorld.class).removeRPZone(room);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of onAction method, of class ZoneExtension.
     */
    @Test
    public void testRemoveAction() {
        System.out.println("Remove Action");
        TestPlayer player = new TestPlayer(new RPObject());
        RPAction action = new RPAction();
        RPEventListenerImpl listener = new RPEventListenerImpl();
        world.registerMonitor(player, PrivateTextEvent.RPCLASS_NAME,
                listener);
        action.put(WellKnownActionConstant.TYPE, ZoneEvent.RPCLASS_NAME);
        String room = UUID.randomUUID().toString();
        assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        action.put(ZoneEvent.ROOM, room);
        action.put(ZoneExtension.OPERATION, ZoneEvent.ADD);
        ZoneExtension instance = new ZoneExtension();
        //Create room
        instance.onAction(player, action);
        assertTrue(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        action.put(ZoneExtension.OPERATION, ZoneEvent.REMOVE);
        //Delete room
        instance.onAction(player, action);
        assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        assertEquals(2, listener.getCount());
    }

    /**
     * Test of onAction method, of class ZoneExtension.
     */
    @Test
    public void testUpdateAction() {
        System.out.println("Update Action");
        TestPlayer player = new TestPlayer(new RPObject());
        RPAction action = new RPAction();
        action.put(WellKnownActionConstant.TYPE, ZoneEvent.RPCLASS_NAME);
        String room = UUID.randomUUID().toString();
        assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        action.put(ZoneEvent.ROOM, room);
        action.put(ZoneExtension.OPERATION, ZoneEvent.ADD);
        ZoneExtension instance = new ZoneExtension();
        //Create room
        instance.onAction(player, action);
        assertEquals("", ((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).getDescription());
        action.put(ZoneExtension.OPERATION, ZoneEvent.UPDATE);
        String desc = "Desc";
        action.put(ZoneExtension.DESC, desc);
        instance.onAction(player, action);
        assertEquals(desc, ((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).getDescription());
        assertTrue(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isPassword(""));
        String pw = UUID.randomUUID().toString();
        action.put(ZoneExtension.PASSWORD, pw);
        instance.onAction(player, action);
        assertEquals(desc, ((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).getDescription());
        assertTrue(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isPassword(pw));
        try {
            //Cleanup
            Lookup.getDefault().lookup(IRPWorld.class).removeRPZone(room);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of onAction method, of class ZoneExtension.
     */
    @Test
    public void testCreateAction() {
        System.out.println("Create Action");
        TestPlayer player = new TestPlayer(new RPObject());
        RPAction action = new RPAction();
        RPEventListenerImpl listener = new RPEventListenerImpl();
        world.registerMonitor(player, PrivateTextEvent.RPCLASS_NAME,
                listener);
        action.put(WellKnownActionConstant.TYPE, ZoneEvent.RPCLASS_NAME);
        action.put(ZoneExtension.OPERATION, ZoneEvent.ADD);
        ZoneExtension instance = new ZoneExtension();
        //Missing room name
        instance.onAction(player, action);
        assertEquals(1, listener.getCount());
        String room = UUID.randomUUID().toString();
        assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        action.put(ZoneEvent.ROOM, room);
        //Add room
        instance.onAction(player, action);
        assertTrue(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        assertFalse(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isLocked());
        //With password
        room = UUID.randomUUID().toString();
        String pw = UUID.randomUUID().toString();
        assertFalse(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        action.put(ZoneEvent.ROOM, room);
        action.put(ZoneExtension.PASSWORD, pw);
        //Add room
        instance.onAction(player, action);
        assertTrue(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        assertTrue(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isLocked());
        assertTrue(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isPassword(pw));
        //Add same room
        instance.onAction(player, action);
        assertTrue(Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(room));
        assertTrue(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isLocked());
        assertTrue(((ISimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class)
                .getZone(room)).isPassword(pw));
        assertEquals(4, listener.getCount());
    }

    private class RPEventListenerImpl implements RPEventListener {

        private final Logger LOG
                = Logger.getLogger(RPEventListenerImpl.class.getSimpleName());
        private final SizeLimitedArray<String> queue = new SizeLimitedArray<>();

        public RPEventListenerImpl() {
        }
        private int count = 0;

        @Override
        public void onRPEvent(RPEvent event) {
            if (!queue.contains(event.get(SimpleRPEvent.EVENT_ID))) {
                queue.add(event.get(SimpleRPEvent.EVENT_ID));
                LOG.info(event.toString());
                if (event instanceof ZoneEvent) {
                    switch (event.getInt(ZoneEvent.ACTION)) {
                        case ZoneEvent.LISTZONES:
                            List<IRPZone> zones
                                    = Lookup.getDefault().lookup(IRPWorld.class)
                                            .getZones();
                            zones.forEach((zone) -> {
                                LOG.info(zone.getID().getID());
                            });
                            assertTrue(
                                    new StringTokenizer(event.get(ZoneEvent.FIELD),
                                            event.get(ZoneExtension.SEPARATOR))
                                            .countTokens() <= zones.size());
                            break;
                        case ZoneEvent.ADD:
                            break;
                    }
                }
                count++;
            }
        }

        public int getCount() {
            return count;
        }
    }
}
