package simple.server.extension;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import marauroa.common.game.Attributes;
import marauroa.common.game.IRPZone;
import marauroa.common.game.IRPZone.ID;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import simple.common.NotificationType;
import simple.server.core.action.ActionInterface;
import simple.server.core.action.CommandCenter;
import simple.server.core.action.DelayedAction;
import simple.server.core.action.WellKnownActionConstant;
import simple.server.core.engine.IRPWorld;
import simple.server.core.engine.ISimpleRPZone;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.RPEntityInterface;
import simple.server.core.event.ITurnNotifier;
import simple.server.core.event.PrivateTextEvent;
import simple.server.core.tool.Tool;

/**
 * Create, Read, Update or Remove actions
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = MarauroaServerExtension.class)
public class ZoneExtension extends SimpleServerExtension
        implements ActionInterface {

    /**
     * the logger instance.
     */
    private static final Logger LOG
            = Logger.getLogger(ZoneExtension.class.getSimpleName());
    public static final String TYPE = "CRUDZone",
            DESC = "description", OPERATION = "operation",
            PASSWORD = "password", SEPARATOR = "separator";

    public ZoneExtension() {
        CommandCenter.register(TYPE, ZoneExtension.this);
    }

    @Override
    public RPObject onRPObjectAddToZone(RPObject object) {
        if (object.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
            //Send the list to the user
            final RPEntityInterface player = (RPEntityInterface) object;
            final RPAction action = new RPAction();
            action.put(WellKnownActionConstant.TYPE, ZoneExtension.TYPE);
            action.put(ZoneExtension.OPERATION, ZoneEvent.LISTZONES);
            action.put(ZoneExtension.SEPARATOR, "#");
            player.addEvent(new ZoneEvent(player.getZone(),
                    ZoneEvent.JOIN));
            listZones(player, ZoneEvent.LISTZONES, action);
        } else {
            LOG.log(Level.FINE, "Added a {0}", object.getClass());
        }
        return object;
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
            RPEntityInterface player = new RPEntity(rpo);
            LOG.log(Level.FINE, "Action requested by: {0}, action: {1}",
                    new Object[]{rpo, action});
            if (action.has(OPERATION)) {
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
                            listZones(player, op, action);
                            break;
                        case ZoneEvent.JOIN:
                            join(player, action);
                            break;
                        case ZoneEvent.LISTPLAYERS:
                            listPlayers(player, action);
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
                LOG.log(Level.SEVERE,
                        "Error processing CRUD room operation. Missing Operation!"
                        + " Action: {0}", action);
            }
        } else {
            LOG.log(Level.WARNING, "Unexpected action from a non-player: {0}", rpo);
        }
    }

    private void create(final RPEntityInterface player, final RPAction action) {
        LOG.log(Level.FINE, "Request for zone creation from: {0}, zone: {1}",
                new Object[]{player.getName(), action.get(ZoneEvent.ROOM)});
        final SimpleRPWorld world = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
        //Make sure the zone doesn't exists!
        if (action.has(ZoneEvent.ROOM) && !world.hasRPZone(new ID(action.get(ZoneEvent.ROOM)))) {
            SimpleRPZone zone = new SimpleRPZone(action.get(ZoneEvent.ROOM));
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
            world.addZone(zone);
            LOG.fine("Scheduling moving player to created zone...");
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(10,
                    new DelayedAction(new AbstractAction() {
                        private static final long serialVersionUID = -5644390861803492172L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (player instanceof RPObject) {
                                world.changeZone(action.get(ZoneEvent.ROOM),
                                        (RPObject) player);
                            }
                        }
                    }));
        } else if (action.has(ZoneEvent.ROOM)) {
            player.sendPrivateText(NotificationType.PRIVMSG,
                    "Sorry, that room already exists!");
        } else {
            player.sendPrivateText(NotificationType.PRIVMSG,
                    "Missing room name!");
        }
    }

    private void join(RPEntityInterface player, RPAction action) {
        if (player != null && action != null) {
            //If in same room, tell the player. The client should handle this but just in case...
            if (player instanceof RPObject
                    && action.get(ZoneEvent.ROOM)
                            .equals(((Attributes) player).get("zoneid"))) {
                player.sendPrivateText("You already are in "
                        + action.get(ZoneEvent.ROOM) + " room.");
            } //Make sure the zone exists...
            else if (Lookup.getDefault().lookup(IRPWorld.class)
                    .hasRPZone(new ID(action.get(ZoneEvent.ROOM)))) {
                ISimpleRPZone jZone = (ISimpleRPZone) Lookup.getDefault()
                        .lookup(IRPWorld.class)
                        .getZone(((Attributes) player).get("zoneid"));
                //If it's locked it means you need a password, you better have it...
                if (jZone.isLocked()) {
                    if (action.get(PASSWORD) != null) {
                        LOG.fine("Room is locked but password is provided...");
                        if (jZone.isPassword(action.get(PASSWORD))) {
                            LOG.fine("Password correct, changing zone...");
                            Lookup.getDefault().lookup(IRPWorld.class)
                                    .changeZone(action.get(ZoneEvent.ROOM),
                                            (RPObject) player);
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
                    Lookup.getDefault().lookup(IRPWorld.class)
                            .changeZone(action.get(ZoneEvent.ROOM),
                                    (RPObject) player);
                }
            }
        }
    }

    private void update(RPAction action) {
        SimpleRPWorld world
                = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
        if (action.has(ZoneEvent.ROOM) && world.hasRPZone(action.get(ZoneEvent.ROOM))) {
            LOG.log(Level.FINE, "Updating description of zone: {0} to: {1}",
                    new Object[]{action.get(ZoneEvent.ROOM), action.get(DESC)});
            SimpleRPZone updated
                    = world.updateRPZoneDescription(action.get(ZoneEvent.ROOM),
                            action.get(DESC));
            if (action.has(PASSWORD)) {
                try {
                    updated.setPassword(action.get(PASSWORD));
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
            LOG.fine("Updating description done!");
            world.applyPublicEvent(null,
                    new ZoneEvent(updated, ZoneEvent.UPDATE));
        }
    }

    private void remove(RPEntityInterface player, RPAction action) {
        if (!action.get(ZoneEvent.ROOM).equals(Lookup.getDefault()
                .lookup(IRPWorld.class).getDefaultZone().getID().getID())) {
            SimpleRPWorld world = (SimpleRPWorld) Lookup.getDefault()
                    .lookup(IRPWorld.class);
            SimpleRPZone zone
                    = (SimpleRPZone) world.getZone(new ID(action.get(ZoneEvent.ROOM)));
            Collection<RPEntityInterface> players = zone.getPlayers();
            for (RPEntityInterface clientObject : players) {
                world.changeZone(Lookup.getDefault().lookup(
                        IRPWorld.class).getDefaultZone().getID().getID(),
                        (RPObject) clientObject);
            }
            try {
                world.removeRPZone(new ID(action.get(ZoneEvent.ROOM)));
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            world.applyPublicEvent(null,
                    new ZoneEvent(new SimpleRPZone(action.get(ZoneEvent.ROOM)),
                            ZoneEvent.REMOVE));
            if (player != null && player instanceof RPObject) {
                ((RPObject) player).addEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, "Command completed"));
                player.notifyWorldAboutChanges();
            }
        } else if (player != null) {
            ((RPObject) player).addEvent(new PrivateTextEvent(
                    NotificationType.INFORMATION,
                    "Can't remove the default room!"));
            player.notifyWorldAboutChanges();
        }
    }

    private void listZones(RPEntityInterface player, int option, RPAction a) {
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
            String list = Lookup.getDefault().lookup(IRPWorld.class)
                    .listZones(separator).toString();
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
                new ZoneEvent((ISimpleRPZone) zone, ZoneEvent.ADD));
    }

    @Override
    public String getName() {
        return "Zone Extension";
    }

    private void listPlayers(RPEntityInterface player, RPAction action) {
        //Extract zone from field
        String z = action.get(ZoneEvent.FIELD);
        IRPZone zone = Lookup.getDefault()
                .lookup(IRPWorld.class).getZone(z);
        if (zone == null) {
            LOG.log(Level.WARNING,
                    "Invalid or missing zone specified: ''{0}''", z);
        } else {
            String separator = "#";
            if (action.has(SEPARATOR)) {
                if (action.get(SEPARATOR) != null
                        && !action.get(SEPARATOR).isEmpty()) {
                    separator = action.get(SEPARATOR);
                    LOG.log(Level.FINE, "Separator requested: {0}",
                            separator);
                }
            }
            StringBuilder players = new StringBuilder();
            for (RPObject obj : zone) {
                if (!players.toString().isEmpty()) {
                    players.append(separator);
                }
                players.append(Tool.extractName(obj));
            }
            ZoneEvent zoneEvent = new ZoneEvent(players.toString(),
                    ZoneEvent.LISTPLAYERS);
            //Add a separator if none defined
            if (!zoneEvent.has(SEPARATOR)) {
                zoneEvent.put(SEPARATOR, separator);
            }
            player.addEvent(zoneEvent);
            player.notifyWorldAboutChanges();
            LOG.log(Level.INFO, "Sent player list: {0}", players.toString());
        }
    }
}
