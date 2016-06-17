package simple.server.core.account;

import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.Result;

/**
 * Checks that the password is not closly related to the username.
 *
 * @author timothyb89
 */
public class PasswordDiffersFromUsernameValidator implements
        AccountParameterValidator {

    private static Logger LOG
            = Logger.getLogger(PasswordDiffersFromUsernameValidator.class.getSimpleName());
    private String username;
    private String password;

    /**
     * Creates a new PasswordDiffersFromUsernameValidator validator.
     *
     * @param username name of user
     * @param password password
     */
    public PasswordDiffersFromUsernameValidator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Result validate() {
        // check for username
        boolean hasUsername = false;
        if (password.contains(username)) {
            hasUsername = true;
        }

        if (!hasUsername) {
            // now we'll do some more checks to see if the password
            // contains more than three letters of the username
            LOG.fine("Checking is password contains a derivitive of the "
                    + "username, trimming from the back...");
            int min_user_length = 3;
            for (int i = 1; i < username.length(); i++) {
                String subuser = username.substring(0, username.length() - i);
                LOG.log(Level.FINE, "\tchecking for \"{0}\"...", subuser);
                if (subuser.length() <= min_user_length) {
                    break;
                }

                if (password.contains(subuser)) {
                    hasUsername = true;
                    LOG.fine("Password contians username!");
                    break;
                }
            }

            if (!hasUsername) {
                // now from the end of the password..
                LOG.fine("Checking is password contains a derivitive of "
                        + "the username, trimming from the front...");
                for (int i = 0; i < username.length(); i++) {
                    String subuser = username.substring(i);
                    LOG.log(Level.FINE, "\tchecking for \"{0}\"...", subuser);
                    if (subuser.length() <= min_user_length) {
                        break;
                    }
                    if (password.contains(subuser)) {
                        hasUsername = true;
                        LOG.fine("Password contains username!");
                        break;
                    }
                }
            }
        }

        if (hasUsername) {
            return Result.FAILED_PASSWORD_TOO_CLOSE_TO_USERNAME;
        }

        return null;
    }
}
