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

    void add(final RPObject object, final ClientObjectInterface player);

    void applyPublicEvent(RPEvent event);

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
     * Gets all non-players in this zone.
     *
     * @return A list of all non-players.
     */
    List<RPObject> getNonPlayers();

    ClientObjectInterface getPlayer(final String name);

    /**
     * Gets all players in this zone.
     *
     * @return A list of all players.
     */
    Collection<ClientObjectInterface> getPlayers();
    
    /**
     * Gets all NPCs in this zone.
     *
     * @return A list of all NPC's.
     */
    Collection<RPEntityInterface> getNPCS();

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
     * @return the locked
     */
    boolean isLocked();

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

    void setPassword(final String pass) throws IOException;

    void showZone();

    void unlock();
}
