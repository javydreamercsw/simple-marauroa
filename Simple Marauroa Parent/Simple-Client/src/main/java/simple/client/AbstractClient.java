package simple.client;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.SocketException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.client.BannedAddressException;
import marauroa.client.ClientFramework;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.client.net.IPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;
import org.openide.util.Lookup;
import simple.client.api.AddListener;
import simple.client.api.ClearListener;
import simple.client.api.DeleteListener;
import simple.client.api.ExceptionListener;
import simple.client.api.IWorldManager;
import simple.client.api.ModificationListener;
import simple.client.api.PerceptionListener;
import simple.client.api.SelfChangeListener;
import simple.client.api.SyncListener;
import simple.server.core.entity.clientobject.ClientObject;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractClient implements ClientFrameworkProvider {

    private String port;
    private String gameName;
    private String version;
    private marauroa.client.ClientFramework clientManager;
    private PerceptionHandler handler;
    private static boolean showWorld = true, chat = true;
    private String character;
    private String host;
    private String username;
    private String password;

    private static final Logger LOG
            = Logger.getLogger(AbstractClient.class.getSimpleName());

    /**
     * @return the showWorld
     */
    public static boolean isShowWorld() {
        return showWorld;
    }

    /**
     * @param aShowWorld the showWorld to set
     */
    public static void setShowWorld(boolean aShowWorld) {
        showWorld = aShowWorld;
    }

    /**
     * @return the chat
     */
    public static boolean isChat() {
        return chat;
    }

    /**
     * @param aChat the chat to set
     */
    public static void setChat(boolean aChat) {
        chat = aChat;
    }

    public AbstractClient() {
        handler = new PerceptionHandler(new IPerceptionListener() {
            @Override
            public boolean onAdded(RPObject object) {
                boolean result = false;
                LOG.log(Level.FINE, "onAdded: {0}", object);
                for (AddListener listener
                        : Lookup.getDefault().lookupAll(AddListener.class)) {
                    if (!listener.onAdded(object)) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean onClear() {
                LOG.fine("onClear");
                boolean result = false;
                for (ClearListener listener
                        : Lookup.getDefault().lookupAll(ClearListener.class)) {
                    if (!listener.onClear()) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean onDeleted(RPObject object) {
                LOG.log(Level.FINE, "onDeleted: {0}", object);
                boolean result = false;
                for (DeleteListener listener
                        : Lookup.getDefault().lookupAll(DeleteListener.class)) {
                    if (!listener.onDeleted(object)) {
                        result = true;
                    }
                }
                Lookup.getDefault().lookup(IWorldManager.class).getWorld()
                        .remove(object.getID());
                return result;
            }

            @Override
            public void onException(Exception exception,
                    MessageS2CPerception perception) {
                for (ExceptionListener listener
                        : Lookup.getDefault().lookupAll(ExceptionListener.class)) {
                    listener.onException(exception, perception);
                }
                LOG.log(Level.SEVERE, getPort(), exception);
            }

            @Override
            public boolean onModifiedAdded(RPObject object, RPObject changes) {
                LOG.log(Level.FINE, "onModifiedAdded: {0}, {1}",
                        new Object[]{object, changes});
                boolean result = false;
                for (ModificationListener listener
                        : Lookup.getDefault().lookupAll(ModificationListener.class)) {
                    if (!listener.onModifiedAdded(object, changes)) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean onModifiedDeleted(RPObject object, RPObject changes) {
                LOG.log(Level.FINE, "onModifiedDeleted: {0}, {1}",
                        new Object[]{object, changes});
                boolean result = false;
                for (ModificationListener listener
                        : Lookup.getDefault().lookupAll(ModificationListener.class)) {
                    if (!listener.onModifiedDeleted(object, changes)) {
                        result = true;
                    }
                }
                return result;
            }

            @Override
            public boolean onMyRPObject(RPObject added, RPObject deleted) {
                boolean result = true;
                RPObject.ID id = null;
                if (added != null) {
                    id = added.getID();
                }
                if (deleted != null) {
                    id = deleted.getID();
                }
                if (id != null) {
                    RPObject object = Lookup.getDefault().lookup(IWorldManager.class).get(id);
                    if (object != null) {
                        Collection<? extends SelfChangeListener> listeners
                                = Lookup.getDefault().lookupAll(SelfChangeListener.class);
                        for (SelfChangeListener listener : listeners) {
                            if (!listener.onMyRPObject(added, deleted)) {
                                result = false;
                                break;
                            }
                        }
                    }
                } else {
                    // Unchanged.
                    // Do nothing.
                }
                return result;
            }

            @Override
            public void onPerceptionBegin(byte type, int timestamp) {
                for (PerceptionListener listener
                        : Lookup.getDefault().lookupAll(PerceptionListener.class)) {
                    listener.onPerceptionBegin(type, timestamp);
                }
            }

            @Override
            public void onPerceptionEnd(byte type, int timestamp) {
                for (PerceptionListener listener
                        : Lookup.getDefault().lookupAll(PerceptionListener.class)) {
                    listener.onPerceptionEnd(type, timestamp);
                }
            }

            @Override
            public void onSynced() {
                for (SyncListener listener
                        : Lookup.getDefault().lookupAll(SyncListener.class)) {
                    listener.onSynced();
                }
            }

            @Override
            public void onUnsynced() {
                for (SyncListener listener
                        : Lookup.getDefault().lookupAll(SyncListener.class)) {
                    listener.onUnsynced();
                }
            }
        });
        if (clientManager == null) {
            createClientManager(gameName != null ? gameName : "jWrestling",
                    version != null ? version : "0.09");
        }
    }

    protected void createClientManager(String name, String gversion) {
        setGameName(name);
        setVersion(gversion);
        setClientManager(new ClientFramework("log4j.properties") {
            @Override
            protected String getGameName() {
                return AbstractClient.this.getGameName();
            }

            @Override
            protected String getVersionNumber() {
                return getVersion();
            }

            @Override
            protected void onPerception(MessageS2CPerception message) {
                try {
                    System.out.println("Received perception "
                            + message.getPerceptionTimestamp());
                    getPerceptionHandler().apply(message,
                            Lookup.getDefault().lookup(IWorldManager.class).getWorld());
                    int i = message.getPerceptionTimestamp();
                    if (isChat()) {
                        RPAction action = new RPAction();
                        if (i % 50 == 0) {
                            action.put("type", "chat");
                            action.put("text", "Hi!");
                            send(action);
                        } else if (i % 50 == 20) {
                            action.put("type", "chat");
                            action.put("text", "How are you?");
                            send(action);
                        }
                    }
                    if (isShowWorld()) {
                        System.out.println("<World contents ------------------------------------->");
                        int j = 0;
                        for (RPObject object
                                : Lookup.getDefault().lookup(IWorldManager.class).getWorld().values()) {
                            j++;
                            System.out.println(j + ". " + object);
                        }
                        System.out.println("</World contents ------------------------------------->");
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            }

            @Override
            protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
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
                    System.out.println("The requested character is not available, trying to create character " + getCharacter());
                    final ClientObject template = new ClientObject();
                    try {
                        final CharacterResult result = createCharacter(getCharacter(), template);
                        if (result.getResult().failed()) {
                            System.out.println(result.getResult().getText());
                        }
                    } catch (final BannedAddressException e) {
                        LOG.log(Level.SEVERE, null, e);
                    } catch (final TimeoutException e) {
                        LOG.log(Level.SEVERE, null, e);
                    } catch (final InvalidVersionException e) {
                        LOG.log(Level.SEVERE, null, e);
                    }
                    return;
                }
                // autologin if a valid character was specified.
                if ((getCharacter() != null) && (characters.keySet().contains(getCharacter()))) {
                    try {
                        chooseCharacter(getCharacter());
                    } catch (final BannedAddressException e) {
                        LOG.log(Level.SEVERE, null, e);
                    } catch (final TimeoutException e) {
                        LOG.log(Level.SEVERE, null, e);
                    } catch (final InvalidVersionException e) {
                        LOG.log(Level.SEVERE, null, e);
                    }
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
        });
    }

    @Override
    public void run() {
        try {
            getClientManager().connect(getHost(), Integer.parseInt(getPort()));
            LOG.log(Level.FINE, "Logging as: {0} with pass: {1} "
                    + "version: ''{2}''", new Object[]{getUsername(),
                        password, getVersion()});
            getClientManager().login(getUsername(), password);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } catch (LoginFailedException e) {
            try {
                LOG.log(Level.WARNING,
                        "Creating account and logging in to continue....");
                getClientManager().createAccount(getUsername(), password, getHost());
                getClientManager().login(getUsername(), password);
            } catch (LoginFailedException | TimeoutException | InvalidVersionException | BannedAddressException ex) {
                LOG.log(Level.SEVERE, null, ex);
                System.exit(1);
            }
        } catch (InvalidVersionException | TimeoutException | BannedAddressException ex) {
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

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the clientManager
     */
    @Override
    public marauroa.client.ClientFramework getClientManager() {
        return clientManager;
    }

    /**
     * @param clientManager the clientManager to set
     */
    @Override
    public void setClientManager(marauroa.client.ClientFramework clientManager) {
        if (this.clientManager != null) {
            LOG.warning("Trying to override ClientFramework!");
        }
        this.clientManager = clientManager;
    }

    /**
     * @return the handler
     */
    @Override
    public PerceptionHandler getPerceptionHandler() {
        return handler;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @param gameName the gameName to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @param handler the handler to set
     */
    @Override
    public void setPerceptionHandler(PerceptionHandler handler) {
        this.handler = handler;
    }

    /**
     * @return the character
     */
    @Override
    public String getCharacter() {
        return character;
    }

    /**
     * @param character the character to set
     */
    public void setCharacter(String character) {
        this.character = character;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void connect(String host, String username, String password,
            String user_character, String port,
            String game_name, String version) throws SocketException {
        setHost(host);
        setUsername(username);
        setPassword(password);
        setCharacter(user_character);
        setPort(port);
        setVersion(version);
        setGameName(game_name);
    }
}
