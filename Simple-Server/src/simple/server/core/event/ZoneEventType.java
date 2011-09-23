/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.event;

/**
 * Event types used in the new Zone notifier.
 * 
 * @author kymara (based on TutorialEventType by hendrik)
 */
public enum ZoneEventType {

    VISIT_X(
    "text");
    private String message;

    /**
     * create a new ZoneEventType.
     *
     * @param message
     *            human readable message
     */
    private ZoneEventType(String message) {
        this.message = message;
    }

    /**
     * get the descriptive message.
     *
     * @return message
     */
    String getMessage() {
        return message;
    }
}
