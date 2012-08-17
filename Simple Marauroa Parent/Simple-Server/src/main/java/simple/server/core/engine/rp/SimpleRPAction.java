package simple.server.core.engine.rp;

import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPServerManager;
import org.openide.util.Lookup;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.IRPObjectFactory;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.entity.Entity;
import simple.server.core.event.TutorialNotifier;

public class SimpleRPAction {

    /**
     * the logger instance.
     */
    private static final Logger logger = Log4J.getLogger(SimpleRPAction.class);
    /**
     * server manager.
     */
    private static RPServerManager rpman;

    public static void initialize(RPServerManager rpman) {
        SimpleRPAction.rpman = rpman;
    }

    /**
     * Places an entity at a specified position in a specified zone. This will
     * remove the entity from any existing zone and add it to the target zone if
     * needed.
     *
     * @param zone zone to place the entity in
     * @param entity the entity to place
     * @return true, if it was possible to place the entity, false otherwise
     */
    public static boolean placeat(SimpleRPZone zone, Entity entity) {
        // check in case of players that that they are still in game
        // because the entity is added to the world again otherwise.
        if (entity instanceof ClientObjectInterface && Lookup.getDefault().lookup(IRPObjectFactory.class).createDefaultClientObject(entity).isDisconnected()) {
            return true;
        }

        SimpleRPZone oldZone = entity.getZone();
        boolean zoneChanged = ( oldZone != zone );

        /*
         * Remove from old zone (if any) during zone change
         */
        if (oldZone != null) {
            /*
             * ClientObjectInterface specific pre-remove handling
             */
            if (entity instanceof ClientObjectInterface) {
                //Nothing right now
            }

            if (zoneChanged) {
                oldZone.remove(entity);
            }
        }

        /*
         * Place in new zone (if needed)
         */
        if (zoneChanged) {
            zone.add(entity);
        }

        /*
         * ClientObjectInterface specific post-change handling
         */
        if (entity instanceof ClientObjectInterface) {
            ClientObjectInterface player =
                    Lookup.getDefault().lookup(IRPObjectFactory.class).createDefaultClientObject(entity);

            if (zoneChanged) {
                /*
                 * Zone change notifications/updates
                 */
                transferContent(player);

                if (oldZone != null) {
                    String source = oldZone.getName();
                    String destination = zone.getName();

                    ( (SimpleRPRuleProcessor) Lookup.getDefault().lookup(IRPRuleProcessor.class) ).addGameEvent(
                            player.getName(), "change zone", destination);

                    TutorialNotifier.zoneChange(player, source, destination);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Placed " + entity.getTitle() + " at " + zone.getName());
        }
        return true;
    }

    /**
     * send the content of the zone the player is in to the client.
     *
     * @param player
     */
    public static void transferContent(ClientObjectInterface player) {
        if (rpman != null) {
            SimpleRPZone zone = player.getZone();
            if (zone != null) {
                rpman.transferContent((RPObject) player, zone.getContents());
            }
        } else {
            logger.warn("rpmanager not found");
        }
    }
}
