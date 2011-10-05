package simple.server.extension;

import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.server.core.event.SimpleRPEvent;
import simple.server.core.event.api.IRPEvent;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = IRPEvent.class)
public class MonitorEvent extends SimpleRPEvent {

    public static final String ACTION = "action", RPCLASS_NAME = "monitor_event",
            STRING = "string", OBJECT = "object";
    public static final int GET_ZONE_INFO = 1, GET_ZONES = 2, REGISTER = 3,
            UNREGISTER = 4;

    public MonitorEvent(String string, int action) {
        super(RPCLASS_NAME);
        put(ACTION, action);
        put(STRING, string);
    }

    public MonitorEvent(Object object, int action) {
        super(RPCLASS_NAME);
        put(ACTION, action);
        put(OBJECT, object.toString());
    }

    public MonitorEvent() {
        super(RPCLASS_NAME);
    }

    @Override
    public String getRPClassName() {
        return RPCLASS_NAME;
    }

    /**
     * Creates the rpclass.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass rpclass = new RPClass(RPCLASS_NAME);
            rpclass.add(DefinitionClass.ATTRIBUTE, ACTION, Type.INT);
            rpclass.add(DefinitionClass.ATTRIBUTE, OBJECT, Type.LONG_STRING);
            addCommonAttributes(rpclass);
        }
    }
}
