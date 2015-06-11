
package simple.client.entity;

import simple.common.Grammar;
import simple.common.NotificationType;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;
import simple.client.SimpleUI;

/**
 * Represents the user
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public class User extends ClientRPEntity {

    private static User instance;
    private String serverVersion = null;

    /**
     * Is null?
     * @return
     */
    public static boolean isNull() {
        return instance == null;
    }

    /**
     * Singleton pattern
     * @return
     */
    public static User get() {
        return instance;
    }

    /**
     * Constructor
     */
    public User() {
        instance = this;
        modificationCount = 0;
    }
    private int modificationCount;

    /**
     * Returns the modificationCount. This counter is increased each time a
     * perception is received from the server (so all serverside changes
     * increases the mod-count). This counter's purpose is to be sure that this
     * entity is modified or not (ie for gui elements).
     * @return a number representing the amount of changes.
     */
    public long getModificationCount() {
        return modificationCount;
    }

    protected void onAway(final String message) {

        SimpleUI.get().addEventLine(
                (message != null) ? "You have been marked as being away."
                : "You are no longer marked as being away.",
                NotificationType.INFORMATION);
    }

    /**
     * Is administrator
     * @return
     */
    public static boolean isAdmin() {
        if (isNull()) {
            return false;
        }

        User me = User.get();
        if (me.rpObject == null) {
            return false;
        }

        return me.rpObject.has("adminlevel") && (me.rpObject.getInt("adminlevel") >= 600);
    }

    /**
     * Get player level
     * @return
     */
    public static int getPlayerLevel() {
        if (!isNull()) {
            User me = User.get();

            if (me.rpObject != null) {
                return me.getLevel();
            }
        }

        return 0;
    }

    /**
     * Get object ID
     * @return
     */
    public int getObjectID() {
        return rpObject.getID().getObjectID();
    }

    /**
     *
     * @param amount
     */
    @Override
    public void onHealed(final int amount) {
        super.onHealed(amount);

        SimpleUI.get().addEventLine(
                getTitle() + " heals " + Grammar.quantityplnoun(amount, "health point") + ".",
                NotificationType.POSITIVE);
    }

    /**
     * The absolute world area (coordinates) where the player can possibly hear.
     * sounds
     *
     * @return Rectangle2D area
     */
    public Rectangle2D getHearingArea() {
        final double HEARING_RANGE = 20;
        double width = HEARING_RANGE * 2;
        return new Rectangle2D.Double(getX() - HEARING_RANGE, getY() - HEARING_RANGE, width, width);
    }

    /**
     * Initialize this entity for an object.
     *
     * @param object
     *            The object.
     *
     * @see #release()
     */
    @Override
    public void initialize(final RPObject object) {
        super.initialize(object);
    }

    /**
     * The object added/changed attribute(s).
     *
     * @param object
     *            The base object.
     * @param changes
     *            The changes.
     */
    @Override
    public void onChangedAdded(final RPObject object, final RPObject changes) {
        super.onChangedAdded(object, changes);
        modificationCount++;

        // The first time we ignore it.
        if (object != null) {
            if (changes.has("online")) {
                String[] players = changes.get("online").split(",");
                for (String playerName : players) {
                    SimpleUI.get().addEventLine(
                            playerName + " has joined Jwrestling.",
                            NotificationType.INFORMATION);
                }
            }

            if (changes.has("offline")) {
                String[] players = changes.get("offline").split(",");
                for (String playername : players) {
                    SimpleUI.get().addEventLine(
                            playername + " has left Jwrestling.",
                            NotificationType.INFORMATION);
                }
            }

            if (changes.has("release")) {
                serverVersion = changes.get("release");
            }
        }
    }

    @Override
    public void onChangedRemoved(final RPObject base, final RPObject diff) {
        modificationCount++;
        super.onChangedRemoved(base, diff);
    }

    /**
     * Returns true when the entity was modified since the
     * <i>oldModificationCount</i>.
     *
     * @param oldModificationCount
     *            the old modificationCount
     * @return true when the entity was modified, false otherwise
     * @see #getModificationCount()
     */
    public boolean isModified(final long oldModificationCount) {
        return oldModificationCount != modificationCount;
    }

    /**
     * Resets the class to uninitialized.
     */
    public static void setNull() {
        instance = null;
    }

    /**
     * Query the version of the server we are currently connected to.
     *
     * @return server version string
     */
    public String getServerVersion() {
        return serverVersion;
    }

    /**
     * Returns the objectid for the named item.
     *
     * @param slotName
     *            name of slot to search
     * @param itemName
     *            name of item
     * @return objectid or <code>-1</code> in case there is no such item
     */
    public int findItem(String slotName, String itemName) {
        for (RPObject item : getSlot(slotName)) {
            if (item.get("name").equals(itemName)) {
                int itemID = item.getID().getObjectID();

                return itemID;
            }
        }

        return -1;
    }
}
