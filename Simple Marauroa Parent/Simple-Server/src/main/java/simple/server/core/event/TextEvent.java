package simple.server.core.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import static simple.server.core.action.WellKnownActionConstant.FROM;
import static simple.server.core.action.WellKnownActionConstant.TEXT;
import static simple.server.core.action.WellKnownActionConstant.TIME;
import simple.server.core.event.api.IRPEvent;

/**
 * A public text message.
 *
 * @author hendrik
 */
@ServiceProvider(service = IRPEvent.class)
public class TextEvent extends SimpleRPEvent {

    public static final String RPCLASS_NAME = "text";
    public static final String TEXT_TYPE = "texttype";
    private final SimpleDateFormat defaultFormat
            = new SimpleDateFormat("HH:mm:SS");
    private final SimpleDateFormat extendedFormat
            = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");

    /**
     * Creates the rpclass.
     */
    @Override
    public void generateRPClass() {
        if (!RPClass.hasRPClass(RPCLASS_NAME)) {
            RPClass rpclass = new RPClass(RPCLASS_NAME);
            rpclass.add(DefinitionClass.ATTRIBUTE, TEXT, Type.LONG_STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, FROM, Type.LONG_STRING);
            rpclass.add(DefinitionClass.ATTRIBUTE, TIME, Type.LONG_STRING);
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
        put(TIME, formatDate(new Date()));
    }

    /**
     * Creates a new text event.
     *
     * @param type NotificationType
     * @param text Text
     * @param from Player's name (the one who spoke)
     */
    public TextEvent(NotificationType type, String text, String from) {
        super(RPCLASS_NAME);
        put(TEXT_TYPE, type.name());
        put(TEXT, text);
        put(FROM, from);
        put(TIME, formatDate(new Date()));
    }

    /**
     * Format date.
     *
     * @param date date to format
     * @return formatted date
     */
    private String formatDate(Date date) {
        return formatDate(date, defaultFormat);
    }

    /**
     * Format date.
     *
     * @param date date to format
     * @param format format for the date
     * @return formatted date
     */
    private String formatDate(Date date, DateFormat format) {
        if (format == null) {
            format = defaultFormat;
        }
        if (isYesterday(date)) {
            //Message was sent yesterday, add the date
            format = extendedFormat;
        }
        return format.format(date);
    }

    private boolean isYesterday(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_WEEK);
        c.setTime(new Date());
        int now = c.get(Calendar.DAY_OF_WEEK);
        return day < now;
    }
}
