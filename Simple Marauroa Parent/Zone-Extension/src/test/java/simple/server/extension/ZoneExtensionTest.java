package simple.server.extension;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
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
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.ISimpleRPZone;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TurnListener;
import simple.test.AbstractSystemTest;
import simple.test.TestPlayer;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class ZoneExtensionTest extends AbstractSystemTest {

    private static final Logger LOG
            = Logger.getLogger(ZoneExtensionTest.class.getName());

    /**
     * Test of onRPObjectAddToZone method, of class ZoneExtension.
     */
    @Test

    public void testOnRPObjectAddToZone() {
        System.out.println("onRPObjectAddToZone");
        int initial = getDelayedActions();
        new TestPlayer(new RPObject());
        assertEquals(initial + 3, getDelayedActions());
    }

    private int getDelayedActions() {
        int result = 0;
        for (Entry<Integer, Set<TurnListener>> entry
                : Lookup.getDefault().lookup(ITurnNotifier.class)
                        .getEventListForDebugging().entrySet()) {
            LOG.log(Level.INFO, "{0}:{1}",
                    new Object[]{entry.getKey(), entry.getValue().size()});
            result += entry.getValue().size();
        }
        return result;
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
        player.registerListener(ZoneEvent.RPCLASS_NAME,
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
            player.registerListener(ZoneEvent.RPCLASS_NAME,
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
        player.registerListener(ZoneEvent.RPCLASS_NAME,
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
        player.registerListener(PrivateTextEvent.RPCLASS_NAME,
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
        assertEquals(1, listener.getCount());
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
        player.registerListener(PrivateTextEvent.RPCLASS_NAME,
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
        assertEquals(2, listener.getCount());
        try {
            //Cleanup
            Lookup.getDefault().lookup(IRPWorld.class).removeRPZone(room);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
    }

    private class RPEventListenerImpl implements RPEventListener {

        private final Logger LOG
                = Logger.getLogger(RPEventListenerImpl.class.getSimpleName());

        public RPEventListenerImpl() {
        }
        private int count = 0;

        public void onRPEvent(RPEvent event) {
            LOG.info(event.toString());
            if (event instanceof ZoneEvent) {
                switch (event.getInt(ZoneEvent.ACTION)) {
                    case ZoneEvent.LISTZONES:
                        List<IRPZone> zones
                                = Lookup.getDefault().lookup(IRPWorld.class)
                                        .getZones();
                        for (IRPZone zone : zones) {
                            LOG.info(zone.getID().getID());
                        }
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

        public int getCount() {
            return count;
        }
    }
}
