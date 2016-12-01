package simple.server.core.engine;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.CRC;
import marauroa.common.Configuration;
import marauroa.common.game.Perception;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObjectInvalidException;
import marauroa.common.net.message.TransferContent;
import marauroa.server.game.rp.MarauroaRPZone;
import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.entity.Entity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.entity.character.PlayerCharacter;
import simple.server.core.entity.npc.NPC;
import simple.server.core.event.DelayedPlayerEventSender;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.event.TurnNotifier;
import simple.server.core.tool.Tool;
import simple.server.extension.MarauroaServerExtension;

public class SimpleRPZone extends MarauroaRPZone implements ISimpleRPZone {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPZone.class.getSimpleName());
    private final List<TransferContent> contents;
    private String description = "";
    private boolean deleteWhenEmpty = false;
    private boolean visited = false;
    private String password = "";

    public SimpleRPZone(final String name) {
        super(name);
        contents = new LinkedList<>();
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
        LOG.log(Level.FINE, "Layer timestamp: {0}",
                Integer.toString(content.timestamp));
        content.data = byteContents;
        content.timestamp = CRC.cmpCRC(content.data);

        contents.add(content);
    }

    @Override
    public List<TransferContent> getContents() {
        return contents;
    }

    @Override
    public RPObject remove(RPObject.ID id) {
        RPObject object = super.remove(id);
        if (object != null) {
            Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                    .stream().forEach((extension) -> {
                        extension.onRPObjectRemoveFromZone(object);
                    });

            if (object instanceof RPEntityInterface) {
                RPEntityInterface player = (RPEntityInterface) object;
                player.onRemoved(this);
                //Let everyone else know
                applyPublicEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, player.getName()
                        + " left " + getName()));
            } else if (object instanceof Entity) {
                ((RPEntityInterface) object).onRemoved(this);
            }
            Lookup.getDefault().lookup(IRPWorld.class).deleteIfEmpty(
                    getID().getID());
            //Update the Player list
            Iterator<RPEntityInterface> it = getPlayers().iterator();
            while (it.hasNext()) {
                RPEntityInterface player = it.next();
                if (player.getName().equals(Tool.extractName(object))) {
                    it.remove();
                    break;
                }
            }
            //Update the NPC list
            Iterator<RPObject> it2 = getNPCS().iterator();
            while (it2.hasNext()) {
                RPObject npc = it2.next();
                if (Tool.extractName(npc).equals(Tool.extractName(object))) {
                    it2.remove();
                    break;
                }
            }
            return object;
        } else {
            LOG.warning("Trying to remove null RPObject!");
            return null;
        }
    }

    /**
     * Removes object from zone.
     *
     * @param object
     * @return the removed object
     */
    @Override
    public RPObject remove(final RPObject object) {
        return remove(object.getID());
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
    public Collection<RPEntityInterface> getPlayers() {
        List<RPEntityInterface> result = new ArrayList<>();
        objects.values().stream().filter((o)
                -> (o instanceof RPEntity)).forEachOrdered((o) -> {
            result.add((RPEntityInterface) o);
        });
        return result;
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
        Iterator i = objects.values().iterator();
        while (i.hasNext()) {
            playerList.append(((ClientObjectInterface) i.next()).getName());
            if (i.hasNext()) {
                playerList.append(separator);
            }
        }
        return playerList.toString();
    }

    @Override
    public RPEntityInterface getPlayer(final String name) {
        RPEntityInterface result = null;
        for (RPEntityInterface o : getPlayers()) {
            if (o.getName().equals(name)) {
                result = o;
                break;
            }
        }
        return result;
    }

    @Override
    public RPObject getNPC(String name) {
        RPObject result = null;
        for (RPObject o : getNPCS()) {
            if (Tool.extractName((RPObject) o).equals(name)) {
                result = o;
                break;
            }
        }
        return result;
    }

    @Override
    public void add(RPObject object) throws RPObjectInvalidException {
        synchronized (this) {
            if (object.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
                add(new RPEntity(object), null);
            } else {
                super.add(object);
            }
        }
    }

    @Override
    public void add(final RPObject object, final RPEntityInterface player) {
        synchronized (this) {
            /*
             * Assign [zone relative] ID info if not already there.
             */
            if (!object.has("id")) {
                assignRPObjectID(object);
            }

            if (object instanceof RPEntityInterface) {
                RPEntityInterface p = (RPEntityInterface) object;
                LOG.fine("Processing RPEntityInterface");
                //Let everyone else know
                applyPublicEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, p.getName()
                        + " joined " + getName()));
                p.onAdded(this);
            } else if (object.has(WellKnownActionConstant.TYPE)) {
                switch (object.get(WellKnownActionConstant.TYPE)) {
                    case PlayerCharacter.DEFAULT_RP_CLASSNAME:
                        if (getPlayer(Tool.extractName(object)) != null) {
                            LOG.log(Level.FINE,
                                    "Character added:\n{0}", object);
                        }
                        break;
                    default:
                        LOG.log(Level.WARNING,
                                "Unhandled RPObject added:\n{0}", object);
                        break;
                }
            }
            super.add(object);
            //Request sync previous to any modification
            Lookup.getDefault().lookup(IRPWorld.class).requestSync(object);
            if (player != null) {
                //Notify the player that created it
                player.sendPrivateText(NotificationType.RESPONSE, object
                        + " successfully created!");
            }
            Lookup.getDefault().lookupAll(MarauroaServerExtension.class)
                    .stream().map((extension) -> {
                        LOG.log(Level.FINE, "Processing extension: {0}",
                                extension.getClass().getSimpleName());
                        return extension;
                    }).forEachOrdered((extension) -> {
                extension.onRPObjectAddToZone(object);
            });
        }
    }

    @Override
    public void showZone() {
        if (LOG.isLoggable(Level.FINE)) {
            System.out.println("Zone " + getName() + " contents:");
            System.out.println("Players: "
                    + (getPlayers().isEmpty() ? "Empty" : ""));
            LOG.log(Level.INFO, "Players: {0}",
                    (getPlayers().isEmpty() ? "Empty" : ""));
            getPlayers().stream().forEach((co) -> {
                System.out.println(co.toString());
            });
            System.out.println("NPC's: " + (getNPCS().isEmpty()
                    ? "Empty" : ""));
            objects.entrySet().stream().forEach((co) -> {
                System.out.println(co.toString());
            });
            System.out.println("-------------------------------------------------");
        }
    }

    @Override
    public Perception getPerception(final RPObject player, final byte type) {
        Perception p = super.getPerception(player, type);
        if (p.size() > 0) {
            showZone();
        }
        if (!visited) {
            LOG.log(Level.FINE, "Modifying perception for: {0}", player);
            LOG.fine("Before:");
            LOG.fine(p.toString());
            visited = true;
            LOG.fine("Modification Done!");
            LOG.fine("After:");
            LOG.fine(p.toString());
            visited = false;
        }
        return p;
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
        objects.values().stream().map((obj) -> {
            if (obj instanceof ClientObjectInterface) {
                LOG.log(Level.FINE, "Adding event to: {0}, {1}, {2}",
                        new Object[]{obj, obj.getID(),
                            ((ClientObjectInterface) obj).getZone()});
                if (delay <= 0) {
                    obj.addEvent(event);
                    ((ClientObjectInterface) obj).notifyWorldAboutChanges();
                } else {
                    LOG.log(Level.FINE, "With a delay of {0} turns", delay);
                    Lookup.getDefault().lookup(TurnNotifier.class).notifyInTurns(
                            delay,
                            new DelayedPlayerEventSender(event, obj));
                }
            } else {
                LOG.log(Level.FINE, "Adding event to: {0}, {1}, {2}",
                        new Object[]{obj, obj.getID(),
                            Lookup.getDefault().lookup(IRPWorld.class)
                                    .getZone(obj.get(Entity.ZONE_ID))});
                obj.addEvent(event);
            }
            return obj;
        }).forEachOrdered((obj) -> {
            Lookup.getDefault().lookup(IRPWorld.class).modify(obj);
        });
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
    public final void setDescription(String description) {
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
        }
        catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            result = false;
        }
        return result;
    }

    /**
     * @return the NPCs
     */
    @Override
    public Collection<RPObject> getNPCS() {
        List<RPObject> result = new ArrayList<>();
        objects.values().stream().filter((o)
                -> (o instanceof NPC)).forEachOrdered((o) -> {
            result.add(o);
        });
        return result;
    }

    @Override
    public Collection<RPObject> getZoneContents() {
        return objects.values();
    }
}
