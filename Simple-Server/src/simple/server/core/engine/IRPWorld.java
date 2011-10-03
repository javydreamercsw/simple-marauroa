package simple.server.core.engine;

import java.util.List;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IRPWorld extends Iterable<IRPZone> {

    boolean addPlayer(RPObject object);

    void addRPZone(IRPZone zone);

    IRPZone removeRPZone(IRPZone zone);

    IRPZone removeRPZone(IRPZone.ID zoneid) throws java.lang.Exception;

    void addZone(String name);

    void addZone(String name, String description);

    boolean applyPrivateEvent(String target, RPEvent event);

    boolean applyPrivateEvent(String target, RPEvent event, int delay);

    boolean applyPublicEvent(RPEvent event);

    boolean applyPublicEvent(SimpleRPZone zone, RPEvent event);

    boolean applyPublicEvent(SimpleRPZone zone, RPEvent event, int delay);

    void changeZone(String newzoneid, RPObject object);

    void changeZone(IRPZone.ID newzoneid, RPObject object);

    void deleteIfEmpty(String zone);

    IRPZone getRPZone(String zone);

    /**
     * Gives the number of turns that will take place during a given number of
     * seconds.
     *
     * @param seconds The number of seconds.
     *
     * @return The number of turns.
     */
    int getTurnsInSeconds(int seconds);

    /**
     * Finds a zone by its id.
     *
     * @param id The zone's id
     *
     * @return The matching zone, or
     * <code>null</code> if not found.
     */
    SimpleRPZone getZone(final String id);

    List<SimpleRPZone> getZones();

    /**
     * Gets all zones in this world.
     *
     * @param separator Character to separate the names in the list.
     * @return zones in this world in a list separated with the separator
     * character.
     */
    StringBuilder listZones(String separator);

    void onFinish();

    /**
     * World initialization
     */
    void onInit();

    void showWorld();

    SimpleRPZone updateRPZoneDescription(String zone, String desc);

    public void requestSync(RPObject object);

    public void modify(RPObject object);

    public boolean hasRPZone(IRPZone.ID zoneid);
}
