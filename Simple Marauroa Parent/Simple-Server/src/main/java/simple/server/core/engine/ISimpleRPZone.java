package simple.server.core.engine;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.TransferContent;
import simple.common.game.ClientObjectInterface;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ISimpleRPZone extends IRPZone {

    /**
     * Add object to the zone.
     *
     * @param object object to add
     * @param player player adding the object
     */
    void add(final RPObject object, final ClientObjectInterface player);

    /**
     * Apply a public event to the zone.
     *
     * @param event event to apply
     */
    void applyPublicEvent(RPEvent event);

    /**
     * Apply a public event to the zone with a delay.
     *
     * @param event event to apply
     * @param delay delay in turns
     */
    void applyPublicEvent(final RPEvent event, final int delay);

    /**
     * Return whether the zone contains one or more players.
     *
     * @return
     */
    boolean containsPlayer();

    List<TransferContent> getContents();

    /**
     * @return the description
     */
    String getDescription();

    /**
     * Get the zone name. This is the same as <code>getID().getID()</code>, only
     * cleaner to use.
     *
     * @return The zone name.
     */
    String getName();

    /**
     * Get a player by the specified name.
     *
     * @param name player's name
     * @return the requested player or null if not found.
     */
    ClientObjectInterface getPlayer(final String name);

    /**
     * Gets all players in this zone.
     *
     * @return A list of all players.
     */
    Collection<RPObject> getPlayers();

    /**
     * Gets all NPCs in this zone.
     *
     * @return A list of all NPC's.
     */
    Collection<RPEntityInterface> getNPCS();

    /**
     * Get a specific NPC.
     *
     * @param name NPC's name.
     * @return NPC or null if not found.
     */
    RPEntityInterface getNPC(String name);

    /**
     * Gets all players in this zone.
     *
     * @param separator Character to separate the names in the list.
     * @return A list of all players.
     */
    String getPlayersInString(final String separator);

    /**
     * @return the deleteWhenEmpty
     */
    boolean isDeleteWhenEmpty();

    /**
     * Return whether the zone is completely empty.
     *
     * @return
     */
    boolean isEmpty();

    /**
     * @return if zone is locked
     */
    boolean isLocked();

    /**
     * Check if the password is the right one.
     *
     * @param pass password to check
     * @return true if correct, false otherwise
     */
    boolean isPassword(final String pass);

    /**
     * Removes object from zone.
     *
     * @param object
     * @return the removed object
     */
    RPObject remove(final RPObject object);

    /**
     * @param deleteWhenEmpty the deleteWhenEmpty to set
     */
    void setDeleteWhenEmpty(boolean deleteWhenEmpty);

    /**
     * @param description the description to set
     */
    void setDescription(String description);

    /**
     * Set password for the zone.
     *
     * @param pass password to set
     * @throws IOException
     */
    void setPassword(final String pass) throws IOException;

    /**
     * Output the contents of the zone.
     */
    void showZone();

    /**
     * Unlock zone.
     */
    void unlock();

    /**
     * Get a list of both players and non-players currently on the zone.
     *
     * @return list of both players and non-players currently on the zone
     */
    Collection<RPObject> getZoneContents();
}
