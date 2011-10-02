
package simple.common.game;

import java.util.List;
import marauroa.common.game.IRPZone;
import marauroa.common.net.Serializable;
import simple.common.NotificationType;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.RPEntityInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ClientObjectInterface extends RPEntityInterface, Serializable, Comparable {

    String DEFAULT_ENTRY_ZONE = "Default Zone";
    /**
     * This is the assigned key for encryption purposes on the client
     */
    String KEY = "#key";
    String RESET_ENTRY_ZONE = "Default Zone";

    /**
     * Add a player ignore entry.
     *
     * @param name
     * The player name.
     * @param duration
     * The ignore duration (in minutes), or <code>0</code> for
     * infinite.
     * @param reply
     * The reply.
     *
     * @return <code>true</code> if value changed, <code>false</code> if
     * there was a problem.
     */
    boolean addIgnore(String name, int duration, String reply);

    /**
     * @return the adminLevel
     */
    int getAdminLevel();

    /**
     * Get the away message.
     *
     * @return The away message, or <code>null</code> if unset.
     */
    String getAwayMessage();

    /**
     * Get the grumpy message.
     *
     * @return The grumpy message, or <code>null</code> if unset.
     */
    String getGrumpyMessage();

    /**
     * Determine if a player is on the ignore list and return their reply
     * message.
     *
     * @param name
     * The player name.
     *
     * @return The custom reply message (including an empty string), or
     * <code>null</code> if not ignoring.
     */
    String getIgnore(String name);

    /**
     * Get a keyed string value on a named slot.
     *
     * @param name
     * The slot name.
     * @param key
     * The value key.
     *
     * @return The keyed value of the slot, or <code>null</code> if not set.
     */
    String getKeyedSlot(String name, String key);

    /**
     * Gets the name of the last player who privately talked to this player
     * using the /tell command, or null if nobody has talked to this player
     * since he logged in.
     * @return
     */
    String getLastPrivateChatter();

    /**
     * Gets the player's current status in the given quest.
     *
     * @param name
     * The quest's name
     * @return the player's status in the quest
     */
    String getQuest(String name);

    List<String> getQuests();

    /**
     * Checks whether the player has made any progress in the given quest or
     * not. For many quests, this is true right after the quest has been
     * started.
     *
     * @param name
     * The quest's name
     * @return true iff the player has made any progress in the quest
     */
    boolean hasQuest(String name);

    /**
     * Check if another player should be notified that this player is away. This
     * assumes the player has already been checked for away. Players will be
     * reminded once an hour.
     *
     * @param name
     * The name of the other player.
     *
     * @return <code>true</code> if the player should be notified.
     */
    boolean isAwayNotifyNeeded(String name);

    boolean isDisconnected();

    boolean isGhost();

    boolean isInvisibleToCreatures();

    /**
     * Checks whether the player has completed the given quest or not.
     *
     * @param name
     * The quest's name
     * @return true iff the quest has been completed by the player
     */
    boolean isQuestCompleted(String name);

    /**
     * Is the named quest in one of the listed states?
     *
     * @param name
     * quest
     * @param states
     * valid states
     * @return true, if the quest is in one of theses states, false otherwise
     */
    boolean isQuestInState(String name, String... states);

    /**
     * Notifies this player that the given player has logged out.
     *
     * @param who
     * The name of the player who has logged out.
     */
    void notifyOffline(String who);

    /**
     * Notifies this player that the given player has logged in.
     *
     * @param who
     * The name of the player who has logged in.
     */
    void notifyOnline(String who);

    /**
     * Called when this object is added to a zone.
     *
     * @param zone
     * The zone this was added to.
     */
    void onAdded(IRPZone zone);

    void onRemoved(IRPZone zone);

    void removeQuest(String name);

    /**
     * Clear out all recorded away responses.
     */
    void resetAwayReplies();

    /**
     * Sends a message that only this player can read.
     *
     * @param text
     * the message.
     */
    void sendPrivateText(String text);

    /**
     * @param adminLevel the adminLevel to set
     */
    void setAdminLevel(int adminLevel);

    /**
     * Set the away message.
     *
     * @param message
     * An away message, or <code>null</code>.
     */
    void setAwayMessage(final String message);

    /**
     * @param disconnected the disconnected to set
     */
    void setDisconnected(boolean disconnected);

    /**
     * Set whether this player is a ghost (invisible/non-interactive).
     *
     * @param ghost
     * <code>true</code> if a ghost.
     */
    void setGhost(final boolean ghost);

    /**
     * Set the grumpy message.
     *
     * @param message
     * A grumpy message, or <code>null</code>.
     */
    void setGrumpyMessage(final String message);

    /**
     * Set whether this player is invisible to creatures.
     *
     * @param invisible
     * <code>true</code> if invisible.
     */
    void setInvisible(final boolean invisible);

    /**
     * Set a keyed string value on a named slot.
     *
     * @param name
     * The slot name.
     * @param key
     * The value key.
     * @param value
     * The value to assign (or remove if <code>null</code>).
     *
     * @return <code>true</code> if value changed, <code>false</code> if
     * there was a problem.
     */
    boolean setKeyedSlot(String name, String key, String value);

    /**
     * Sets the name of the last player who privately talked to this player
     * using the /tell command. It needs to be stored non-persistently so that
     * /answer can be used.
     * @param lastPrivateChatterName
     */
    void setLastPrivateChatter(String lastPrivateChatterName);

    /**
     * Allows to store the player's current status in a quest in a string. This
     * string may, for instance, be "started", "done", a semicolon- separated
     * list of items that need to be brought/NPCs that need to be met, or the
     * number of items that still need to be brought. Note that the string
     * "done" has a special meaning: see isQuestComplete().
     *
     * @param name
     * The quest's name
     * @param status
     * the player's status in the quest. Set it to null to completely
     * reset the player's status for the quest.
     */
    void setQuest(String name, String status);

    @Override
    String toString();

    public String getName();

    @Override
    public SimpleRPZone getZone();

    public String getTitle();

    public void notifyWorldAboutChanges();

    public void update();

    public void sendPrivateText(NotificationType type, String message);
    
    public void sendText(String text);
    /**
     * Static methods create, createDefaultObject, generateRPClass and destroy must also be created in the
     * ClientObject implementation or extends example ClientObject to use
     * the already implemented ones.
     */
}

