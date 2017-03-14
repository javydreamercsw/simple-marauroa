package simple.server.core.entity.npc;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import static junit.framework.Assert.*;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import org.junit.Test;
import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.common.SizeLimitedArray;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.entity.npc.action.NPCAction;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.SimpleRPEvent;
import simple.server.core.event.TextEvent;
import simple.test.AbstractSystemTest;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
public class NPCTest extends AbstractSystemTest {

    private final IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);

    /**
     * Test of generateRPClass method, of class NPC.
     */
    @Test
    public void testGenerateRPClass() {
        System.out.println("generateRPClass");
        RPClass.hasRPClass(NPC.NAME);
    }

    /**
     * Test of processPrivateTextEvent method, of class NPC.
     */
    @Test
    public void testProcessPrivateTextEvent() {
        System.out.println("processPrivateTextEvent");
        RPEvent event = new PrivateTextEvent(NotificationType.NORMAL,
                "Hey!",
                "test",
                "test");
        NPC instance = new NPC(new RPObject(), "test");
        world.add(instance);
        RPEventListenerImpl listener = new RPEventListenerImpl();
        world.registerMonitor(instance, PrivateTextEvent.RPCLASS_NAME,
                listener);
        instance.processPrivateTextEvent(event);
        assertEquals(1, listener.getCount());
    }

    /**
     * Test of processTextEvent method, of class NPC.
     */
    @Test
    public void testProcessTextEvent() {
        System.out.println("processTextEvent");
        RPEvent event = new TextEvent(NotificationType.NORMAL,
                "Hello",
                "test");
        NPC instance = new NPC(new RPObject(), "test");
        world.add(instance);
        RPEventListenerImpl listener = new RPEventListenerImpl();
        world.registerMonitor(instance, PrivateTextEvent.RPCLASS_NAME,
                listener);
        world.registerMonitor(instance, TextEvent.RPCLASS_NAME,
                listener);
        instance.processTextEvent(event);
        assertEquals(0, listener.getCount());
        //Now add an action so the NPC replies
        instance.context.add(new NPCAction() {
            @Override
            public List<String> getKeywords() {
                return Arrays.asList("hola");
            }

            @Override
            public RPEvent onAction(RPEvent event, NPC npc) {
                return new TextEvent(NotificationType.NORMAL,
                        "Hello",
                        npc.getName());
            }

            @Override
            public String getDescription() {
                return "just a test";
            }
        });
        //Not matching the keyword
        instance.processTextEvent(event);
        assertEquals(0, listener.getCount());
        //Matching the keyword
        event.put(WellKnownActionConstant.TEXT, "hola");
        instance.processTextEvent(event);
        assertEquals(1, listener.getCount());
    }

    private class RPEventListenerImpl implements RPEventListener {

        private final Logger LOG
                = Logger.getLogger(RPEventListenerImpl.class.getSimpleName());

        public RPEventListenerImpl() {
        }
        private final SizeLimitedArray<String> queue = new SizeLimitedArray<>();
        private int count = 0;

        @Override
        public void onRPEvent(RPEvent event) {
            if (!queue.contains(event.get(SimpleRPEvent.EVENT_ID))) {
                queue.add(event.get(SimpleRPEvent.EVENT_ID));
                LOG.info(event.toString());
                if (event instanceof TextEvent) {

                } else if (event instanceof PrivateTextEvent) {

                }
                count++;
            }
        }

        public int getCount() {
            return count;
        }
    }
}
