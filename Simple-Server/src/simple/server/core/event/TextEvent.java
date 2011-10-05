package simple.server.core.event;

import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.server.core.event.api.IRPEvent;

/**
 * A public text message.
 *
 * @author hendrik
 */
@ServiceProvider(service = IRPEvent.class)
public class TextEvent extends SimpleRPEvent {

    public static final String RPCLASS_NAME = "text";
    private static final String TEXT = "text", FROM = "from";
    private static final String TEXT_TYPE = "texttype";

    /**
     * Creates the rpclass.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass rpclass = new RPClass(RPCLASS_NAME);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, FROM, Type.LONG_STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT_TYPE, Type.STRING);
            addCommonAttributes(rpclass);
        }
    }

    @Override
    public String getRPClassName() {
        return RPCLASS_NAME;
    }

    public TextEvent() {
        super(RPCLASS_NAME);
    }

    /**
     * Creates a new text event.
     *
     * @param text Text
     * @param from Player's name (the one who spoke)
     */
    public TextEvent(String text, String from) {
        super(RPCLASS_NAME);
        put(TEXT, text);
        put(FROM, from);
    }

    /**
     * Creates a new text event.
     *
     * @param type NotificationType
     * @param text Text
     * @param from
     */
    public TextEvent(NotificationType type, String text, String from) {
        super(RPCLASS_NAME);
        put(TEXT_TYPE, type.name());
        put(TEXT, text);
    }
}
