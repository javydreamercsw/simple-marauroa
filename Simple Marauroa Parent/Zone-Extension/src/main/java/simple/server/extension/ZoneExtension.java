package simple.server.extension;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.IRPZone.ID;
import marauroa.common.game.*;
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
    private static final Logger logger = Log4J.getLogger(ZoneExtension.class);
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
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(5,
                    new DelayedAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    list(player, ZoneEvent.LISTZONES, action);
                }
            }));
        } else {
            logger.warn("Added a " + object.getClass());
        }
        return object;
    }

    @Override
    public void onAction(RPObject rpo, RPAction action) {
        if (rpo instanceof ClientObjectInterface) {
            ClientObjectInterface player = (ClientObjectInterface) rpo;
            logger.debug("Action requested by: " + rpo + ", action: " + action);
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
                        logger.warn("Invalid CRUD operation: " + op);
                }
            } catch (Exception e) {
                logger.error("Error processing CRUD room operation: " + op + "."
                        + " Action: " + action, e);
            }
        }
    }

    private void create(final ClientObjectInterface player, final RPAction action) {
        logger.debug("Request for zone creation from: "
                + player.getName() + ", zone: " + action.get(ROOM));
        final SimpleRPWorld world = (SimpleRPWorld) Lookup.getDefault().lookup(IRPWorld.class);
        //Make sure the zone doesn't exists!
        if (!world.hasRPZone(new ID(action.get(ROOM)))) {
            SimpleRPZone zone = new SimpleRPZone(action.get(ROOM));
            if (action.get(DESC) != null && !action.get(DESC).isEmpty()) {
                logger.debug("Setting description: " + action.get(DESC));
                zone.setDescription(action.get(DESC));
            }
            if (action.get(PASSWORD) != null && !action.get(PASSWORD).isEmpty()) {
                try {
                    logger.debug("Setting password: " + action.get(PASSWORD));
                    zone.setPassword(action.get(PASSWORD));
                } catch (IOException ex) {
                    logger.error(ex);
                }
            }
            logger.debug("Adding zone to the world...");
            world.addRPZone(zone);
            logger.info("Scheduling moving player to created zone...");
            Lookup.getDefault().lookup(ITurnNotifier.class).notifyInTurns(2,
                    new DelayedAction(new AbstractAction() {
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
                        logger.debug("Room is locked but password is provided...");
                        if (jZone.isPassword(action.get(PASSWORD))) {
                            logger.debug("Password correct, changing zone...");
                            Lookup.getDefault().lookup(IRPWorld.class).changeZone(action.get(ROOM), (RPObject) player);
                        } else {
                            ZoneEvent re = new ZoneEvent(action, ZoneEvent.NEEDPASS);
                            logger.debug("Room is locked. " + re);
                            logger.debug("Wrong password, requesting again...");
                            ((RPObject) player).addEvent(re);
                            player.notifyWorldAboutChanges();
                        }
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
            logger.debug("Updating description of zone: "
                    + action.get(ROOM) + " to: " + action.get(DESC));
            SimpleRPZone updated = world.updateRPZoneDescription(action.get(ROOM), action.get(DESC));
            logger.debug("Updating description done!");
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
        } else {
            if (player != null) {
                ((RPObject) player).addEvent(new PrivateTextEvent(
                        NotificationType.INFORMATION, "Can't remove the default room!"));
                player.notifyWorldAboutChanges();
            }
        }
    }

    private void list(ClientObjectInterface player, int option, RPAction a) {
        try {
            logger.debug("Request for zone list from: " + player.getName());
            String separator = "#";
            if (a.has(SEPARATOR)) {
                if (a.get(SEPARATOR) != null && !a.get(SEPARATOR).isEmpty()) {
                    separator = a.get(SEPARATOR);
                    logger.debug("Separator requested: " + separator);
                }
            }
            String list = Lookup.getDefault().lookup(IRPWorld.class).listZones(separator).toString();
            logger.debug("Zone List: " + list);
            player.addEvent(new ZoneEvent(list, option));
            player.notifyWorldAboutChanges();
            logger.debug(player);
        } catch (Exception ex) {
            logger.fatal(null, ex);
        }
    }

    @Override
    public void onAddRPZone(IRPZone zone) {
        //Let everyone know
        logger.info("Notifying everyone about the creation of zone: " + zone.getID());
        Lookup.getDefault().lookup(IRPWorld.class).applyPublicEvent(new ZoneEvent((SimpleRPZone) zone, ZoneEvent.ADD));
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
