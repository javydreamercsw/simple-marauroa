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
import simple.server.core.entity.Entity;
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
    private final SimpleRPWorld world
            = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);

    public ZoneExtension() {
        CommandCenter.register(TYPE, ZoneExtension.this);
    }

    @Override
    public RPObject onRPObjectAddToZone(RPObject player) {
        if (player.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
            //Send the list to the user
            final RPAction action = new RPAction();
            action.put(WellKnownActionConstant.TYPE, ZoneExtension.TYPE);
            action.put(ZoneExtension.OPERATION, ZoneEvent.LISTZONES);
            action.put(ZoneExtension.SEPARATOR, "#");
            player.addEvent(new ZoneEvent((ISimpleRPZone) world
                    .getZone(player.get(Entity.ZONE_ID)),
                    ZoneEvent.JOIN));
            listZones(player, ZoneEvent.LISTZONES, action);
        } else {
            LOG.log(Level.FINE, "Added a {0}", player.getClass());
        }
        return player;
    }

    @Override
    public void onAction(RPObject player, RPAction action) {
        if (player.getRPClass().subclassOf(RPEntity.DEFAULT_RPCLASS)) {
            LOG.log(Level.FINE, "Action requested by: {0}, action: {1}",
                    new Object[]{player, action});
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
            LOG.log(Level.WARNING, "Unexpected action from a non-player: {0}", player);
        }
    }

    private void create(final RPObject player, final RPAction action) {
        LOG.log(Level.FINE, "Request for zone creation from: {0}, zone: {1}",
                new Object[]{Tool.extractName(player),
                    action.get(ZoneEvent.ROOM)});
        //Make sure the zone doesn't exists!
        if (action.has(ZoneEvent.ROOM)
                && !world.hasRPZone(new ID(action.get(ZoneEvent.ROOM)))) {
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
            world.addRPZone(zone);
            LOG.fine("Scheduling moving player to created zone...");
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(10,
                    new DelayedAction(new AbstractAction() {
                        private static final long serialVersionUID = -5644390861803492172L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (player instanceof RPObject) {
                                world.changeZone(action.get(ZoneEvent.ROOM), player);
                            }
                        }
                    }));
            if (player instanceof RPObject) {
                player.addEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, "Command completed"));
                world.modify(player);
            }
        } else if (action.has(ZoneEvent.ROOM)) {
            ((Entity) player).sendPrivateText(NotificationType.PRIVMSG,
                    "Sorry, that room already exists!");
        } else {
            ((Entity) player).sendPrivateText(NotificationType.PRIVMSG,
                    "Missing room name!");
        }
    }

    private void join(RPObject player, RPAction action) {
        if (player != null && action != null) {
            //If in same room, tell the player. The client should handle this
            //but just in case...
            if (player instanceof RPObject
                    && action.get(ZoneEvent.ROOM)
                            .equals(((Attributes) player).get("zoneid"))) {
                ((Entity) player).sendPrivateText("You already are in "
                        + action.get(ZoneEvent.ROOM) + " room.");
            } //Make sure the zone exists...
            else if (world.hasRPZone(new ID(action.get(ZoneEvent.ROOM)))) {
                ISimpleRPZone jZone = (ISimpleRPZone) world
                        .getZone(action.get(ZoneEvent.ROOM));
                //If it's locked it means you need a password, you better have it...
                if (jZone.isLocked()) {
                    if (action.get(PASSWORD) != null) {
                        LOG.fine("Room is locked but password is provided...");
                        if (jZone.isPassword(action.get(PASSWORD))) {
                            LOG.fine("Password correct, changing zone...");
                            world.changeZone(action.get(ZoneEvent.ROOM), player);
                        } else {
                            ZoneEvent re = new ZoneEvent(action, ZoneEvent.NEEDPASS);
                            LOG.fine("Room is locked.");
                            LOG.fine("Wrong password, requesting again...");
                            ((RPObject) player).addEvent(re);
                            ((Entity) player).notifyWorldAboutChanges();
                        }
                    } else {
                        LOG.fine("Room is locked but no password was provided...");
                        ZoneEvent re = new ZoneEvent(action, ZoneEvent.NEEDPASS);
                        LOG.fine("Room is locked. Requesting password...");
                        ((RPObject) player).addEvent(re);
                        ((Entity) player).notifyWorldAboutChanges();
                    }
                } else {
                    //The room is open so just join it.
                    world.changeZone(action.get(ZoneEvent.ROOM), player);
                }
            }
        }
    }

    private void update(RPAction action) {
        if (action.has(ZoneEvent.ROOM) && world.hasRPZone(action.get(ZoneEvent.ROOM))) {
            LOG.log(Level.FINE, "Updating description of zone: {0} to: {1}",
                    new Object[]{action.get(ZoneEvent.ROOM), action.get(DESC)});
            ISimpleRPZone updated
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

    private void remove(RPObject player, RPAction action) {
        if (!action.get(ZoneEvent.ROOM).equals(world.getDefaultZone()
                .getID().getID())) {
            SimpleRPZone zone
                    = (SimpleRPZone) world.getZone(new ID(action.get(ZoneEvent.ROOM)));
            Collection<RPEntityInterface> players = zone.getPlayers();
            for (RPEntityInterface clientObject : players) {
                world.changeZone(world.getDefaultZone().getID().getID(),
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
                player.addEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, "Command completed"));
                ((Entity) player).notifyWorldAboutChanges();
            }
        } else if (player != null) {
            player.addEvent(new PrivateTextEvent(
                    NotificationType.INFORMATION,
                    "Can't remove the default room!"));
            ((Entity) player).notifyWorldAboutChanges();
        }
    }

    private void listZones(RPObject player, int option, RPAction a) {
        try {
            LOG.log(Level.FINE, "Request for zone list from: {0}",
                    Tool.extractName(player));
            String separator = "#";
            if (a.has(SEPARATOR)) {
                if (a.get(SEPARATOR) != null && !a.get(SEPARATOR).isEmpty()) {
                    separator = a.get(SEPARATOR);
                    LOG.log(Level.FINE, "Separator requested: {0}",
                            separator);
                }
            }
            String list = world.listZones(separator).toString();
            LOG.log(Level.FINE, "Zone List: {0}", list);
            ZoneEvent zoneEvent = new ZoneEvent(list, option);
            //Add a separator if none defined
            if (!zoneEvent.has(SEPARATOR)) {
                zoneEvent.put(SEPARATOR, separator);
            }
            player.addEvent(zoneEvent);
            ((Entity) player).notifyWorldAboutChanges();
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
        world.applyPublicEvent(
                new ZoneEvent((ISimpleRPZone) zone, ZoneEvent.ADD));
    }

    @Override
    public String getName() {
        return "Zone Extension";
    }

    private void listPlayers(RPObject player, RPAction action) {
        //Extract zone from field
        String z = action.get(ZoneEvent.FIELD);
        IRPZone zone = world.getZone(z);
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
            ((Entity) player).notifyWorldAboutChanges();
            LOG.log(Level.INFO, "Sent player list: {0}", players.toString());
        }
    }
}
