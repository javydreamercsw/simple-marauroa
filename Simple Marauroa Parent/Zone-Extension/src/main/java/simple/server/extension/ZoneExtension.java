package simple.server.extension;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import marauroa.common.game.Definition;
import marauroa.common.game.IRPZone;
import marauroa.common.game.IRPZone.ID;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.common.game.ClientObjectInterface;
import simple.server.core.action.ActionInterface;
import simple.server.core.action.CommandCenter;
import simple.server.core.action.DelayedAction;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;

/**
 * Create, Read, Update or Remove actions
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class ZoneExtension extends SimpleServerExtension implements ActionInterface {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(ZoneExtension.class.getSimpleName());
    public static final String TYPE = "CRUDZone", ROOM = "room",
            DESC = "description", OPERATION = "operation", PASSWORD = "password",
            SEPARATOR = "separator";

    public ZoneExtension() {
        CommandCenter.register(TYPE, ZoneExtension.this);
    }

    @Override
    public RPObject onRPObjectAddToZone(RPObject object) {
        if (object instanceof ClientObjectInterface) {
            //Send the list to the user
            final ClientObjectInterface player = (ClientObjectInterface) object;
            final RPAction action = new RPAction();
            action.put("type", ZoneExtension.TYPE);
            action.put(ZoneExtension.OPERATION, ZoneEvent.LISTZONES);
            action.put(ZoneExtension.SEPARATOR, "#");
            //Just wait a little bit...
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(10,
                    new DelayedAction(new AbstractAction() {
                        private static final long serialVersionUID = -5644390861803492172L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            list(player, ZoneEvent.LISTZONES, action);
                        }
                    }));
        } else {
            LOG.log(Level.WARNING, "Added a {0}", object.getClass());
        }
        return object;
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            LOG.log(Level.FINE, "Action requested by: {0}, action: {1}",
                    new Object[]{rpo, action});
            int op = action.getInt(OPERATION);
            try {
                switch (op) {
                    case ZoneEvent.ADD:
                        create(player, action);
                        break;
                    case ZoneEvent.UPDATE:
                        update(action);
                        break;
                    case ZoneEvent.REMOVE:
                        remove(player, action);
                        break;
                    case ZoneEvent.LISTZONES:
                        list(player, op, action);
                        break;
                    case ZoneEvent.JOIN:
                        join(player, action);
                        break;
                    default:
                        LOG.log(Level.WARNING, "Invalid CRUD operation: {0}", op);
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Error processing CRUD room operation: "
                        + op + "."
                        + " Action: " + action, e);
            }
        } else {
            LOG.log(Level.WARNING, "Unexpected action from a non-player: {0}", rpo);
        }
    }

    private void create(final ClientObjectInterface player, final RPAction action) {
        LOG.log(Level.FINE, "Request for zone creation from: {0}, zone: {1}",
                new Object[]{player.getName(), action.get(ROOM)});
        final SimpleRPWorld world = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
        //Make sure the zone doesn't exists!
        if (!world.hasRPZone(new ID(action.get(ROOM)))) {
            SimpleRPZone zone = new SimpleRPZone(action.get(ROOM));
            if (action.get(DESC) != null && !action.get(DESC).isEmpty()) {
                LOG.log(Level.FINE, "Setting description: {0}", action.get(DESC));
                zone.setDescription(action.get(DESC));
            }
            if (action.get(PASSWORD) != null && !action.get(PASSWORD).isEmpty()) {
                try {
                    LOG.log(Level.FINE, "Setting password: {0}", action.get(PASSWORD));
                    zone.setPassword(action.get(PASSWORD));
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            LOG.fine("Adding zone to the world...");
            world.addRPZone(zone);
            LOG.fine("Scheduling moving player to created zone...");
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(10,
                    new DelayedAction(new AbstractAction() {
                        private static final long serialVersionUID = -5644390861803492172L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (player instanceof RPObject) {
                                world.changeZone(action.get(ROOM), (RPObject) player);
                            }
                        }
                    }));
        } else {
            player.sendPrivateText(NotificationType.PRIVMSG, "Sorry, that room already exists!");
        }
    }

    private void join(ClientObjectInterface player, RPAction action) {
        if (player != null && action != null) {
            //If in same room, tell the player. The client should handle this but just in case...
            if (player instanceof RPObject && action.get(ROOM).equals(((RPObject) player).get("zoneid"))) {
                player.sendPrivateText("You already are in " + action.get(ROOM) + " room.");
            } //Make sure the zone exists...
            else if (Lookup.getDefault().lookup(IRPWorld.class).hasRPZone(new ID(action.get(ROOM)))) {
                SimpleRPZone jZone = (SimpleRPZone) Lookup.getDefault().lookup(IRPWorld.class).getRPZone(((RPObject) player).get("zoneid"));
                //If it's locked it means you need a password, you better have it...
                if (jZone.isLocked()) {
                    if (action.get(PASSWORD) != null) {
                        LOG.fine("Room is locked but password is provided...");
                        if (jZone.isPassword(action.get(PASSWORD))) {
                            LOG.fine("Password correct, changing zone...");
                            Lookup.getDefault().lookup(IRPWorld.class).changeZone(action.get(ROOM), (RPObject) player);
                        } else {
                            ZoneEvent re = new ZoneEvent(action, ZoneEvent.NEEDPASS);
                            LOG.fine("Room is locked.");
                            LOG.fine("Wrong password, requesting again...");
                            ((RPObject) player).addEvent(re);
                            player.notifyWorldAboutChanges();
                        }
                    } else {
                        LOG.fine("Room is locked but no password was provided...");
                        ZoneEvent re = new ZoneEvent(action, ZoneEvent.NEEDPASS);
                        LOG.fine("Room is locked. Requesting password...");
                        ((RPObject) player).addEvent(re);
                        player.notifyWorldAboutChanges();
                    }
                } else {
                    //The room is open so just join it.
                    Lookup.getDefault().lookup(IRPWorld.class).changeZone(action.get(ROOM), (RPObject) player);
                }
            }
        }
    }

    private void update(RPAction action) {
        SimpleRPWorld world = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
        if (world.getRPZone(action.get(ROOM)) != null) {
            LOG.log(Level.FINE, "Updating description of zone: {0} to: {1}",
                    new Object[]{action.get(ROOM), action.get(DESC)});
            SimpleRPZone updated = world.updateRPZoneDescription(action.get(ROOM),
                    action.get(DESC));
            LOG.fine("Updating description done!");
            world.applyPublicEvent(null,
                    new ZoneEvent(updated, ZoneEvent.UPDATE));
        }
    }

    private void remove(ClientObjectInterface player, RPAction action) {
        if (!action.get(ROOM).equals(Lookup.getDefault().lookup(IRPWorld.class).getDefaultZone().getID().getID())) {
            SimpleRPWorld world = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
            SimpleRPZone zone = (SimpleRPZone) world.getRPZone(new ID(action.get(ROOM)));
            Collection<ClientObjectInterface> players = zone.getPlayers();
            for (ClientObjectInterface clientObject : players) {
                world.changeZone(Lookup.getDefault().lookup(
                        IRPWorld.class).getDefaultZone().getID().getID(),
                        (RPObject) clientObject);
            }
            try {
                world.removeRPZone(new ID(action.get(ROOM)));
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(ZoneExtension.class.getSimpleName()).log(Level.SEVERE, null, ex);
            }
            world.applyPublicEvent(null,
                    new ZoneEvent(new SimpleRPZone(action.get(ROOM)),
                            ZoneEvent.REMOVE));
            for (ClientObjectInterface p : zone.getPlayers()) {
                p.notifyWorldAboutChanges();
            }
            if (player != null && player instanceof RPObject) {
                ((RPObject) player).addEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, "Command completed"));
                player.notifyWorldAboutChanges();
            }
        } else if (player != null) {
            ((RPObject) player).addEvent(new PrivateTextEvent(
                    NotificationType.INFORMATION, "Can't remove the default room!"));
            player.notifyWorldAboutChanges();
        }
    }

    private void list(ClientObjectInterface player, int option, RPAction a) {
        try {
            LOG.log(Level.FINE, "Request for zone list from: {0}",
                    player.getName());
            String separator = "#";
            if (a.has(SEPARATOR)) {
                if (a.get(SEPARATOR) != null && !a.get(SEPARATOR).isEmpty()) {
                    separator = a.get(SEPARATOR);
                    LOG.log(Level.FINE, "Separator requested: {0}",
                            separator);
                }
            }
            String list = Lookup.getDefault().lookup(IRPWorld.class).listZones(separator).toString();
            LOG.log(Level.FINE, "Zone List: {0}", list);
            ZoneEvent zoneEvent = new ZoneEvent(list, option);
            //Add a separator if none defined
            if (!zoneEvent.has(SEPARATOR)) {
                zoneEvent.put(SEPARATOR, separator);
            }
            player.addEvent(zoneEvent);
            player.notifyWorldAboutChanges();
            LOG.fine(player.toString());
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onAddRPZone(IRPZone zone) {
        //Let everyone know
        LOG.log(Level.FINE, "Notifying everyone about the creation of zone: {0}",
                zone.getID());
        Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(
                new ZoneEvent((SimpleRPZone) zone, ZoneEvent.ADD));
    }

    @Override
    public void modifyClientObjectDefinition(RPClass player) {
        player.addRPEvent(ZoneEvent.RPCLASS_NAME, Definition.VOLATILE);
    }

    @Override
    public String getName() {
        return "Zone Extension";
    }
}
