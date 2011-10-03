package simple.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import marauroa.client.ClientFramework;
import marauroa.client.extension.MarauroaClientExtension;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;
import org.openide.util.Lookup;
import simple.client.entity.IUserContext;
import simple.client.event.ChatListener;
import simple.client.gui.GameObjects;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TextEvent;

/**
 *
 * @author Javier A. Ortiz <javier.ortiz.78@gmail.com> A base class for the
 * Simple client UI (not GUI).
 *
 * This should have minimal UI-implementation dependent code. That's what
 * sub-classes are for!
 */
public class SimpleClient extends ClientFramework implements IPerceptionListener {

    protected SimplePerceptionHandler handler;
    protected static SimpleClient client;
    private String[] available_characters;
    private RPObject player;
    private String userName;
    public static String LOG4J_PROPERTIES = "log4j.properties";
    public ArrayList<String> whoplayers;
    protected GameObjects gameObjects;
    protected Enum state;
    protected ChatScreenInterface mainFrame;
    protected RPObjectChangeDispatcher rpobjDispatcher;
    protected final PerceptionDispatcher dispatch = new PerceptionDispatcher();
    private static final Logger logger = Logger.getLogger(SimpleClient.class.getSimpleName());
    protected String gameName, versionNumber;
    protected World world;

    public static SimpleClient get() {
        if (client == null) {
            client = new SimpleClient(LOG4J_PROPERTIES);
        }
        return client;
    }

    protected SimpleClient(String properties) {
        super(properties);
        world = new World();
        gameObjects = GameObjects.createInstance();
        //Register listeners for normal chat and private messages
        registerListeners();
        //**************************
        rpobjDispatcher = new RPObjectChangeDispatcher(gameObjects,
                getUserContext());
        PerceptionToObject pto = new PerceptionToObject();
        pto.setObjectFactory(new ObjectFactory());
        dispatch.register(pto);
        dispatch.register(SimpleClient.this);
        handler = new SimplePerceptionHandler(dispatch, rpobjDispatcher, this);
        //**************************
        for (Iterator<? extends MarauroaClientExtension> it = Lookup.getDefault().lookupAll(MarauroaClientExtension.class).iterator(); it.hasNext();) {
            MarauroaClientExtension extension = it.next();
            logger.log(Level.INFO, "Loading extension: {0}",
                    extension.getClass().getSimpleName());
        }
    }

    /**
     * Register RPEvent listeners. this is meant to be overwritten by the client.
     * Defaults to listen to chat only.
     */
    protected void registerListeners() {
        ChatListener cl = new ChatListener();
        if (getUserContext() != null ) {
            getUserContext().registerRPEventListener(new TextEvent(), cl);
            getUserContext().registerRPEventListener(new PrivateTextEvent(), cl);
        }
    }

    /**
     * Get interface
     * @return
     */
    public ChatScreenInterface getInterface() {
        return mainFrame;
    }

    /**
     * Set Main Frame
     * @param frame
     */
    public void setMainframe(ChatScreenInterface frame) {
        mainFrame = frame;
    }

    /**
     * Set client state
     * @param newState
     */
    public void setState(Enum newState) {
        state = newState;
    }

    /**
     * Read client state
     * @return
     */
    public Enum getCurrentState() {
        return state;
    }

    public String[] getAvailableCharacters() {
        return available_characters;
    }

    public RPObject getPlayerRPC() {
        return player;
    }

    public void setPlayerRPC(RPObject object) {
        player = object;
    }

    public void sendMessage(String text) {
        RPAction action;
        action = new RPAction();
        action.put("type", "chat");
        action.put("text", text);
        send(action);
    }

    /**
     * Get game objects
     * @return
     */
    public GameObjects getGameObjects() {
        return gameObjects;
    }

    /**
     * Refresh screen
     * @param delta
     */
    public void refresh(int delta) {
        getGameObjects().update(delta);
    }

    /**
     * @return the userContext
     */
    public static IUserContext getUserContext() {
        return Lookup.getDefault().lookup(IUserContext.class);
    }

    /**
     * Generate string for who command
     * @param text
     */
    public void generateWhoPlayers(String text) {
        Matcher matcher = Pattern.compile("^[0-9]+ Players online:( .+)$").matcher(text);
        if (matcher.find()) {
            String[] names = matcher.group(1).split("\\s+");
            whoplayers.clear();
            for (int i = 0; i < names.length; i++) {
                /*
                 * NOTE: On the future Players names won't have any non ascii
                 * character.
                 */
                matcher = Pattern.compile(
                        "^([-_a-zA-Z0-9\u00E4\u00F6\u00FC\u00DF\u00C4\u00D6\u00DC]+)\\([0-9]+\\)$").matcher(
                        names[i]);
                if (matcher.find()) {
                    whoplayers.add(matcher.group(1));
                }
            }
        }
    }

    @Override
    protected void onPerception(MessageS2CPerception message) {
        try {
            handler.apply(message, world.getWorldObjects());
        } catch (java.lang.Exception e) {
            // Something weird happened while applying perception
            logger.log(Level.SEVERE, message.toString(), e);
        }
    }

    @Override
    protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
        return items;
    }

    @Override
    protected void onTransfer(List<TransferContent> items) {
        logger.log(Level.FINE, "Transfering ----");
        for (TransferContent item : items) {
            logger.log(Level.FINE, item.toString());
        }
    }

    @Override
    protected void onAvailableCharacters(String[] characters) {
        available_characters = characters;
    }

    @Override
    protected void onServerInfo(String[] info) {
        logger.log(Level.FINE, "Server info");
        for (String info_string : info) {
            logger.log(Level.FINE, info_string);
        }
    }

    @Override
    protected String getGameName() {
        return gameName;
    }

    @Override
    protected String getVersionNumber() {
        return versionNumber;
    }

    @Override
    protected void onPreviousLogins(List<String> previousLogins) {
        logger.log(Level.FINE, "Previous logins");
        for (String FINE_string : previousLogins) {
            logger.log(Level.FINE, FINE_string);
        }
    }

    /**
     * Set Account username
     * @param username
     */
    public void setAccountUsername(String username) {
        userName = username;
    }

    /**
     * Get account username
     * @return
     */
    public String getAccountUsername() {
        return userName;
    }

    /**
     * Process different RPEvents. This is the default implementation,
     * clients are expected to override to fit their needs
     * 
     * @param event Event to process
     */
    protected void processEvent(RPEvent event) {
        logger.log(Level.FINE, "Processing: {0}", event);
        if (event.getName().equals(TextEvent.RPCLASS_NAME)) {
            logger.log(Level.FINE, "<{0}> {1}", new Object[]{
                        event.get("from"),
                        event.get("text")});
        } else if (event.getName().equals(PrivateTextEvent.RPCLASS_NAME)) {
            logger.log(Level.FINE, "<{0}> {1}", new Object[]{
                        event.get("from"),
                        event.get("text")});
        } else {
            logger.log(Level.WARNING, "Received the following event but didn\'t "
                    + "know how to handle it: {0}", event);
        }
    }

    @Override
    public boolean onAdded(RPObject rpo) {
        logger.log(Level.FINE, "onAdded {0}", rpo.toString());
        return true;
    }

    @Override
    public boolean onModifiedAdded(RPObject object, RPObject changes) {
        logger.log(Level.FINE, "onModifiedAdded {0}: {1}", new Object[]{object, changes});
        return true;
    }

    @Override
    public boolean onModifiedDeleted(RPObject object, RPObject changes) {
        logger.log(Level.FINE, "onModifiedDeleted {0}: {1}", new Object[]{object, changes});
        return true;
    }

    @Override
    public boolean onDeleted(RPObject object) {
        logger.log(Level.FINE, "onDeleted {0}", new Object[]{object});
        return true;
    }

    @Override
    public boolean onMyRPObject(RPObject added, RPObject deleted) {
        logger.log(Level.FINE, "onMyRPObject {0}: {1}", new Object[]{added, deleted});
        return true;
    }

    @Override
    public boolean onClear() {
        logger.log(Level.FINE, "onClear");
        return true;
    }

    @Override
    public void onSynced() {
        logger.log(Level.FINE, "onSynced");
    }

    @Override
    public void onUnsynced() {
        logger.log(Level.FINE, "onUnsynced");
    }

    @Override
    public void onPerceptionBegin(byte b, int i) {
        logger.log(Level.FINE, "onPerceptionBegin {0}: {1}", new Object[]{b, i});
    }

    @Override
    public void onPerceptionEnd(byte b, int i) {
        logger.log(Level.FINE, "onPerceptionEnd {0}: {1}", new Object[]{b, i});
    }

    @Override
    public void onException(Exception excptn, MessageS2CPerception mscp) {
        logger.log(Level.FINE, "onException {0}: {1}", new Object[]{excptn, mscp});
    }
}
