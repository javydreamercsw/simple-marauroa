package simple.server.core.event;

import java.util.Date;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import static simple.server.core.action.WellKnownActionConstant.*;
import simple.server.core.event.api.IRPEvent;

/**
 * A text message.
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com> based on work
 * from hendrik
 */
@ServiceProvider(service = IRPEvent.class)
public class PrivateTextEvent extends SimpleRPEvent {

    public static final String RPCLASS_NAME = "private_text_event";
    public static final String TEXT_TYPE = "texttype";
    public static final String CHANNEL = "channel";

    /**
     * Creates the rpclass.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass rpclass = new RPClass(RPCLASS_NAME);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT_TYPE, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, CHANNEL, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TARGET, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, FROM, Type.LONG_STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TIME, Type.LONG_STRING);
            addCommonAttributes(rpclass);
        }
    }

    @Override
    public String getRPClassName() {
        return RPCLASS_NAME;
    }

    public PrivateTextEvent() {
        super(RPCLASS_NAME);
    }

    /**
     * Creates a new text event.
     *
     * @param type NotificationType
     * @param text Text
     */
    public PrivateTextEvent(NotificationType type, String text) {
        super(RPCLASS_NAME);
        put(TEXT_TYPE, type.name());
        put(TEXT, text);
        put(TIME, new Date().toString());
    }

    /**
     * Creates a new text event.
     *
     * @param type NotificationType
     * @param text Text
     * @param target target player
     * @param from player who spoke
     */
    public PrivateTextEvent(NotificationType type, String text, String target,
            String from) {
        super(RPCLASS_NAME);
        put(TEXT_TYPE, type.name());
        put(TEXT, text);
        put(TARGET, target);
        put(FROM, from);
        put(TIME, new Date().toString());
    }
}
