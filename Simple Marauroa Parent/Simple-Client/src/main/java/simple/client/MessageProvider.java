/*
 * This interface allows to display messages to the user.
 */
package simple.client;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface MessageProvider {

    /**
     * Display a warning message.
     *
     * @param title
     * @param message
     */
    void displayWarning(String title, String message);
    
    /**
     * Display an error message.
     *
     * @param title
     * @param message
     */
    void displayError(String title, String message);
    
    /**
     * Display an informational message.
     *
     * @param title
     * @param message
     */
    void displayInfo(String title, String message);
}
