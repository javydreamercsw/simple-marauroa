package simple.server.core.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import marauroa.common.CRC;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.IRPZone;
import marauroa.common.game.Perception;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.TransferContent;
import marauroa.server.game.extension.MarauroaServerExtension;
import marauroa.server.game.rp.MarauroaRPZone;
import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.clientobject.ClientObject;
import simple.server.core.event.DelayedPlayerEventSender;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TurnNotifier;
import simple.server.core.tool.Tool;

public class SimpleRPZone extends MarauroaRPZone {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(SimpleRPZone.class);
    private List<TransferContent> contents;
    private HashMap<String, ClientObjectInterface> players;
    private String description = "";
    private boolean deleteWhenEmpty = false;
    private boolean visited = false;
    private String password = "";

    public SimpleRPZone(String name) {
        super(name);
        contents = new LinkedList<TransferContent>();
        players = new HashMap<String, ClientObjectInterface>();
    }

    //Stuff to do at the end of a turn
    protected void logic() {
    }

    /**
     * Creates a new TransferContent for the specified data and adds it to the
     * contents list.
     *
     * @param name
     * @param byteContents
     */
    protected void addToContent(String name, byte[] byteContents) {
        TransferContent content = new TransferContent();
        content.name = name;
        content.cacheable = true;
        logger.debug("Layer timestamp: " + Integer.toString(content.timestamp));
        content.data = byteContents;
        content.timestamp = CRC.cmpCRC(content.data);

        contents.add(content);
    }

    public List<TransferContent> getContents() {
        return contents;
    }

    @Override
    public void add(RPObject object) {
        add(object, null);
    }

    @Override
    public void onFinish() throws Exception {
        super.onFinish();
        /**
         * Kick everyone to the default zone or they'll end in the limbo!
         */
        Iterator i = getPlayers().iterator();
        while (i.hasNext()) {
            Lookup.getDefault().lookup(IRPWorld.class).changeZone(SimpleRPWorld.getDefaultRoom(),
                    (ClientObject) i.next());
        }
    }

    @Override
    public RPObject remove(RPObject.ID id) {
        return remove(objects.get(id));
    }

    /**
     * Removes object from zone.
     *
     * @param object
     * @return the removed object
     */
    public RPObject remove(RPObject object) {
        for (Iterator<? extends MarauroaServerExtension> it = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it.hasNext();) {
            MarauroaServerExtension extension = it.next();
            extension.onRPObjectRemoveFromZone(object);
        }

        if (object instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) object;
            players.remove(player.getName());
            if (object instanceof ClientObjectInterface) {
                try {
                    //Make sure that the correct onRemoved method is called
                    Configuration conf = Configuration.getConfiguration();
                    Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
                    Class[] types = new Class[]{IRPZone.class};
                    java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("onRemoved", types);
                    localSingleton.invoke(clientObjectClass.cast(object), this);
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                }
            }
            //Let everyone else know
            applyPublicEvent(new PrivateTextEvent(
                    NotificationType.INFORMATION, player.getName()
                    + " left " + getName()));
        } else if (object instanceof Entity) {
            ((Entity) object).onRemoved(this);
        }
        Lookup.getDefault().lookup(IRPWorld.class).deleteIfEmpty(getID().toString());
        return super.remove(object.getID());
    }

    /**
     * Get the zone name. This is the same as
     * <code>getID().getID()</code>, only cleaner to use.
     *
     * @return The zone name.
     */
    public String getName() {
        return getID().getID();
    }

    @Override
    public String toString() {
        return "zone " + zoneid;
    }

    /**
     * Gets all players in this zone.
     *
     * @return A list of all players.
     */
    public Collection<ClientObjectInterface> getPlayers() {
        return players.values();
    }

    /**
     * Gets all players in this zone.
     *
     * @param separator Character to separate the names in the list.
     * @return A list of all players.
     */
    public String getPlayersInString(String separator) {
        String playerList = "";
        Iterator i = players.values().iterator();
        while (i.hasNext()) {
            playerList += ((ClientObjectInterface) i.next()).getName();
            if (i.hasNext()) {
                playerList += separator;
            }
        }
        return playerList;
    }

    /**
     * Gets all non-players in this zone.
     *
     * @return A list of all non-players.
     */
    public List<RPObject> getNonPlayers() {
        ArrayList<RPObject> contentList = new ArrayList<RPObject>();
        Iterator<RPObject> i = iterator();
        while (i.hasNext()) {
            RPObject object = i.next();
            if (!players.containsKey(object.get("name"))) {
                contentList.add(object);
            }
        }
        return contentList;
    }

    public ClientObjectInterface getPlayer(String name) {
        return players.get(name);
    }

    public void add(RPObject object, final ClientObjectInterface player) {
        synchronized (this) {
            /*
             * Assign [zone relative] ID info.
             */
            assignRPObjectID(object);

            for (Iterator<? extends MarauroaServerExtension> it = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it.hasNext();) {
                MarauroaServerExtension extension = it.next();
                logger.debug("Processing extension: " + extension.getClass().getSimpleName());
                extension.onRPObjectAddToZone(object);
            }

            if (object instanceof ClientObjectInterface) {
                logger.debug("Processing ClientObjectInterface");
                try {
                    //Make sure that the correct onAdded method is called
                    Configuration conf = Configuration.getConfiguration();
                    Class<?> clientObjectClass = Class.forName(conf.get("client_object"));
                    Class[] types = new Class[]{IRPZone.class};
                    java.lang.reflect.Method localSingleton = clientObjectClass.getDeclaredMethod("onAdded", types);
                    localSingleton.invoke(clientObjectClass.cast(object), this);
                    final ClientObjectInterface p = ((ClientObjectInterface) object);
                    players.put(p.getName(), p);
                    logger.debug("Object zone: " + ((RPObject) p).get("zoneid"));
                    welcome(p);
                    //Let everyone else know
                    applyPublicEvent(new PrivateTextEvent(
                            NotificationType.INFORMATION, p.getName() + " joined " + getName()));
                } catch (IllegalAccessException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                    /**
                     * Method not implemented,fallback
                     */
                    ((ClientObjectInterface) object).onAdded(this);
                } catch (SecurityException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(SimpleRPZone.class.getSimpleName()).log(Level.SEVERE, null, ex);
                }
            } else if (object instanceof RPEntityInterface) {
                logger.debug("Processing RPEntityInterface");
                ((RPEntityInterface) object).onAdded(this);
            }
            //Request sync previous to any modification
            Lookup.getDefault().lookup(IRPWorld.class).requestSync(object);
            if (player != null) {
                //Notify the player that created it
                player.sendPrivateText(NotificationType.RESPONSE, object + " successfully created!");
            }
            super.add(object);
        }
    }

    public void showZone() {
        logger.debug("Zone " + getName() + " contents:");
        logger.debug("Players: " + (getPlayers().isEmpty() ? "Empty" : ""));
        for (ClientObjectInterface co : getPlayers()) {
            logger.debug(co);
        }
        logger.debug("Objects: " + (objects.entrySet().isEmpty() ? "Empty" : ""));
        for (Entry co : objects.entrySet()) {
            logger.debug(co);
        }
    }

    @Override
    public Perception getPerception(RPObject player, byte type) {
        Perception p = super.getPerception(player, type);
        if (logger.isDebugEnabled() && p.size() > 0) {
            showZone();
        }
        if (!visited) {
            logger.debug("Modifying perception for: " + player);
            logger.debug("Before:");
            logger.debug(p.toString());
            visited = true;
            //Modify the perception
            for (Iterator<? extends MarauroaServerExtension> it = Lookup.getDefault().lookupAll(MarauroaServerExtension.class).iterator(); it.hasNext();) {
                MarauroaServerExtension extension = it.next();
                logger.debug("Processing extension: " + extension.getClass().getSimpleName());
                extension.updateMonitor(player, p);
            }
            logger.debug("Modification Done!");
            logger.debug("After:");
            logger.debug(p.toString());
            visited = false;
        }
        return p;
    }

    /**
     * Send a welcome message to the player which can be configured in
     * server.ini file as "server_welcome". If the value is an http:// address,
     * the first line of that address is read and used as the message
     *
     * @param player ClientObjectInterface
     */
    protected static void welcome(ClientObjectInterface player) {
        String msg = "";
        try {
            Configuration config = Configuration.getConfiguration();
            if (config.has("server_welcome")) {
                msg = config.get("server_welcome");
                if (msg.startsWith("http://")) {
                    URL url = new URL(msg);
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    msg = br.readLine();
                    br.close();
                    connection.disconnect();
                }
            }
        } catch (Exception e) {
            logger.error(null, e);
        }
        if (msg != null && !msg.isEmpty()) {
            Lookup.getDefault().lookup(TurnNotifier.class).notifyInTurns(2,
                    new DelayedPlayerEventSender(new PrivateTextEvent(
                    NotificationType.TUTORIAL, msg), player));
        }
    }

    /**
     * Return whether the zone is completely empty.
     *
     * @return
     */
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * Return whether the zone contains one or more players.
     *
     * @return
     */
    public boolean containsPlayer() {
        for (RPObject obj : objects.values()) {
            if (obj instanceof ClientObjectInterface) {
                return true;
            }
        }
        return false;
    }

    public void applyPublicEvent(RPEvent event) {
        applyPublicEvent(event, 0);
    }

    public void applyPublicEvent(RPEvent event, int delay) {
        Iterator playerList = getPlayers().iterator();
        while (playerList.hasNext()) {
            ClientObjectInterface p = (ClientObjectInterface) playerList.next();
            logger.debug("Adding event to: " + p + ", " + ((RPObject) p).getID() + ", " + p.getZone());
            if (delay <= 0) {
                ((RPObject) p).addEvent(event);
                p.notifyWorldAboutChanges();
            } else {
                logger.debug("With a delay of " + delay + " turns");
                Lookup.getDefault().lookup(TurnNotifier.class).notifyInTurns(delay,
                        new DelayedPlayerEventSender(event, p));
            }
        }
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the deleteWhenEmpty
     */
    public boolean isDeleteWhenEmpty() {
        return deleteWhenEmpty;
    }

    /**
     * @param deleteWhenEmpty the deleteWhenEmpty to set
     */
    public void setDeleteWhenEmpty(boolean deleteWhenEmpty) {
        this.deleteWhenEmpty = deleteWhenEmpty;
    }

    public void setPassword(String pass) throws IOException {
        /**
         * Encrypt password with private key. This way encryption is unique per
         * server. (Assuming that the server.ini file was generated and not
         * copied)
         */
        if (pass != null && !pass.isEmpty()) {
            password = Tool.Encrypt(pass, Configuration.getConfiguration().get("d"));
        }
    }

    /**
     * @return the password
     */
    protected String getPassword() {
        return password;
    }

    /**
     * @return the locked
     */
    public boolean isLocked() {
        return !password.trim().isEmpty();
    }

    public void unlock() {
        if (isLocked()) {
            password = "";
        }
    }

    public boolean isPassword(String pass) {
        try {
            return Tool.Encrypt(pass, Configuration.getConfiguration().get("d")).equals(password);
        } catch (IOException ex) {
            logger.error(ex);
            return false;
        }
    }
}
