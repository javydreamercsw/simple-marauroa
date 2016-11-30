package simple.server.core.entity.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.SizeLimitedArray;
import static simple.server.core.action.WellKnownActionConstant.FROM;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.Entity;
import static simple.server.core.entity.Entity.NAME;
import simple.server.core.entity.ExtensibleRPClass;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.MonitoreableEntity;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.entity.npc.action.NPCAction;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = RPEntityInterface.class, position = 200)
public class NPC extends Entity implements MonitoreableEntity {

    public static final String NPC_TYPE = "NPC_Type";
    private final Map<String, List<RPEventListener>> listeners;
    private final List<String> processedEvents = new SizeLimitedArray<>(100);
    protected final InstanceContent context = new InstanceContent();
    protected final Lookup localLookup = new AbstractLookup(context);
    /**
     * the logger instance.
     */
    private static final Logger LOG = Logger.getLogger(NPC.class.getSimpleName());

    public NPC(RPObject object) {
        super(object);
        this.listeners = new HashMap<>();
        RPCLASS_NAME = "NPC";
        if (object.has(NAME)) {
            Lookup.getDefault().lookup(IRPWorld.class)
                    .registerMonitor(object.get(NAME), NPC.this);
        }
    }

    public NPC(RPObject object, String name) {
        super(object);
        this.listeners = new HashMap<>();
        put(NAME, name);
        RPCLASS_NAME = "NPC";
        Lookup.getDefault().lookup(IRPWorld.class)
                .registerMonitor(object.get(NAME), NPC.this);
    }

    public NPC() {
        this.listeners = new HashMap<>();
        RPCLASS_NAME = "NPC";
    }

    @Override
    public void generateRPClass() {
        try {
            if (!RPClass.hasRPClass(RPCLASS_NAME)) {
                ExtensibleRPClass npc = new ExtensibleRPClass(RPCLASS_NAME);
                npc.isA(RPEntity.DEFAULT_RPCLASS);
                npc.addAttribute(NPC_TYPE, Type.STRING);
            }
        }
        catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void registerListener(String eventClassName,
            RPEventListener listener) {
        synchronized (listeners) {
            if (!listeners.containsKey(eventClassName)) {
                listeners.put(eventClassName, new ArrayList<>());
            }
            List<RPEventListener> list = listeners.get(eventClassName);
            if (!list.contains(listener)) {
                list.add(listener);
            }
        }
    }

    @Override
    public void unregisterListener(String eventClassName,
            RPEventListener listener) {
        synchronized (listeners) {
            if (listeners.containsKey(eventClassName)) {
                List<RPEventListener> list = listeners.get(eventClassName);
                if (list.contains(listener)) {
                    list.remove(listener);
                }
            }
        }
    }

    @Override
    public void modify(RPObject obj) {
        for (RPEvent event : obj.events()) {
            processEvent(event);
            if (listeners.containsKey(event.getName())) {
                for (RPEventListener l : listeners.get(event.getName())) {
                    l.onRPEvent(event);
                }
            }
        }
        obj.clearEvents();
    }

    /**
     * Handle an uncommon event.
     *
     * @param event Event to process.
     */
    protected void defaultProcessEvent(RPEvent event) {
        if (!isAlreadyProcessed(event)) {
            switch (event.getName()) {
                case TextEvent.RPCLASS_NAME:
                    markEventAsProcessed(event);
                    if (!event.get(FROM).equals(get(NAME))) {
                        processTextEvent(event);
                    }
                    break;
                case PrivateTextEvent.RPCLASS_NAME:
                    markEventAsProcessed(event);
                    processPrivateTextEvent(event);
                    break;
                default:
                    //Not handled by default.
                    LOG.log(Level.WARNING,
                            "Unhandled event type: {0}\n{1}\n{2}",
                            new Object[]{event.getName(), event,
                                getClass().getSimpleName()});
            }
        }
    }

    protected void processPrivateTextEvent(RPEvent event) {
        markEventAsProcessed(event);
        LOG.fine("Processing private text...");
        if (event.has(FROM)) {//System messages have no from
            //By default let player know you can't handle secrets.
            RPEvent reply = new PrivateTextEvent(NotificationType.NEGATIVE,
                    "Why so much secrecy? I'm not good keeping secrets.",
                    event.get(FROM),
                    get(NAME));
            Lookup.getDefault().lookup(IRPWorld.class)
                    .applyPrivateEvent(event.get(FROM), reply);
        } else {
            LOG.log(Level.FINE, "No source:\n{0}", event.toString());
        }
    }

    protected void processTextEvent(RPEvent event) {
        markEventAsProcessed(event);
        RPEvent reply = null;
        //Check on the default commands
        for (NPCAction action : getLocalLookup().lookupAll(NPCAction.class)) {
            if (action.getKeywords().contains(event.get(TEXT).trim().toLowerCase())) {
                reply = action.onAction(event, this);
                break;
            }
        }
        if (reply != null) {
            Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(reply);
        }
    }

    /**
     * @return the localLookup
     */
    public final Lookup getLocalLookup() {
        return localLookup;
    }

    @Override
    public void processEvent(RPEvent event) {
        defaultProcessEvent(event);
    }

    @Override
    public final void put(String attribute, String value) {
        //Detect if name is being set.
        if (attribute.equals(NAME)) {
            if (has(NAME) && !get(NAME).trim().isEmpty()) {
                //Unregister monitor and register to new name
                Lookup.getDefault().lookup(IRPWorld.class)
                        .unregisterMonitor(get(NAME), this);
            }
            super.put(attribute, value);
            //Register to the new name
            Lookup.getDefault().lookup(IRPWorld.class)
                    .registerMonitor(get(NAME), NPC.this);
        } else {
            super.put(attribute, value);
        }
    }

    @Override
    public void markEventAsProcessed(RPEvent event) {
        if (!isAlreadyProcessed(event)) {
            processedEvents.add(event.get("event_id"));
        }
    }

    @Override
    public boolean isAlreadyProcessed(RPEvent event) {
        return processedEvents.contains(event.get("event_id"));
    }
}
