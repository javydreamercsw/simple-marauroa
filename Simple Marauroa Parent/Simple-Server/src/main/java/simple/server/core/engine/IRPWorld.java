package simple.server.core.engine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import simple.server.core.entity.api.RPEventListener;

/**
 *
 * @author Javier A. Ortiz Bultrón <javier.ortiz.78@gmail.com>
 */
public interface IRPWorld extends Iterable<IRPZone> {

    /**
     * Add RPZone
     *
     * @param zone zone to add
     */
    void addRPZone(IRPZone zone);

    /**
     * Remove zone from world (use with caution). Make sure to move all players
     * in it to another zone. This is ignored for the default zone.
     *
     * @param zone Zone to remove
     * @return Removed zone
     * @throws java.lang.Exception if something goes wrong.
     */
    IRPZone removeRPZone(IRPZone zone) throws Exception;

    /**
     * Remove zone from world (use with caution). Make sure to move all players
     * in it to another zone. This is ignored for the default zone.
     *
     * @param zone Zone to remove
     * @return Removed zone
     * @throws java.lang.Exception if something goes wrong.
     */
    IRPZone removeRPZone(String zone) throws Exception;

    /**
     * Remove zone from world (use with caution). Make sure to move all players
     * in it to another zone. This is ignored for the default zone.
     *
     * @param zoneid Id of the zone to remove
     * @return Removed zone
     * @throws java.lang.Exception
     */
    IRPZone removeRPZone(IRPZone.ID zoneid) throws Exception;

    /**
     * Add RPZone
     *
     * @param name zone to add
     */
    void addRPZone(String name);

    /**
     * Add RPZone
     *
     * @param name zone to add
     * @param description zone description
     */
    void addRPZone(String name, String description);

    /**
     * Apply event to target
     *
     * @param target event's target
     * @param event event to apply
     * @return true if successful
     */
    boolean applyPrivateEvent(String target, RPEvent event);

    /**
     * Apply event to target after a delay (in turns)
     *
     * @param target event's target
     * @param event event to apply
     * @param delay amount of turns before applying the event
     * @return true if successful
     */
    boolean applyPrivateEvent(String target, RPEvent event, int delay);

    /**
     * Apply event to everyone
     *
     * @param event event to apply
     * @return true if successful
     */
    boolean applyPublicEvent(RPEvent event);

    /**
     * Apply event to everyone in zone
     *
     * @param zone Zone to apply it
     * @param event Event to apply
     * @return true if successful
     */
    boolean applyPublicEvent(ISimpleRPZone zone, RPEvent event);

    /**
     * Apply event to everyone in zone after a delay (in turns)
     *
     * @param zone Zone to apply it
     * @param event Event to apply
     * @param delay amount of turns before applying the event
     * @return true if successful
     */
    boolean applyPublicEvent(ISimpleRPZone zone, RPEvent event, int delay);

    /**
     * Change object to new zone
     *
     * @param newzoneid new zone
     * @param object object to change
     */
    void changeZone(String newzoneid, RPObject object);

    /**
     * Change object to new zone
     *
     * @param newzoneid new zone
     * @param object object's id
     */
    void changeZone(IRPZone.ID newzoneid, RPObject object);

    /**
     * Delete zone if empty. This is ignored for the default zone.
     *
     * @param zone Zone to delete
     */
    void deleteIfEmpty(String zone);

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
     * @return The matching zone, or <code>null</code> if not found.
     */
    IRPZone getZone(final String id);

    /**
     * Finds a zone by its id.
     *
     * @param id The zone's id
     *
     * @return The matching zone, or <code>null</code> if not found.
     */
    IRPZone getZone(final IRPZone.ID id);

    /**
     * Get a list of zones
     *
     * @return list of zones
     */
    List<IRPZone> getZones();

    /**
     * Gets all zones in this world.
     *
     * @param separator Character to separate the names in the list.
     * @return zones in this world in a list separated with the separator
     * character.
     */
    StringBuilder listZones(String separator);

    /**
     * Actions when finishing the world
     */
    void onFinish();

    /**
     * World initialization
     */
    void onInit();

    /**
     * Text representation of world
     */
    void showWorld();

    /**
     * Change zone's description
     *
     * @param zone zone to update
     * @param desc new description
     * @return new zone
     */
    ISimpleRPZone updateRPZoneDescription(String zone, String desc);

    /**
     * Request sync from object
     *
     * @param object object to request for
     */
    public void requestSync(RPObject object);

    /**
     * Modify object
     *
     * @param object modified object
     */
    public void modify(RPObject object);

    /**
     * Does the zone exist?
     *
     * @param zoneid zone to check
     * @return true if exists
     */
    public boolean hasRPZone(IRPZone.ID zoneid);

    /**
     * Does the zone exist?
     *
     * @param zone zone to check
     * @return true if exists
     */
    public boolean hasRPZone(String zone);

    /**
     * Get the default zone
     *
     * @return Default Zone
     */
    public IRPZone getDefaultZone();

    /**
     * Create system accounts.
     *
     * @throws SQLException
     * @throws IOException
     */
    public void createSystemAccount() throws SQLException, IOException;

    /**
     * Register RPObject monitors.
     *
     * @param target Entity name to monitor
     * @param eventClassName Event name to listen to
     * @param listener Listener
     */
    public void registerMonitor(String target, String eventClassName,
            RPEventListener listener);

    /**
     * Register RPObject monitors.
     *
     * @param target Entity name to monitor
     * @param eventClassName Event name to listen to
     * @param listener Listener
     */
    public void registerMonitor(RPObject target, String eventClassName,
            RPEventListener listener);

    /**
     * Register RPObject monitors.
     *
     * @param target Entity name to monitor
     * @param eventClassName Event name to listen to
     * @param listener Listener
     */
    public void unregisterMonitor(String target, String eventClassName,
            RPEventListener listener);

    /**
     * Register RPObject monitors.
     *
     * @param target Entity name to monitor
     * @param eventClassName Event name to listen to
     * @param listener Listener
     */
    public void unregisterMonitor(RPObject target, String eventClassName,
            RPEventListener listener);

    /**
     * Get ID of object by name.
     *
     * @param name to look for.
     * @return ID of the object or null if not found.
     */
    public RPObject.ID getID(String name);

    /**
     * Add RPObject to the world.
     *
     * @param rpObject Object to add.
     */
    public void add(RPObject rpObject);

    /**
     * Remove RPObject from world.
     *
     * @param id Object to remove.
     * @return Removed object.
     */
    public RPObject remove(RPObject.ID id);

    /**
     * Checks if zone is valid. If not, object is assigned to default zone. This
     * scenario occurs if Object is stored into database as being on a zone that
     * no longer exists when it is loaded back from database.
     *
     * @param object Object to check zone for.
     */
    public void checkZone(RPObject object);

    /**
     * Remove all players and NPC's from the specified zone.
     *
     * @param zone Zone to clean.
     */
    public void emptyZone(IRPZone zone);

    /**
     * Remove all players and NPC's from the specified zone.
     *
     * @param zoneid Zone to clean.
     */
    public void emptyZone(IRPZone.ID zoneid);

    /**
     * Get a player from the world.
     *
     * @param name Name of the player.
     * @return Player or null if not found.
     */
    public RPObject getPlayer(String name);

    /**
     * Get a player from the world.
     *
     * @param name Name of the player.
     * @return Player or null if not found.
     */
    public RPObject getNPC(String name);
}
