package dreamer.card.game.storage.database.persistence.controller.exceptions;

import java.util.logging.Logger;

public class NonexistentEntityException extends Exception {
    public NonexistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public NonexistentEntityException(String message) {
        super(message);
    }
    private static final Logger LOG = Logger.getLogger(NonexistentEntityException.class.getName());
}
