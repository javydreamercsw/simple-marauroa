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
}
