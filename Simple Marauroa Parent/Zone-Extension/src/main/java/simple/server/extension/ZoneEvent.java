package simple.server.extension;

import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.event.SimpleRPEvent;
import simple.server.core.event.api.IRPEvent;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IRPEvent.class)
public class ZoneEvent extends SimpleRPEvent {

    public static final String FIELD = "field", RPCLASS_NAME = "zone_event",
            ACTION = "action", DESC = "description";
    public static final int ADD = 1, UPDATE = 2, REMOVE = 3, LISTZONES = 4,
            NEEDPASS = 5, JOIN = 6;

    /**
     * Creates the rpclass.
     */
    @Override
    public void generateRPClass() {
        RPClass rpclass = new RPClass(RPCLASS_NAME);
        rpclass.add(DefinitionClass.ATTRIBUTE, FIELD, Type.STRING);
        rpclass.add(DefinitionClass.ATTRIBUTE, DESC, Type.LONG_STRING);
        rpclass.add(DefinitionClass.ATTRIBUTE, ACTION, Type.INT);
        rpclass.add(DefinitionClass.ATTRIBUTE, ZoneExtension.SEPARATOR,
                Type.STRING);
        addCommonAttributes(rpclass);
    }

    /**
     * @return the ROOM
     */
    public static String getField() {
        return FIELD;
    }

    /**
     * @return the ACTION
     */
    public static String getAction() {
        return ACTION;
    }

    public ZoneEvent() {
        super(RPCLASS_NAME);
    }

    /**
     * Creates a new room event.
     *
     * @param a action containing all the info so it can be resent from client
     * with the password
     * @param action Only works with NEEDPASS
     */
    public ZoneEvent(RPAction a, int action) {
        super(RPCLASS_NAME);
        if (action == NEEDPASS) {
            fill(a);
        }
        put(ACTION, action);
    }

    /**
     * Creates a new room event.
     *
     * @param zone room added/deleted from server
     * @param action either add or remove
     */
    public ZoneEvent(SimpleRPZone zone, int action) {
        super(RPCLASS_NAME);
        put(FIELD, zone.getName());
        //Don't add the description if deleting the room...
        if (zone.getDescription() != null && !zone.getDescription().isEmpty()
                && action != REMOVE) {
            put(DESC, zone.getDescription());
        }
        put(ACTION, action);
    }

    /**
     * Creates a new room event.
     *
     * @param s string to be sent
     * @param action either add or remove
     */
    public ZoneEvent(String s, int action) {
        super(RPCLASS_NAME);
        put(FIELD, s);
        put(ACTION, action);
    }

    @Override
    public String getRPClassName() {
        return RPCLASS_NAME;
    }
}
