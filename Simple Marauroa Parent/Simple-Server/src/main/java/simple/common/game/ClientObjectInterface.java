
package simple.common.game;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
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

    /**
     * Is the client disconnected?
     * @return true if disconnected
     */
    boolean isDisconnected();

    /**
     * Is a ghost?
     * @return true if is ghost
     */
    boolean isGhost();

    /**
     * Is invisible to creatures?
     * @return true if invisible
     */
    boolean isInvisibleToCreatures();

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

    /**
     * Called when this object is removed from a zone.
     *
     * @param zone
     * The zone this was removed from.
     */
    void onRemoved(IRPZone zone);

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

    @Override
    String toString();

    /**
     * Get name
     * @return Name
     */
    public String getName();
    
    /**
     * Set name
     * @param name name to set
     */
    public void setName(String name);

    @Override
    public SimpleRPZone getZone();

    /**
     * Get title
     * @return title
     */
    public String getTitle();

    /**
     * Spread changes to the world
     */
    public void notifyWorldAboutChanges();

    /**
     * Update this object
     */
    public void update();

    /**
     * Sent private text
     * @param type Notification type
     * @param message message's text
     */
    public void sendPrivateText(NotificationType type, String message);
    
    /**
     * Send public text
     * @param text message's text
     */
    public void sendText(String text);
    
    /**
     * Add an RPEvent to the ClientObject
     * @param event 
     */
    public void addEvent(RPEvent event);
    
    /**
     * Destroy the player. Usually liberating resources.
     */
     public void destroy();
     /**
      * Create a ClientObject from an RPObject
      * @param object RPObject to create from
      * @return Created object
      */
     public ClientObjectInterface create(RPObject object);
     
     /**
      * Create default ClientObject
      * @param name name of the object
      * @return Created object
      */
     public ClientObjectInterface createDefaultClientObject(String name);
     
     /**
      * Create default ClientObject
      * @param object Object to create from
      * @return Created object
      */
     public ClientObjectInterface createDefaultClientObject(RPObject object);
}

