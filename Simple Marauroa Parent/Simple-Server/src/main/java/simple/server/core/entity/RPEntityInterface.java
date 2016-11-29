package simple.server.core.entity;

import marauroa.common.game.RPEvent;
import simple.common.Constants;
import simple.common.NotificationType;
import simple.server.core.engine.ISimpleRPZone;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RPEntityInterface extends Constants {

    /**
     * Called when this object is added to a zone.
     *
     * @param zone The zone this was added to.
     */
    public void onAdded(ISimpleRPZone zone);

    /**
     * Get current zone;
     *
     * @return current zone
     */
    public ISimpleRPZone getZone();

    /**
     * Action(s) to take when removed from a zone.
     *
     * @param zone
     */
    public void onRemoved(ISimpleRPZone zone);

    /**
     * Generate this RPClass
     */
    public void generateRPClass();

    /**
     * Is a ghost?
     *
     * @return true if is ghost
     */
    boolean isGhost();

    /**
     * Set whether this player is a ghost (invisible/non-interactive).
     *
     * @param ghost <code>true</code> if a ghost.
     */
    void setGhost(final boolean ghost);

    /**
     * Get title
     *
     * @return title
     */
    public String getTitle();

    /**
     * @param adminLevel the adminLevel to set
     */
    void setAdminLevel(int adminLevel);

    /**
     * @return the adminLevel
     */
    int getAdminLevel();

    /**
     * Sends a message that only this player can read.
     *
     * @param text the message.
     */
    void sendPrivateText(String text);

    /**
     * Get name
     *
     * @return Name
     */
    public String getName();

    /**
     * Set name
     *
     * @param name name to set
     */
    public void setName(String name);

    /**
     * Update this object
     */
    public void update();

    /**
     * Spread changes to the world
     */
    public void notifyWorldAboutChanges();

    /**
     * Sent private text
     *
     * @param type Notification type
     * @param message message's text
     */
    public void sendPrivateText(NotificationType type, String message);

    /**
     * Send public text
     *
     * @param text message's text
     */
    public void sendText(String text);

    /**
     * Add an RPEvent to the ClientObject
     *
     * @param event
     */
    public void addEvent(RPEvent event);

    /**
     * Destroy the player. Usually liberating resources.
     */
    public void destroy();

    /**
     * Check if another player should be notified that this player is away. This
     * assumes the player has already been checked for away. Players will be
     * reminded once an hour.
     *
     * @param name The name of the other player.
     *
     * @return <code>true</code> if the player should be notified.
     */
    boolean isAwayNotifyNeeded(String name);

    /**
     * Is the client disconnected?
     *
     * @return true if disconnected
     */
    boolean isDisconnected();

    /**
     * Is invisible to creatures?
     *
     * @return true if invisible
     */
    boolean isInvisibleToCreatures();

    /**
     * Notifies this player that the given player has logged out.
     *
     * @param who The name of the player who has logged out.
     */
    void notifyOffline(String who);

    /**
     * Notifies this player that the given player has logged in.
     *
     * @param who The name of the player who has logged in.
     */
    void notifyOnline(String who);

    /**
     * Clear out all recorded away responses.
     */
    void resetAwayReplies();

    /**
     * Set the away message.
     *
     * @param message An away message, or <code>null</code>.
     */
    void setAwayMessage(final String message);

    /**
     * @param disconnected the disconnected to set
     */
    void setDisconnected(boolean disconnected);

    /**
     * Set the grumpy message.
     *
     * @param message A grumpy message, or <code>null</code>.
     */
    void setGrumpyMessage(final String message);

    /**
     * Set whether this player is invisible to creatures.
     *
     * @param invisible <code>true</code> if invisible.
     */
    void setInvisible(final boolean invisible);

    /**
     * Set a keyed string value on a named slot.
     *
     * @param name The slot name.
     * @param key The value key.
     * @param value The value to assign (or remove if <code>null</code>).
     *
     * @return <code>true</code> if value changed, <code>false</code> if there
     * was a problem.
     */
    boolean setKeyedSlot(String name, String key, String value);

    /**
     * Sets the name of the last player who privately talked to this player
     * using the /tell command. It needs to be stored non-persistently so that
     * /answer can be used.
     *
     * @param lastPrivateChatterName
     */
    void setLastPrivateChatter(String lastPrivateChatterName);

    /**
     * Add a player ignore entry.
     *
     * @param name The player name.
     * @param duration The ignore duration (in minutes), or <code>0</code> for
     * infinite.
     * @param reply The reply.
     *
     * @return <code>true</code> if value changed, <code>false</code> if there
     * was a problem.
     */
    boolean addIgnore(String name, int duration, String reply);

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
     * @param name The player name.
     *
     * @return The custom reply message (including an empty string), or
     * <code>null</code> if not ignoring.
     */
    String getIgnore(String name);

    /**
     * Get a keyed string value on a named slot.
     *
     * @param name The slot name.
     * @param key The value key.
     *
     * @return The keyed value of the slot, or <code>null</code> if not set.
     */
    String getKeyedSlot(String name, String key);

    /**
     * Gets the name of the last player who privately talked to this player
     * using the /tell command, or null if nobody has talked to this player
     * since he logged in.
     *
     * @return
     */
    String getLastPrivateChatter();

    @Override
    String toString();
}
