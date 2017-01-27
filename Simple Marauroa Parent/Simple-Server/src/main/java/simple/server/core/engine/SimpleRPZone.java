package simple.server.core.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.CRC;
import marauroa.common.Configuration;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObjectInvalidException;
import marauroa.common.net.message.TransferContent;
import marauroa.server.game.rp.MarauroaRPZone;
import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.tool.Tool;
import simple.server.extension.MarauroaServerExtension;

public class SimpleRPZone extends MarauroaRPZone implements ISimpleRPZone {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPZone.class.getSimpleName());
    private String description = "";
    private boolean deleteWhenEmpty = false;
    private String password = "";
    private List<TransferContent> contents;

    public SimpleRPZone(final String name) {
        super(name);
    }

    @Override
    public void add(RPObject object) throws RPObjectInvalidException {
        synchronized (this) {
            if (object.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
                add(new RPEntity(object), null);
            } else {
                super.add(object);
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
    }

    @Override
    public void add(RPObject object, RPEntityInterface player) {
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
                Lookup.getDefault().lookup(IRPWorld.class)
                        .applyPublicEvent(new PrivateTextEvent(
                                NotificationType.INFORMATION, p.getName()
                                + " joined " + getName()));
                super.add(object);
                p.onAdded(this);
            } else {
                super.add(object);
            }
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
    public boolean containsPlayer() {
        return !getPlayers().isEmpty();
    }

    @Override
    public List<TransferContent> getContents() {
        return contents;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return getID().getID();
    }

    @Override
    public RPEntityInterface getPlayer(String name) {
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
    public Collection<RPEntityInterface> getPlayers() {
        List<RPEntityInterface> result = new ArrayList<>();
        objects.values().stream().filter((o)
                -> (o instanceof RPEntity)).forEachOrdered((o) -> {
            result.add((RPEntityInterface) o);
        });
        return result;
    }

    @Override
    public Collection<RPObject> getNPCS() {
        List<RPObject> result = new ArrayList<>();
        objects.values().stream().filter((o)
                -> !(o instanceof RPEntity)).forEachOrdered((o) -> {
            result.add(o);
        });
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
    public String getPlayersInString(String separator) {
        StringBuilder playerList = new StringBuilder();
        Iterator i = objects.values().iterator();
        while (i.hasNext()) {
            playerList.append(Tool.extractName((RPObject) i.next()));
            if (i.hasNext()) {
                playerList.append(separator);
            }
        }
        return playerList.toString();
    }

    @Override
    public boolean isDeleteWhenEmpty() {
        return deleteWhenEmpty;
    }

    @Override
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    @Override
    public boolean isLocked() {
        return !password.trim().isEmpty();
    }

    @Override
    public boolean isPassword(String pass) {
        boolean result;
        try {
            result = Tool.encrypt(pass,
                    Configuration.getConfiguration().get("d")).equals(password);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            result = false;
        }
        return result;
    }

    @Override
    public RPObject remove(RPObject object) {
        return super.remove(object.getID());
    }

    @Override
    public void setDeleteWhenEmpty(boolean deleteWhenEmpty) {
        this.deleteWhenEmpty = deleteWhenEmpty;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPassword(String pass) throws IOException {
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

    @Override
    public void showZone() {
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
        getNPCS().stream().forEach((co) -> {
            System.out.println(co.toString());
        });
        System.out.println("-------------------------------------------------");
    }

    @Override
    public void unlock() {
        if (isLocked()) {
            password = "";
        }
    }

    @Override
    public Collection<RPObject> getZoneContents() {
        return objects.values();
    }
}
