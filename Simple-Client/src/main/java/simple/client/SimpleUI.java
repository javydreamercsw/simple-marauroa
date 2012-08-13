package simple.client;

import java.awt.Component;
import simple.common.NotificationType;

/**
 * A base class for the Simple client UI (not GUI).
 *
 * This should have minimal UI-implementation dependent code. That's what
 * sub-classes are for!
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class SimpleUI {

    /**
     * A shared [singleton] copy.
     */
    protected static SimpleUI sharedUI;

    /**
     * Create a Simple UI.
     *
     */
    public SimpleUI() {
    }

    //
    // SimpleUI
    //
    /**
     * Add an event line.
     *
     * @param text Text to be added
     */
    public abstract void addEventLine(EventLine text);

    /**
     * Add an event line.
     *
     * @param header Line header
     * @param text Line text
     */
    public abstract void addEventLine(String header, String text);

    /**
     * Add an event line.
     *
     * @param text Text
     * @param type Notification type
     */
    public abstract void addEventLine(String text, NotificationType type);

    /**
     * Add an event line.
     *
     * @param header
     * @param text
     * @param type
     */
    public abstract void addEventLine(String header, String text, NotificationType type);

    /**
     * Adds a Swing component to the view.
     * @param dlg
     */
    public abstract void addDialog(Component dlg);

    /**
     * Initiate outfit selection by the user.
     */
    public abstract void chooseOutfit();

    /**
     * Like chooseOutfit(), but for Guilds.
     */
    public abstract void manageGuilds();

    /**
     * Get the default UI.
     *
     *
     * @return 
     */
    public static SimpleUI get() {
        return sharedUI;
    }

    /**
     * Get the current game screen height.
     *
     * @return The height.
     */
    public abstract int getHeight();

    /**
     * Get the current game screen width.
     *
     * @return The width.
     */
    public abstract int getWidth();

    /**
     * Request quit confirmation from the user.
     */
    public abstract void requestQuit();

    /**
     * Set the shared [singleton] value.
     *
     * @param sharedUI
     *            The Jwrestling UI.
     */
    public static void setDefault(SimpleUI sharedUI) {
        SimpleUI.sharedUI = sharedUI;
    }

    /**
     * Set the offline indication state.
     *
     * @param offline
     *            <code>true</code> if offline.
     */
    public abstract void setOffline(boolean offline);

    /**
     * Handles the shutdown of the UI.
     */
    public abstract void shutdown();

    /**
     * Handles the game main loop.
     * The stuff that happens over and over on the client,
     * i.e. the game itself
     */
    public abstract void gameLoop();
}