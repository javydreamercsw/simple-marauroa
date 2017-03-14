
package simple.client;

import javax.swing.JList;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import simple.common.NotificationType;

/**
 * This is the interface that dictates what the client should have but separated
 * from the implementation. The client can be a proper UI or text based.
 * It's up to you!
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ChatScreenInterface {

    /**
     *
     * @param b
     */
    public void setEnabled(boolean b);

    /**
     * Hide or show the Screen
     * @param visible
     */
    void setVisible(boolean visible);
    /**
     * Add a player to the UI
     * @param p
     */
    void addPlayer(String p);

    /**
     * Add a room (Zone) to the UI
     * @param room
     */
    void addRoom(String room);

    /**
     * Start the game's loop
     */
    void gameLoop();

    /**
     * A list of rooms in the UI
     * @return JList
     */
    JList getRoomList();

    /**
     * @return the gameRunning
     */
    boolean isGameRunning();

    /**
     * Refresh the UI
     * @param delta Delay for the refresh to occur
     */
    void refresh(int delta);

    /**
     * Remove a player from the UI
     * @param p
     */
    void removePlayer(String p);

    /**
     * Remove a room from the UI
     * @param room
     */
    void removeRoom(String room);

    /**
     * Request a password to the user.
     * The RPEvent should contain all the info needed to reprocess the request
     * adding the provided password
     * @param a
     */
    void requestPassword(RPAction a);

    /**
     * Request quiting the game
     */
    void requestQuit();

    /**
     * @param gameRunning the gameRunning to set
     */
    void setGameRunning(boolean gameRunning);

    /**
     * Set the room list. Usualy done when the player logs in.
     * @param rooms
     */
    void setRooms(String rooms);

    /**
     * Sets the offline indication state.
     *
     * @param offline
     * <code>true</code> if offline.
     */
    void setOffline(boolean offline);

    /**
     * Update the UI
     * @param object Object to be used to update
     */
    void update(RPObject object);

    /**
     * Update a room's description w/o changing the password
     * @param room
     * @param desc
     */
    void updateRoom(String room, String desc);

    /**
     * A zone was added
     * @param zone
     */
    void onZoneAdded(String zone);

    /**
     * A zone was removed
     * @param zone
     */
    void onZoneRemoved(String zone);

    /**
     * A zone was updated
     * @param zone Zone updated
     * @param desc new description
     */
    void onZoneUpdated(String zone, String desc);

    /**
     * Received a list of zones
     * @param list
     */
    void onZoneList(String list);

    /**
     * You need a password to enter this room
     * @param event
     */
    void onNeedRoomPassword(RPEvent event);

    /**
     * Add the player's name to the Frame title
     * @param name
     */
    void addPlayerNameToFrameTitle(String name);
    
    /**
     * The implemented method.
     *
     * @param header
     *            a string with the header name
     * @param line
     *            a string representing the line to be printed
     * @param type
     *            The logical format type.
     */
    public void addLine(String header, String line,
            NotificationType type);
}
