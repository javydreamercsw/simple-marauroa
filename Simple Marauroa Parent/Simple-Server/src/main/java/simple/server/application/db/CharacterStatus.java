package simple.server.application.db;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public enum CharacterStatus {
    //This seems like it's not even implemented/defined in Marauroa. Marking as active by default
    //See: https://sourceforge.net/p/arianne/developers/269/
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String text;

    private CharacterStatus(String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
