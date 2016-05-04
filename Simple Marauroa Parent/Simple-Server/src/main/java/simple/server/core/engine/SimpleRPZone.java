package simple.server.core.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import marauroa.common.CRC;
import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.IRPZone;
import marauroa.common.game.Perception;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.TransferContent;
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
import simple.server.extension.MarauroaServerExtension;

public class SimpleRPZone extends MarauroaRPZone implements ISimpleRPZone {

    /**
     * the logger instance.
     */
    private static final Logger LOG = Log4J.getLogger(SimpleRPZone.class);
    private final List<TransferContent> contents;
    private final HashMap<String, ClientObjectInterface> players;
    public final HashMap<String, RPEntityInterface> npcs;
    private String description = "";
    private boolean deleteWhenEmpty = false;
    private boolean visited = false;
    private String password = "";

    public SimpleRPZone(final String name) {
        super(name);
        contents = new LinkedList<>();
        players = new HashMap<>();
        npcs = new HashMap<>();
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
    protected void addToContent(final String name, final byte[] byteContents) {
        TransferContent content = new TransferContent();
        content.name = name;
        content.cacheable = true;
        LOG.debug("Layer timestamp: " + Integer.toString(content.timestamp));
        content.data = byteContents;
        content.timestamp = CRC.cmpCRC(content.data);

        contents.add(content);
    }

    @Override
    public List<TransferContent> getContents() {
        return contents;
    }

    @Override
    public void add(final RPObject object) {
        add(object, null);
    }

    @Override
    public RPObject remove(final RPObject.ID id) {
        return remove(objects.get(id));
    }

    /**
     * Removes object from zone.
     *
     * @param object
     * @return the removed object
     */
    @Override
    public RPObject remove(final RPObject object) {
        if (object != null) {
            Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                    .stream().forEach((extension) -> {
                        extension.onRPObjectRemoveFromZone(object);
                    });

            if (object instanceof ClientObjectInterface) {
                ClientObjectInterface player = (ClientObjectInterface) object;
                players.remove(player.getName());
                try {
                    //Make sure that the correct onRemoved method is called
                    Configuration conf = Configuration.getConfiguration();
                    Class<?> clientObjectClass = Class.forName(
                            conf.get("client_object",
                                    ClientObject.class.getCanonicalName()));
                    Class[] types = new Class[]{IRPZone.class};
                    java.lang.reflect.Method localSingleton = clientObjectClass
                            .getDeclaredMethod("onRemoved", types);
                    localSingleton.invoke(clientObjectClass.cast(object), this);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException | IOException ex) {
                    LOG.error(ex, ex);
                }
                //Let everyone else know
                applyPublicEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, player.getName()
                        + " left " + getName()));
            } else if (object instanceof Entity) {
                ((Entity) object).onRemoved(this);
                npcs.remove(Tool.extractName(object));
            }
            Lookup.getDefault().lookup(IRPWorld.class).deleteIfEmpty(
                    getID().toString());
            return super.remove(object.getID());
        } else {
            LOG.warn("Trying to remove null RPObject!");
            return null;
        }
    }

    /**
     * Get the zone name. This is the same as <code>getID().getID()</code>, only
     * cleaner to use.
     *
     * @return The zone name.
     */
    @Override
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
    @Override
    public Collection<ClientObjectInterface> getPlayers() {
        return players.values();
    }

    /**
     * Gets all players in this zone.
     *
     * @param separator Character to separate the names in the list.
     * @return A list of all players.
     */
    @Override
    public String getPlayersInString(final String separator) {
        StringBuilder playerList = new StringBuilder();
        Iterator i = players.values().iterator();
        while (i.hasNext()) {
            playerList.append(((ClientObjectInterface) i.next()).getName());
            if (i.hasNext()) {
                playerList.append(separator);
            }
        }
        return playerList.toString();
    }

    @Override
    public ClientObjectInterface getPlayer(final String name) {
        return players.get(name);
    }

    @Override
    public void add(final RPObject object, final ClientObjectInterface player) {
        synchronized (this) {
            /*
             * Assign [zone relative] ID info.
             */
            assignRPObjectID(object);

            if (object instanceof ClientObjectInterface) {
                LOG.debug("Processing ClientObjectInterface");
                try {
                    //Make sure that the correct onAdded method is called
                    Configuration conf = Configuration.getConfiguration();
                    Class<?> clientObjectClass
                            = Class.forName(conf.get("client_object",
                                    ClientObject.class.getCanonicalName()));
                    Class[] types = new Class[]{IRPZone.class};
                    java.lang.reflect.Method localSingleton
                            = clientObjectClass.getDeclaredMethod("onAdded",
                                    types);
                    localSingleton.invoke(clientObjectClass.cast(object), this);
                    final ClientObjectInterface p
                            = ((ClientObjectInterface) object);
                    if (!players.containsKey(p.getName())) {
                        players.put(p.getName(), p);
                        welcome(p);
                    }
                    LOG.debug("Object zone: "
                            + ((RPObject) p).get(Entity.ZONE_ID));
                    //Let everyone else know
                    applyPublicEvent(new PrivateTextEvent(
                            NotificationType.INFORMATION, p.getName()
                            + " joined " + getName()));
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | ClassNotFoundException | IOException ex) {
                    LOG.error(ex, ex);
                } catch (NoSuchMethodException ex) {
                    /**
                     * Method not implemented,fallback
                     */
                    ((ClientObjectInterface) object).onAdded(this);
                }
            } else if (object instanceof RPEntityInterface) {
                LOG.debug("Processing RPEntityInterface");
                ((RPEntityInterface) object).onAdded(this);
                npcs.put(Tool.extractName(object), (RPEntityInterface) object);
            }
            //Request sync previous to any modification
            Lookup.getDefault().lookup(IRPWorld.class).requestSync(object);
            if (player != null) {
                //Notify the player that created it
                player.sendPrivateText(NotificationType.RESPONSE, object
                        + " successfully created!");
            }
            super.add(object);
            Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                    .stream().map((extension) -> {
                        LOG.debug("Processing extension: "
                                + extension.getClass().getSimpleName());
                        return extension;
                    }).forEach((extension) -> {
                extension.onRPObjectAddToZone(object);
            });
        }
    }

    @Override
    public void showZone() {
        LOG.debug("Zone " + getName() + " contents:");
        LOG.debug("Players: " + (getPlayers().isEmpty() ? "Empty" : ""));
        getPlayers().stream().forEach((co) -> {
            LOG.debug(co);
        });
        LOG.debug("Objects: " + (objects.entrySet().isEmpty()
                ? "Empty" : ""));
        objects.entrySet().stream().forEach((co) -> {
            LOG.debug(co);
        });
    }

    @Override
    public Perception getPerception(final RPObject player, final byte type) {
        Perception p = super.getPerception(player, type);
        if (LOG.isDebugEnabled() && p.size() > 0) {
            showZone();
        }
        if (!visited) {
            LOG.debug("Modifying perception for: " + player);
            LOG.debug("Before:");
            LOG.debug(p.toString());
            visited = true;
            LOG.debug("Modification Done!");
            LOG.debug("After:");
            LOG.debug(p.toString());
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
    protected static void welcome(final ClientObjectInterface player) {
        String msg = "";
        try {
            Configuration config = Configuration.getConfiguration();
            if (config.has("server_welcome")) {
                msg = config.get("server_welcome");
                if (msg.startsWith("http://")) {
                    URL url = new URL(msg);
                    HttpURLConnection.setFollowRedirects(false);
                    HttpURLConnection connection
                            = (HttpURLConnection) url.openConnection();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {
                        msg = br.readLine();
                    }
                    connection.disconnect();
                }
            }
        } catch (Exception e) {
            LOG.error(null, e);
        }
        TurnNotifier notifier = Lookup.getDefault().lookup(TurnNotifier.class);
        if (msg != null && !msg.isEmpty()) {
            if (notifier != null) {
                notifier.notifyInTurns(2,
                        new DelayedPlayerEventSender(new PrivateTextEvent(
                                NotificationType.TUTORIAL, msg), player));
            } else {
                LOG.warn("Unable to send message: '" + msg
                        + "' to player: " + player.getName());
            }
        }
    }

    /**
     * Return whether the zone is completely empty.
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * Return whether the zone contains one or more players.
     *
     * @return
     */
    @Override
    public boolean containsPlayer() {
        boolean result = false;
        for (RPObject obj : objects.values()) {
            if (obj instanceof ClientObjectInterface) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void applyPublicEvent(RPEvent event) {
        applyPublicEvent(event, 0);
    }

    @Override
    public void applyPublicEvent(final RPEvent event, final int delay) {
        for (ClientObjectInterface p : getPlayers()) {
            LOG.debug("Adding event to: " + p + ", " + ((RPObject) p).getID()
                    + ", " + p.getZone());
            if (delay <= 0) {
                ((RPObject) p).addEvent(event);
                p.notifyWorldAboutChanges();
            } else {
                LOG.debug("With a delay of " + delay + " turns");
                Lookup.getDefault().lookup(TurnNotifier.class).notifyInTurns(
                        delay,
                        new DelayedPlayerEventSender(event, p));
            }
        }
        for (RPEntityInterface npc : getNPCS()) {
            LOG.debug("Adding event to: " + npc + ", " + ((RPObject) npc).getID()
                    + ", " + npc.getZone());
            ((RPObject) npc).addEvent(event);
            Lookup.getDefault().lookup(IRPWorld.class).modify((RPObject) npc);
        }
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the deleteWhenEmpty
     */
    @Override
    public boolean isDeleteWhenEmpty() {
        return deleteWhenEmpty;
    }

    /**
     * @param deleteWhenEmpty the deleteWhenEmpty to set
     */
    @Override
    public void setDeleteWhenEmpty(boolean deleteWhenEmpty) {
        this.deleteWhenEmpty = deleteWhenEmpty;
    }

    @Override
    public void setPassword(final String pass) throws IOException {
        /**
         * Encrypt password with private key. This way encryption is unique per
         * server. (Assuming that the server.ini file was generated and not
         * copied)
         */
        if (pass != null && !pass.isEmpty()) {
            password = Tool.encrypt(pass,
                    Configuration.getConfiguration().get("d"));
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
    @Override
    public boolean isLocked() {
        return !password.trim().isEmpty();
    }

    @Override
    public void unlock() {
        if (isLocked()) {
            password = "";
        }
    }

    @Override
    public boolean isPassword(final String pass) {
        boolean result;
        try {
            result = Tool.encrypt(pass,
                    Configuration.getConfiguration().get("d")).equals(password);
        } catch (IOException ex) {
            LOG.error(ex);
            result = false;
        }
        return result;
    }

    /**
     * @return the npcs
     */
    @Override
    public Collection<RPEntityInterface> getNPCS() {
        return npcs.values();
    }
}
