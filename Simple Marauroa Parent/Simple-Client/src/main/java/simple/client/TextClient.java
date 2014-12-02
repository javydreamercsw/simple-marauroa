package simple.client;

import java.io.IOException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.common.net.InvalidVersionException;

public class TextClient extends AbstractClient {

    private static final Logger LOG
            = Logger.getLogger(TextClient.class.getSimpleName());

    public TextClient(String h, String u, String p, String c, String P,
            boolean t, String name, String v) throws SocketException {
        setHost(h);
        setUsername(u);
        setPassword(p);
        setCharacter(c);
        setPort(P);
        setVersion(v);
        setGameName(name);
        createClientManager(getGameName() != null ? getGameName() : "Simple",
                getVersion() != null ? getVersion() : "0.02.06");
    }

    @Override
    public void run() {
        try {
            getClientManager().connect(getHost(), Integer.parseInt(getPort()));
            System.out.println("Logging as: " + getUsername() + " with pass: "
                    + getPassword() + " version: '" + getVersion() + "'");
            getClientManager().login(getUsername(), getPassword());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (LoginFailedException e) {
            try {
                System.out.println("Creating account and logging in to continue....");
                getClientManager().createAccount(getUsername(), getPassword(),
                        getHost());
                System.out.println("Logging as: " + getUsername()
                        + " with pass: " + getPassword() + " version: '" 
                        + getVersion() + "'");
                getClientManager().login(getUsername(), getPassword());
            } catch (LoginFailedException ex) {
                LOG.log(Level.SEVERE, null, ex);
                System.exit(1);
            } catch (TimeoutException ex) {
                LOG.log(Level.SEVERE, null, ex);
                System.exit(1);
            } catch (InvalidVersionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                System.exit(1);
            } catch (BannedAddressException ex) {
                LOG.log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        } catch (InvalidVersionException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (TimeoutException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (BannedAddressException ex) {
            LOG.log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        boolean cond = true;

        while (cond) {
            getClientManager().loop(0);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                LOG.log(Level.SEVERE, null, e);
                cond = false;
            }
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                int i = 0;
                String username = null;
                String password = null;
                String character = null;
                String host = null;
                String port = null;
                String name = null;
                String version = null;
                boolean tcp = false;

                while (i != args.length) {
                    if (args[i].equals("-u")) {
                        username = args[i + 1];
                    } else if (args[i].equals("-p")) {
                        password = args[i + 1];
                    } else if (args[i].equals("-c")) {
                        character = args[i + 1];
                    } else if (args[i].equals("-h")) {
                        host = args[i + 1];
                    } else if (args[i].equals("-P")) {
                        port = args[i + 1];
                    } else if (args[i].equals("-W")) {
                        if ("1".equals(args[i + 1])) {
                            setShowWorld(true);
                        }
                    } else if (args[i].equals("-chat")) {
                        if ("1".equals(args[i + 1])) {
                            setChat(true);
                        }
                    } else if (args[i].equals("-t")) {
                        tcp = true;
                    } else if (args[i].equals("-n")) {
                        name = args[i + 1];
                    } else if (args[i].equals("-v")) {
                        version = args[i + 1];
                    }
                    i++;
                }

                if ((username != null) && (password != null)
                        && (character != null) && (host != null)
                        && (port != null)) {
                    System.out.println("Parameter operation");
                    new TextClient(host, username, password, character, port,
                            tcp, name, version).start();
                    return;
                }
            }

            System.out.println("Marauroa Simple textClient\n");
            System.out.println("  simple.client.textClient -u username -p pass -h host -P port -c character\n");
            System.out.println("Required parameters");
            System.out.println("* -h\tHost that is running Marauroa server");
            System.out.println("* -P\tPort on which Marauroa server is running");
            System.out.println("* -u\tUsername to log into Marauroa server");
            System.out.println("* -p\tPassword to log into Marauroa server");
            System.out.println("* -c\tCharacter used to log into Marauroa server");
            System.out.println("Optional parameters");
            System.out.println("* -W\tShow world content? 0 or 1");
            System.out.println("* -n\tGame name (Default is 'Simple')");
            System.out.println("* -v\tGame Version (Default is '0.02.06')");
            System.out.println("* -chat\tEnable/Disable chat? 0 or 1");
        } catch (SocketException e) {
            LOG.log(Level.SEVERE, null, e);
            System.exit(1);
        }
    }
}
