package simple.server.core.event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;
import simple.server.core.event.api.IRPEvent;

/**
 * This class just wraps Marauroa's RPEvent to implement IRPEvent. This should
 * be considered a hack until the interfaces are within the Marauroa package (if
 * they finally agree)
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public abstract class SimpleRPEvent extends RPEvent implements IRPEvent {

    public static final String EVENT_ID = "event_id";
    private DateFormat dateFormat
            = new SimpleDateFormat("HH:mm:SS");
    private DateFormat extendedFormat
            = new SimpleDateFormat("dd-MM-yy:HH:mm:SS");

    public SimpleRPEvent(RPEvent event) {
        fill(event);
        update();
    }

    public void update() {
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

    /**
     * Format date.
     *
     * @param date date to format
     * @return formatted date
     */
    protected final String formatDate(Date date) {
        return formatDate(date, dateFormat);
    }

    /**
     * Format date.
     *
     * @param date date to format
     * @param format format for the date
     * @return formatted date
     */
    protected final String formatDate(Date date, DateFormat format) {
        if (format == null) {
            format = dateFormat;
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

    /**
     * Set the date format.
     *
     * @param df format to set.
     */
    protected void setDateFormat(DateFormat df) {
        dateFormat = df;
    }

    /**
     * Set the extended date format.
     *
     * @param df format to set.
     */
    protected void setExtendedDateFormat(DateFormat df) {
        extendedFormat = df;
    }
}
