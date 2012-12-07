package simple.client;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.client.net.IPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;
import simple.server.core.event.TextEvent;

public class TextClient extends Thread {

    private String host;
    private String username;
    private String password;
    private String character;
    private String port, gameName, version;
    private static boolean showWorld = false, chat = false;
    private Map<RPObject.ID, RPObject> world_objects;
    private marauroa.client.ClientFramework clientManager;
    private PerceptionHandler handler;
    private static final Logger logger = Logger.getLogger(TextClient.class.getSimpleName());

    public TextClient(String h, String u, String p, String c, String P,
            boolean t, String name, String v) throws SocketException {
        host = h;
        username = u;
        password = p;
        character = c;
        port = P;
        version = v;
        gameName = name;

        world_objects = new HashMap<RPObject.ID, RPObject>();

        handler = new PerceptionHandler(new IPerceptionListener() {
            @Override
            public boolean onAdded(RPObject object) {
                return false;
            }

            @Override
            public boolean onClear() {
                return false;
            }

            @Override
            public boolean onDeleted(RPObject object) {
                return false;
            }

            @Override
            public void onException(Exception exception,
                    MessageS2CPerception perception) {
                logger.log(Level.SEVERE, port, exception);
            }

            @Override
            public boolean onModifiedAdded(RPObject object, RPObject changes) {
                return false;
            }

            @Override
            public boolean onModifiedDeleted(RPObject object, RPObject changes) {
                return false;
            }

            @Override
            public boolean onMyRPObject(RPObject added, RPObject deleted) {
                RPObject.ID id = null;
                if (added != null) {
                    id = added.getID();
                }
                if (deleted != null) {
                    id = deleted.getID();
                }
                if (id == null) {
                    // Unchanged.
                    return true;
                }
                RPObject object = world_objects.get(id);
                if (object != null) {
                    //Get the list zones event results
                    for (RPEvent event : object.events()) {
                        try {
                            logger.log(Level.INFO, "Processing: {0}, {1}",
                                    new Object[]{event, event.getName()});
                            if (event.getName().equals(TextEvent.RPCLASS_NAME)) {
                                logger.log(Level.INFO, "<{0}>{1}",
                                        new Object[]{event.get("from"), event.get("text")});
                            } else {
                                logger.log(Level.WARNING, "Received the following event but didn\'t "
                                        + "know how to handle it: {0}", new Object[]{event});
                            }
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, null, e);
                            break;
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPerceptionBegin(byte type, int timestamp) {
            }

            @Override
            public void onPerceptionEnd(byte type, int timestamp) {
            }

            @Override
            public void onSynced() {
            }

            @Override
            public void onUnsynced() {
            }
        });
        createClientManager(gameName != null ? gameName : "Simple",
                version != null ? version : "0.02.04");
    }

    private void createClientManager(String name, String gversion) {
        gameName = name;
        version = gversion;
        clientManager = new marauroa.client.ClientFramework(
                "log4j.properties") {
            @Override
            protected String getGameName() {
                return gameName;
            }

            @Override
            protected String getVersionNumber() {
                return version;
            }

            @Override
            protected void onPerception(MessageS2CPerception message) {
                try {
                    System.out.println("Received perception " + message.getPerceptionTimestamp());

                    handler.apply(message, world_objects);
                    int i = message.getPerceptionTimestamp();

                    if (chat) {
                        RPAction action = new RPAction();
                        action.put("type", "chat");
                        action.put("text", "Hi!");
                        clientManager.send(action);
                        if (i % 50 == 0) {
                            action.put("type", "chat");
                            action.put("text", "Hi!");
                            clientManager.send(action);
                        } else if (i % 50 == 20) {
                            action.put("type", "chat");
                            action.put("text", "How are you?");
                            clientManager.send(action);
                        }
                    }
                    if (showWorld) {
                        System.out.println("<World contents ------------------------------------->");
                        int j = 0;
                        for (RPObject object : world_objects.values()) {
                            j++;
                            System.out.println(j + ". " + object);
                        }
                        System.out.println("</World contents ------------------------------------->");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected List<TransferContent> onTransferREQ(
                    List<TransferContent> items) {
                for (TransferContent item : items) {
                    item.ack = true;
                }
                return items;
            }

            @Override
            protected void onTransfer(List<TransferContent> items) {
                System.out.println("Transfering ----");
                for (TransferContent item : items) {
                    System.out.println(item);
                }
            }

            @Override
            protected void onAvailableCharacters(String[] characters) {
                //See onAvailableCharacterDetails
            }

            @Override
            protected void onAvailableCharacterDetails(Map<String, RPObject> characters) {

                // if there are no characters, create one with the specified name automatically
                if (characters.isEmpty()) {
                    System.out.println("The requested character is not available, trying to create character " + character);
                    final RPObject template = new RPObject();
                    try {
                        final CharacterResult result = createCharacter(character, template);
                        if (result.getResult().failed()) {
                            System.out.println(result.getResult().getText());
                        }
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // autologin if a valid character was specified.
                if ((character != null) && (characters.keySet().contains(character))) {
                    try {
                        chooseCharacter(character);
                    } catch (final Exception e) {
                        System.out.println("TextClient::onAvailableCharacters" + e);
                    }
                    return;
                }
            }

            @Override
            protected void onServerInfo(String[] info) {
                System.out.println("Server info");
                for (String info_string : info) {
                    System.out.println(info_string);
                }
            }

            @Override
            protected void onPreviousLogins(List<String> previousLogins) {
                System.out.println("Previous logins");
                for (String info_string : previousLogins) {
                    System.out.println(info_string);
                }
            }
        };
    }

    @Override
    public void run() {
        try {
            clientManager.connect(host, Integer.parseInt(port));
            System.out.println("Logging as: " + username + " with pass: " + password + " version: '" + version + "'");
            clientManager.login(username, password);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (LoginFailedException e) {
            try {
                System.out.println("Creating account and logging in to continue....");
                clientManager.createAccount(username, password, host);
                System.out.println("Logging as: " + username + " with pass: " + password + " version: '" + version + "'");
                clientManager.login(username, password);
            } catch (LoginFailedException ex) {
                logger.log(Level.SEVERE, null, ex);
                System.exit(1);
            } catch (TimeoutException ex) {
                logger.log(Level.SEVERE, null, ex);
                System.exit(1);
            } catch (InvalidVersionException ex) {
                logger.log(Level.SEVERE, null, ex);
                System.exit(1);
            } catch (BannedAddressException ex) {
                logger.log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        } catch (InvalidVersionException ex) {
            logger.log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (TimeoutException ex) {
            logger.log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (BannedAddressException ex) {
            logger.log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        boolean cond = true;

        while (cond) {
            clientManager.loop(0);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, null, e);
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
                            showWorld = true;
                        }
                    } else if (args[i].equals("-chat")) {
                        if ("1".equals(args[i + 1])) {
                            chat = true;
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

                if ((username != null) && (password != null) && (character != null) && (host != null) && (port != null)) {
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
            System.out.println("* -v\tGame Version (Default is '0.02.04')");
            System.out.println("* -chat\tEnable/Disable chat? 0 or 1");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
