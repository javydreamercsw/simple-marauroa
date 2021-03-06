package simple.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.Configuration;
import marauroa.common.crypto.Hash;
import marauroa.common.game.IRPZone;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import org.openide.util.Lookup;
import simple.server.core.action.ActionProvider;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.ISimpleRPZone;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.api.IRPEvent;
import simple.server.core.tool.Tool;
import simple.server.extension.MarauroaServerExtension;

/**
 * This provides a command line interface with the server.
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
class SimpleServerCLI extends Thread {

    private static final Logger LOG
            = Logger.getLogger(SimpleServerCLI.class.getSimpleName());
    private BufferedReader input;

    public SimpleServerCLI() {
        super("Simple Server CLI");
    }

    @Override
    public void run() {
        try {
            LOG.info("Starting Simple Server CLI...");
            input = new BufferedReader(new InputStreamReader(System.in));
            String line;
            line = input.readLine();
            while (line != null && !line.equals("")) {
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
                    LOG.info("Valid commands include: create, delete, quit, show, info.");
                    break;
                case "quit":
                    LOG.info("Are you sure you want to quit? (Y/N)");
                    try {
                        line = input.readLine();
                        OUTER:
                        while (!line.trim().toLowerCase().equals("")) {
                            switch (line) {
                                case "y":
                                    SimpleServer.server.finish();
                                    LOG.info("Server stopped!");
                                    System.exit(0);
                                    break;
                                case "n":
                                    break OUTER;
                                default:
                                    LOG.info("Invalid option. Are you sure you want"
                                            + " to quit? (Y/N)");
                                    line = input.readLine();
                                    break;
                            }
                        }
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                    break;
                case "create":
                    if (st.hasMoreTokens()) {
                        temp = st.nextToken();
                        switch (temp) {
                            case "account":
                                processCreateAccountCommand(st);
                                break;
                            case "zone":
                                processCreateZoneCommand(st);
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
                            case "zone":
                                processDeleteZoneCommand(st);
                                break;
                            default:
                                LOG.log(Level.WARNING, "Unknown parameter: {0}", temp);
                        }
                    } else {
                        LOG.warning("Missing parameters for delete command!\n"
                                + "Usage: create <option> <parameters>\n"
                                + "Options: account\n"
                                + "Parameters:\n"
                                + "-u <username>\n"
                                + "-p <password>\n"
                                + "-e <email>\n"
                                + "Options: zone\n"
                                + "Parameters:\n"
                                + "<zone name>");
                    }
                    break;
                case "show":
                    if (st.hasMoreTokens()) {
                        temp = st.nextToken();
                        StringBuilder sb = new StringBuilder();
                        switch (temp) {
                            case "entity":
                                Lookup.getDefault()
                                        .lookupAll(RPEntityInterface.class).forEach((e) -> {
                                    sb.append(e.getClass().getSimpleName()).append("\n");
                                });
                                LOG.info(sb.toString());
                                break;
                            case "extension":
                                Lookup.getDefault()
                                        .lookupAll(MarauroaServerExtension.class).forEach((e) -> {
                                    sb.append(e.getName()).append("\n");
                                });
                                LOG.info(sb.toString());
                                break;
                            case "action":
                                Lookup.getDefault()
                                        .lookupAll(ActionProvider.class).forEach((e) -> {
                                    sb.append(e.getClass().getSimpleName()).append("\n");
                                });
                                LOG.info(sb.toString());
                                break;
                            case "event":
                                Lookup.getDefault()
                                        .lookupAll(IRPEvent.class).forEach((e) -> {
                                    sb.append(e.getName()).append("\n");
                                });
                                LOG.info(sb.toString());
                                break;
                            case "zone":
                                boolean detail = st.hasMoreTokens();
                                IRPWorld world = Lookup.getDefault()
                                        .lookup(IRPWorld.class);
                                if (detail) {
                                    String z = "";
                                    while (st.hasMoreTokens()) {
                                        z += st.nextToken() + " ";
                                    }
                                    IRPZone zone = world.getZone(z.trim());
                                    if (zone == null) {
                                        LOG.log(Level.WARNING,
                                                "Unable to find zone: {0}", z);
                                    } else {
                                        if (zone instanceof ISimpleRPZone) {
                                            ISimpleRPZone sz = (ISimpleRPZone) zone;
                                            sb.append("Players--------------------"
                                                    + "---------------").append("\n");
                                            sz.getPlayers().forEach((p) -> {
                                                sb.append(p.getName()).append("\n");
                                            });
                                            sb.append("NPC------------------------"
                                                    + "-----------").append("\n");
                                            sz.getNPCS().forEach((npc) -> {
                                                sb.append(Tool.extractName(npc)).append("\n");
                                            });
                                        }
                                    }
                                } else {
                                    world.getZones().forEach((zone) -> {
                                        sb.append(zone.getID()).append("\n");
                                    });
                                }
                                LOG.info(sb.toString());
                                break;
                            default:
                                LOG.log(Level.WARNING, "Unknown parameter: {0}", temp);
                        }
                    } else {
                        LOG.warning("Missing parameters for show command!\n"
                                + "Usage: create <option> <parameters>\n"
                                + "Options: account\n"
                                + "Parameters:\n"
                                + "entity\n"
                                + "extension\n"
                                + "action\n"
                                + "event\n"
                                + "zone\n");
                    }
                    break;
                case "info":
                    //Display server information
                    try {
                        Configuration conf = Configuration.getConfiguration();
                        Properties p = conf.getAsProperties();
                        StringBuilder sb = new StringBuilder();
                        p.entrySet().stream().filter((entry)
                                -> (!entry.getKey().equals("e")
                                && !entry.getKey().equals("d")
                                && !entry.getKey().equals("n")
                                && !entry.getKey().toString().startsWith("jdbc")
                                && !entry.getKey().toString()
                                        .contains("password")))
                                .forEachOrdered((entry) -> {
                                    sb.append(entry.getKey()).append(": ")
                                            .append(entry.getValue()).append('\n');
                                }); //Make sure not to disclose security info.
                        LOG.log(Level.INFO, sb.toString());
                    } catch (IOException ex) {
                        LOG.log(Level.SEVERE, null, ex);
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

    private void processDeleteZoneCommand(StringTokenizer st) {
        if (st.hasMoreTokens()) {
            String zone = "";
            while (st.hasMoreTokens()) {
                zone += st.nextToken() + " ";
            }
            IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);
            if (world.hasRPZone(zone.trim())) {
                try {
                    world.removeRPZone(zone.trim());
                    LOG.log(Level.INFO, "Zone: {0} succesfully deleted!", zone);
                } catch (Exception ex) {
                    LOG.log(Level.SEVERE, "Unable to delete zone!", ex);
                }
            } else {
                LOG.log(Level.INFO, "Unable to find zone: {0}", zone);
            }
        }
    }

    private void processCreateZoneCommand(StringTokenizer st) {
        if (st.hasMoreTokens()) {
            String zone = "";
            while (st.hasMoreTokens()) {
                zone += st.nextToken() + " ";
            }
            IRPWorld world = Lookup.getDefault().lookup(IRPWorld.class);
            if (world.hasRPZone(zone.trim())) {
                LOG.log(Level.INFO, "Zone: {0} already exists!", zone);
            } else {
                world.addRPZone(zone.trim());
                LOG.log(Level.INFO, "Zone: {0} succesfully created!", zone);
            }
        }
    }
}
