package simple.server.core.event;


import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import simple.common.NotificationType;
import static simple.server.core.action.WellKnownActionConstants.TARGET;
import static simple.server.core.action.WellKnownActionConstants.TEXT;
import simple.server.core.event.api.IPrivateChatEvent;

/**
 * A text message.
 *
 * @author hendrik
 */
public class PrivateTextEvent extends SimpleRPEvent implements IPrivateChatEvent {

    public static final String RPCLASS_NAME = "private_text";
    public static final String TEXT_TYPE = "texttype";
    public static final String CHANNEL = "channel";

    /**
     * Creates the rpclass.
     */
    public static void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass rpclass = new RPClass(RPCLASS_NAME);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT_TYPE, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, CHANNEL, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TARGET, Type.STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
            addCommonAttributes(rpclass);
        }
    }

    public static String getRPClassName() {
        return RPCLASS_NAME;
    }

    public PrivateTextEvent() {
        super(RPCLASS_NAME);
        registerIfNeeded();
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
        registerIfNeeded();
    }
    
    /**
     * Creates a new text event.
     *
     * @param type NotificationType
     * @param text Text
     * @param target  
     */
    public PrivateTextEvent(NotificationType type, String text, String target) {
        super(RPCLASS_NAME);
        put(TEXT_TYPE, type.name());
        put(TEXT, text);
        put(TARGET, target);
        registerIfNeeded();
    }

    private void registerIfNeeded() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            generateRPClass();
        }
    }
}
