package simple.server.core.engine;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObjectInvalidException;
import marauroa.common.net.message.TransferContent;
import marauroa.server.game.rp.MarauroaRPZone;
import org.openide.util.Lookup;
import simple.common.NotificationType;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.PrivateTextEvent;
import simple.server.extension.MarauroaServerExtension;

public class SimpleRPZone extends MarauroaRPZone implements ISimpleRPZone {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(SimpleRPZone.class.getSimpleName());
    private String description = "";

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

    @Override
    public boolean containsPlayer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<TransferContent> getContents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<RPEntityInterface> getPlayers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<RPObject> getNPCS() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RPObject getNPC(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPlayersInString(String separator) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDeleteWhenEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        return !iterator().hasNext();
    }

    @Override
    public boolean isLocked() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isPassword(String pass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RPObject remove(RPObject object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDeleteWhenEmpty(boolean deleteWhenEmpty) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setPassword(String pass) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showZone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unlock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<RPObject> getZoneContents() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
