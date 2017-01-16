package simple.client;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public interface LoginProvider {

    /**
     * Display the login dialog.
     */
    void displayLoginDialog();

    /**
     * Prompt user to provide email.
     */
    void getEmailFromUser();

    /**
     * Check if the user is already logged in.
     *
     * @return true if logged in, false otherwise.
     */
    boolean isAuthenticated();

    /**
     * Set the value of the authentication flag.
     *
     * @param authenticated new value
     */
    void setAuthenticated(boolean authenticated);
}
