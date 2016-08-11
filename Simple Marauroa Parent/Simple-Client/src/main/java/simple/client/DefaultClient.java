package simple.client;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.Collection;
import java.util.HashMap;
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
import marauroa.common.game.AccountResult;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CLoginNACK.Reasons;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
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
@ServiceProvider(service = ClientFrameworkProvider.class)
public class DefaultClient implements ClientFrameworkProvider {

    private String port;
    private String gameName;
    private String version;
    private marauroa.client.ClientFramework clientManager;
    private PerceptionHandler handler;
    private static boolean showWorld = false, chat = false;
    private String character;
    private String host;
    private String username;
    private String password;
    private String email;
    private Map<String, RPObject> characters = new HashMap<>();
    private boolean createDefaultCharacter = false;
    private boolean connected = false;
    private boolean autocreation = true;

    private static final Logger LOG
            = Logger.getLogger(DefaultClient.class.getSimpleName());

    @Override
    public boolean isShowWorld() {
        return showWorld;
    }

    @Override
    public void setShowWorld(boolean aShowWorld) {
        showWorld = aShowWorld;
    }

    @Override
    public boolean isChat() {
        return chat;
    }

    @Override
    public void setChat(boolean aChat) {
        chat = aChat;
    }

    protected void createClientManager(String name, String gversion) {
        setGameName(name);
        setVersion(gversion);
        setClientManager(new ClientFramework("log4j.properties") {
            @Override
            protected String getGameName() {
                return DefaultClient.this.getGameName();
            }

            @Override
            protected String getVersionNumber() {
                return getVersion();
            }

            @Override
            protected void onPerception(MessageS2CPerception message) {
                try {
                    LOG.log(Level.FINE, "Received perception {0}",
                            message.getPerceptionTimestamp());
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
                        LOG.log(Level.FINE, "<World contents ------------------------------------->");
                        int j = 0;
                        for (RPObject object
                                : Lookup.getDefault().lookup(IWorldManager.class)
                                .getWorld().values()) {
                            j++;
                            LOG.log(Level.FINE, "{0}. {1}",
                                    new Object[]{j, object});
                        }
                        LOG.log(Level.FINE, "</World contents ------------------------------------->");
                    }
                }
                catch (Exception e) {
                    LOG.log(Level.SEVERE, null, e);
                }
            }

            @Override
            protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
                items.stream().forEach((item) -> {
                    item.ack = true;
                });
                return items;
            }

            @Override
            protected void onTransfer(List<TransferContent> items) {
                LOG.log(Level.FINE, "Transfering ----");
                items.stream().forEach((item) -> {
                    LOG.log(Level.FINE, item.toString());
                });
            }

            @Override
            protected void onAvailableCharacters(String[] characters) {
                //See onAvailableCharacterDetails
            }

            @Override
            protected void onAvailableCharacterDetails(Map<String, RPObject> characters) {
                Lookup.getDefault().lookup(ClientFrameworkProvider.class)
                        .getCharacters().clear();
                Lookup.getDefault().lookup(ClientFrameworkProvider.class)
                        .getCharacters().putAll(characters);
                // If there are no characters, create one with the specified name automatically
                if (characters.isEmpty() && isCreateDefaultCharacter()) {
                    LOG.log(Level.WARNING,
                            "The requested character is not available, trying "
                            + "to create character {0}", getCharacter());
                    final ClientObject template = new ClientObject();
                    try {
                        final CharacterResult result = createCharacter(getCharacter(),
                                template);
                        if (result.getResult().failed()) {
                            LOG.log(Level.WARNING, result.getResult().getText());
                        }
                    }
                    catch (final BannedAddressException | TimeoutException | InvalidVersionException e) {
                        LOG.log(Level.SEVERE, null, e);
                    }
                    return;
                }
                // Autologin if a valid character was specified.
                if ((getCharacter() != null)
                        && (characters.keySet().contains(getCharacter()))
                        && isCreateDefaultCharacter()) {
                    try {
                        chooseCharacter(getCharacter());
                    }
                    catch (final BannedAddressException | TimeoutException | InvalidVersionException e) {
                        LOG.log(Level.SEVERE, null, e);
                    }
                }
            }

            @Override
            protected void onServerInfo(String[] info) {
                LOG.log(Level.FINE, "Server info");
                for (String info_string : info) {
                    LOG.log(Level.FINE, info_string);
                }
            }

            @Override
            protected void onPreviousLogins(List<String> previousLogins) {
                LOG.log(Level.FINE, "Previous logins");
                previousLogins.stream().forEach((info_string) -> {
                    LOG.log(Level.FINE, info_string);
                });
            }
        });
    }

    private void showLoginDialog() {
        LoginProvider lp = Lookup.getDefault().lookup(LoginProvider.class);
        if (lp != null) {
            lp.displayLoginDialog();
        }
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        if (getPerceptionHandler() == null) {
            setPerceptionHandler(new PerceptionHandler(new IPerceptionListener() {
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
                    Lookup.getDefault().lookupAll(ExceptionListener.class)
                            .stream().forEach((listener) -> {
                                listener.onException(exception, perception);
                            });
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
                            object.applyDifferences(added, deleted);
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
                    Lookup.getDefault().lookupAll(PerceptionListener.class)
                            .stream().forEach((listener) -> {
                                listener.onPerceptionBegin(type, timestamp);
                            });
                }

                @Override
                public void onPerceptionEnd(byte type, int timestamp) {
                    Lookup.getDefault().lookupAll(PerceptionListener.class)
                            .stream().forEach((listener) -> {
                                listener.onPerceptionEnd(type, timestamp);
                            });
                }

                @Override
                public void onSynced() {
                    Lookup.getDefault().lookupAll(SyncListener.class).stream()
                            .forEach((listener) -> {
                                listener.onSynced();
                            });
                }

                @Override
                public void onUnsynced() {
                    Lookup.getDefault().lookupAll(SyncListener.class).stream()
                            .forEach((listener) -> {
                                listener.onUnsynced();
                            });
                }
            }));
        }
        if (getClientManager() == null) {
            createClientManager(gameName != null ? gameName : "jWrestling",
                    version != null ? version : "1.0");
        }
        try {
            if (!Lookup.getDefault().lookup(LoginProvider.class).isAuthenticated()) {
                showLoginDialog();
            }
            getClientManager().connect(getHost(), Integer.parseInt(getPort()));
            LOG.log(Level.FINE, "Logging as: {0} with pass: {1} "
                    + "version: ''{2}''", new Object[]{getUsername(),
                        password, getVersion()});
            setEmail("dummy@dummy.com");
            getClientManager().login(getUsername(), password);
            connected = true;
        }
        catch (ConnectException ex) {
            Lookup.getDefault().lookup(MessageProvider.class).displayWarning(
                    "Unable to connect",
                    "Unable to connect to the server: " + getHost());
            showLoginDialog();
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        catch (LoginFailedException e) {
            if (e.getReason().equals(Reasons.USERNAME_WRONG) && isAutoCreation()) {
                try {
                    if (getEmail() == null) {
                        //Prompt user to enter additional information
                        LoginProvider lp = Lookup.getDefault().lookup(LoginProvider.class);
                        if (lp != null) {
                            lp.getEmailFromUser();
                            while (getEmail().trim().isEmpty()) {
                                Thread.sleep(100);
                            }
                        }
                    }
                    if (getEmail() == null) {
                        LOG.severe("Unable to proceed without an email!");
                        throw new RuntimeException("Unable to proceed without an email!");
                    }
                    LOG.log(Level.WARNING,
                            "Creating account and logging in to continue....");
                    AccountResult result = getClientManager().createAccount(getUsername(),
                            password, getEmail());
                    switch (result.getResult()) {
                        case OK_CREATED:
                            getClientManager().login(getUsername(), password);
                            connected = true;
                            break;
                        case FAILED_CREATE_ON_MAIN_INSTEAD:
                            LOG.severe("Account creation is disabled on server!");
                            Lookup.getDefault().lookup(MessageProvider.class).displayError("ERROR",
                                    "Account creation is disabled on server!");
                            break;
                        default:
                            LOG.log(Level.SEVERE, "Unable to create account: {0}",
                                    result.getResult().getText());
                            Lookup.getDefault().lookup(MessageProvider.class).displayError("ERROR",
                                    "Unable to create account: " + result.getResult().getText());
                            break;
                    }
                }
                catch (LoginFailedException | TimeoutException | BannedAddressException ex) {
                    if (ex instanceof LoginFailedException) {
                        Lookup.getDefault().lookup(MessageProvider.class)
                                .displayWarning("Login Failed!",
                                        ex.getLocalizedMessage()
                                        + "\nMake sure you have verified "
                                        + "your account. Check your provided email.");
                        showLoginDialog();
                    }
                }
                catch (InvalidVersionException ex) {
                    Lookup.getDefault().lookup(MessageProvider.class)
                            .displayError("Invalid version!",
                                    "Invalid version: " + ex.getVersion()
                                    + " vs. protocol version: " + ex.getProtocolVersion());
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(DefaultClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Lookup.getDefault().lookup(MessageProvider.class)
                        .displayWarning("Login Failed!", e.getLocalizedMessage());
                showLoginDialog();
            }
        }
        catch (InvalidVersionException | TimeoutException | BannedAddressException ex) {
            System.exit(1);
        }
        while (isConnected()) {
            getClientManager().loop(0);
            try {
                sleep(100);
            }
            catch (InterruptedException e) {
                LOG.log(Level.SEVERE, null, e);
                connected = false;
            }
        }
    }

    @Override
    public String getPort() {
        return port;
    }

    @Override
    public String getGameName() {
        return gameName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public final marauroa.client.ClientFramework getClientManager() {
        return clientManager;
    }

    @Override
    public void setClientManager(marauroa.client.ClientFramework clientManager) {
        if (this.clientManager != null) {
            LOG.warning("Trying to override ClientFramework!");
        }
        this.clientManager = clientManager;
    }

    @Override
    public final PerceptionHandler getPerceptionHandler() {
        return handler;
    }

    @Override
    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public void setVersion(String version) {
        LOG.log(Level.FINE, "Version changed from: ''{0}'' to: ''{1}''",
                new Object[]{this.version, version});
        this.version = version;
    }

    @Override
    public final void setPerceptionHandler(PerceptionHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getCharacter() {
        return character;
    }

    @Override
    public void setCharacter(String character) {
        this.character = character;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void connect(String host, String username, String password,
            String user_character, String port,
            String game_name, String version)
            throws SocketException {
        setHost(host);
        setUsername(username);
        setPassword(password);
        setCharacter(user_character);
        setPort(port);
        setVersion(version);
        setGameName(game_name);
    }

    @Override
    public Map<String, RPObject> getCharacters() {
        return characters;
    }

    @Override
    public boolean isCreateDefaultCharacter() {
        return createDefaultCharacter;
    }

    @Override
    public void setCreateDefaultCharacter(boolean createDefaultCharacter) {
        this.createDefaultCharacter = createDefaultCharacter;
    }

    /**
     * @return the connected
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public boolean chooseCharacter(String character) throws TimeoutException,
            InvalidVersionException, BannedAddressException {
        return getClientManager().chooseCharacter(character);
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isAutoCreation() {
        return autocreation;
    }

    @Override
    public void setAutoCreation(boolean autocreation) {
        this.autocreation = autocreation;
    }
}
