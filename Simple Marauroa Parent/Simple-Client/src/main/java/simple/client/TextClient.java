package simple.client;

import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TextClient extends DefaultClient {

    private static final Logger LOG
            = Logger.getLogger(TextClient.class.getSimpleName());
    private static boolean chat = false, world = false;

    public TextClient(String h, String u, String p, String c, String P,
            boolean t, String name, String v) throws SocketException {
        setShowWorld(world);
        setChat(chat);
        setCreateDefaultCharacter(true);
        connect(h, u, p, c, P, name, v);
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
                    switch (args[i]) {
                        case "-u":
                            username = args[i + 1];
                            break;
                        case "-p":
                            password = args[i + 1];
                            break;
                        case "-c":
                            character = args[i + 1];
                            break;
                        case "-h":
                            host = args[i + 1];
                            break;
                        case "-P":
                            port = args[i + 1];
                            break;
                        case "-W":
                            if ("1".equals(args[i + 1])) {
                                world = true;
                            }
                            break;
                        case "-chat":
                            if ("1".equals(args[i + 1])) {
                                chat = true;
                            }
                            break;
                        case "-t":
                            tcp = true;
                            break;
                        case "-n":
                            name = args[i + 1];
                            break;
                        case "-v":
                            version = args[i + 1];
                            break;
                    }
                    i++;
                }

                if ((username != null) && (password != null)
                        && (character != null) && (host != null)
                        && (port != null)) {
                    System.out.println("Parameter operation");
                    new Thread(new TextClient(host, username, password, character, port,
                            tcp, name, version)).start();
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
