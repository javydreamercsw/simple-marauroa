package simple.server.core.entity.npc;

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
import static simple.server.core.action.WellKnownActionConstant.FROM;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import simple.server.core.engine.IRPWorld;
import simple.server.core.entity.Entity;
import static simple.server.core.entity.Entity.NAME;
import simple.server.core.entity.ExtensibleRPClass;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.api.RPEventListener;
import simple.server.core.entity.npc.action.NPCAction;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;

/**
 *
 * @author Javier A. Ortiz Bultron javier.ortiz.78@gmail.com
 */
@ServiceProvider(service = RPEntityInterface.class, position = 200)
public class NPC extends Entity implements RPEventListener {

    public static final String NPC_TYPE = "NPC_Type";
    public static final String DEFAULT_RP_CLASSNAME = "NPC";
    protected final InstanceContent context = new InstanceContent();
    protected final Lookup localLookup = new AbstractLookup(context);
    /**
     * the logger instance.
     */
    private static final Logger LOG = Logger.getLogger(NPC.class.getSimpleName());

    public NPC(RPObject object) {
        super(object);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        if (object.has(NAME)) {
            Lookup.getDefault().lookup(IRPWorld.class)
                    .registerMonitor(object, TextEvent.RPCLASS_NAME, NPC.this);
            Lookup.getDefault().lookup(IRPWorld.class)
                    .registerMonitor(object, PrivateTextEvent.RPCLASS_NAME,
                            NPC.this);
        }
    }

    public NPC(RPObject object, String name) {
        super(object);
        put(NAME, name);
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
        Lookup.getDefault().lookup(IRPWorld.class)
                .registerMonitor(object, TextEvent.RPCLASS_NAME, NPC.this);
        Lookup.getDefault().lookup(IRPWorld.class)
                .registerMonitor(object, PrivateTextEvent.RPCLASS_NAME,
                        NPC.this);
    }

    public NPC() {
        RPCLASS_NAME = DEFAULT_RP_CLASSNAME;
    }

    @Override
    public void generateRPClass() {
        try {
            if (!RPClass.hasRPClass(RPCLASS_NAME)) {
                ExtensibleRPClass npc = new ExtensibleRPClass(RPCLASS_NAME);
                npc.isA(RPEntity.DEFAULT_RPCLASS);
                npc.addAttribute(NPC_TYPE, Type.STRING);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    /**
     * Handle an uncommon event.
     *
     * @param event Event to process.
     */
    protected void defaultProcessEvent(RPEvent event) {
        switch (event.getName()) {
            case TextEvent.RPCLASS_NAME:
                if (!event.get(FROM).equals(get(NAME))) {
                    processTextEvent(event);
                }
                break;
            case PrivateTextEvent.RPCLASS_NAME:
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

    protected void processPrivateTextEvent(RPEvent event) {
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
                        .unregisterMonitor(NPC.this,
                                TextEvent.RPCLASS_NAME, NPC.this);
                Lookup.getDefault().lookup(IRPWorld.class)
                        .unregisterMonitor(NPC.this,
                                PrivateTextEvent.RPCLASS_NAME, NPC.this);
                //Register to the new name
                Lookup.getDefault().lookup(IRPWorld.class)
                        .registerMonitor(value, TextEvent.RPCLASS_NAME, NPC.this);
                Lookup.getDefault().lookup(IRPWorld.class)
                        .registerMonitor(value, PrivateTextEvent.RPCLASS_NAME,
                                NPC.this);
            }
            super.put(attribute, value);
        } else {
            super.put(attribute, value);
        }
    }

    @Override
    public void onRPEvent(RPEvent event) {
        processEvent(event);
    }
}
