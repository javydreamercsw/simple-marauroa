package simple.server.core.event;

import java.io.IOException;
import marauroa.common.Configuration;

/**
 * Event types used in the tutorial.
 *
 * @author hendrik
 */
public enum TutorialEventType {

    LOGIN("Hi, welcome to %g!"),
    TIMED_PASSWORD(
            "Remember to keep your password completely secret, never tell it to another friend, player, or even admin.");
    private final String message;

    /**
     * Creates a new TutorialEventType.
     *
     * @param message human readable message
     */
    private TutorialEventType(String message) {
        String g = "Simple-Game";
        try {
            g = Configuration.getConfiguration().get("server_name");
        } catch (IOException ex) {
        }
        this.message = message.replaceAll("%g", g);
    }

    /**
     * Gets the descriptive message.
     *
     * @return message
     */
    String getMessage() {
        return message;
    }
}
