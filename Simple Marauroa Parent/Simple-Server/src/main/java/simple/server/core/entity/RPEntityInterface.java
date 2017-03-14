package simple.server.core.entity;

import marauroa.common.game.RPEvent;
import simple.common.NotificationType;
import simple.server.core.engine.ISimpleRPZone;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RPEntityInterface {

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
     * @param disconnected the disconnected to set
     */
    void setDisconnected(boolean disconnected);

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
