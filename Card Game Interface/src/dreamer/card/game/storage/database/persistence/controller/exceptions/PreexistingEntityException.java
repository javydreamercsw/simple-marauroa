package dreamer.card.game.storage.database.persistence.controller.exceptions;

import java.util.logging.Logger;

public class PreexistingEntityException extends Exception {
    public PreexistingEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public PreexistingEntityException(String message) {
        super(message);
    }
    private static final Logger LOG = Logger.getLogger(PreexistingEntityException.class.getName());
}
