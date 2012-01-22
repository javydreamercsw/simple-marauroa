
package simple.common;

/**
 * Gathers all Debug constants in one place
 *
 * @author mtotz
 *
 */
public final class Debug {

    /** server version */
    public static final String VERSION = "0.01";
    /** if this is enable tileset are loaded on demand. */
    public static final boolean VERY_FAST_CLIENT_START = true;
    /** enables cycling through the panel textures by clicking at the title bar */
    public static final boolean CYCLE_PANEL_TEXTURES = false;
    /**
     * should the creature ai and pathfinding be shown? Note: The server must
     * send these infos (CREATURES_DEBUG_SERVER)
     */
    public static final boolean CREATURES_DEBUG_CLIENT = false;
    /**
     * should the server send debug information about creature ai and
     * pathfinding to the client? Note: CREATURES_DEBUG_CLIENT should be enabled
     * too
     */
    public static final boolean CREATURES_DEBUG_SERVER = false;
    /**
     * This emulates perception losses. Never make this true and commit it to
     * CVS
     */
    public static final boolean EMULATE_PERCEPTION_LOSS = false;
    /**
     * Just a try to get webstart work without additional rights
     */
    public static boolean WEB_START_SANDBOX;
    /**
     * Log list sizes to find the memory leak.
     * It must be somewhere...
     */
    public static boolean SHOW_LIST_SIZES;

    /** no instance */
    private Debug() {
        // this is a collection of constants
    }
}
