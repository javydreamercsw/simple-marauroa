package simple.server.core.event;

import java.util.UUID;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import simple.server.core.event.api.IRPEvent;

/**
 * This class just wraps Marauroa's RPEvent to implement IRPEvent.
 * This should be considered a hack until the interfaces are within the 
 * Marauroa package (if they finally agree)
 * 
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public class SimpleRPEvent extends RPEvent implements IRPEvent {

    public static final String EVENT_ID = "event_id";

    public SimpleRPEvent(RPEvent event) {
        fill(event);
        //Add the event id if not already there
        if (!has(EVENT_ID)) {
            put(EVENT_ID, UUID.randomUUID().toString());
        }
    }

    protected SimpleRPEvent(String name) {
        super(name);
        //Add the event id if not already there
        if (!has(EVENT_ID)) {
            put(EVENT_ID, UUID.randomUUID().toString());
        }
    }

    protected static void addCommonAttributes(RPClass rpclass) {
        rpclass.add(DefinitionClass.ATTRIBUTE, EVENT_ID, Type.STRING);
    }
}
