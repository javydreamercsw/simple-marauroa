package simple.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.crypto.Hash;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;

/**
 * This provides a command line interface with the server.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
class SimpleServerCLI extends Thread {

    private static final Logger LOG
            = Logger.getLogger(SimpleServerCLI.class.getSimpleName());

    public SimpleServerCLI() {
        super("Simple Server CLI");
    }

    @Override
    public void run() {
        try {
            LOG.info("Starting Simple Server CLI...");
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(System.in));
            String line;
            line = input.readLine();
            while (!line.equals("")) {
                processInput(line.trim());
                line = input.readLine();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    private void processInput(String line) {
        StringTokenizer st = new StringTokenizer(line, " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            String temp;//Used to hold temporary values
            switch (token) {
                case "help":
                    break;
                case "quit":
                    break;
                case "create":
                    if (st.hasMoreTokens()) {
                        temp = st.nextToken();
                        switch (temp) {
                            case "account":
                                processCreateAccountCommand(st);
                                break;
                            default:
                                LOG.log(Level.WARNING, "Unknown parameter: {0}", temp);
                        }
                    } else {
                        LOG.warning("Missing parameters for create command!\n"
                                + "Usage: create <option> <parameters>\n"
                                + "Options: account\n"
                                + "Parameters:\n"
                                + "-u <username>\n"
                                + "-p <password>\n"
                                + "-e <email>");
                    }
                    break;
                case "delete":
                    if (st.hasMoreTokens()) {
                        temp = st.nextToken();
                        switch (temp) {
                            case "account":
                                processDeleteAccountCommand(st);
                                break;
                            default:
                                LOG.log(Level.WARNING, "Unknown parameter: {0}", temp);
                        }
                    } else {
                        LOG.warning("Missing parameters for create command!\n"
                                + "Usage: create <option> <parameters>\n"
                                + "Options: account\n"
                                + "Parameters:\n"
                                + "-u <username>\n"
                                + "-p <password>\n"
                                + "-e <email>");
                    }
                    break;
                default:
                    LOG.log(Level.WARNING, "Unknown command: {0}", line);
            }
        }
    }

    private void processCreateAccountCommand(StringTokenizer st) {
        String temp;
        //We need username, password and email
        String username = "",
                password = "",
                email = "";
        while (st.hasMoreTokens()) {
            temp = st.nextToken();
            switch (temp) {
                case "-u"://username
                    username = st.nextToken();
                    break;
                case "-p"://password
                    password = st.nextToken();
                    break;
                case "-e"://email
                    email = st.nextToken();
                    break;
                default:
                    LOG.log(Level.WARNING,
                            "Unknown parameter: {0}", temp);
            }
        }
        if (username.trim().isEmpty()
                || password.trim().isEmpty()
                || email.trim().isEmpty()) {
            LOG.log(Level.WARNING,
                    "Missing parameters!",
                    username);
        } else {
            try {
                //Try to create account
                AccountDAO dao = DAORegister.get().get(AccountDAO.class);
                if (dao.hasPlayer(username)) {
                    LOG.log(Level.WARNING,
                            "Account: {0} already exists!",
                            username);
                } else {
                    dao.addPlayer(username,
                            Hash.hash(password), email);
                    LOG.log(Level.INFO,
                            "Account: {0} succesfully created!",
                            username);
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE,
                        "Error creating account: "
                        + username, ex);
            }
        }
    }

    private void processDeleteAccountCommand(StringTokenizer st) {
        String temp;
        //We need username, password and email
        String username = "";
        while (st.hasMoreTokens()) {
            temp = st.nextToken();
            switch (temp) {
                case "-u"://username
                    username = st.nextToken();
                    break;
                default:
                    LOG.log(Level.WARNING,
                            "Unknown parameter: {0}", temp);
            }
        }
        if (username.trim().isEmpty()) {
            LOG.log(Level.WARNING,
                    "Missing parameters!",
                    username);
        } else {
            try {
                //Try to create account
                AccountDAO dao = DAORegister.get().get(AccountDAO.class);
                if (dao.hasPlayer(username)) {
                    dao.removePlayer(username);
                    LOG.log(Level.INFO,
                            "Account: {0} succesfully removed!",
                            username);
                } else {
                    LOG.log(Level.WARNING,
                            "Account: {0} doesn't exist!",
                            username);
                }
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE,
                        "Error deleting account: "
                        + username, ex);
            }
        }
    }
}
