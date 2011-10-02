
package simple.server.util;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import simple.server.core.engine.SimpleRPRuleProcessor;
import simple.server.core.engine.SimpleRPZone;
import simple.server.core.engine.SimpleSingletonRepository;
import simple.server.core.entity.Entity;
import simple.server.core.entity.clientobject.ClientObject;

/**
 * Utilities to handle entities in the server.
 * 
 * @author Martin Fuchs
 */
public class EntityHelper {

    private static final String ATTR_BASESLOT = "baseslot";
    private static final String ATTR_BASEOBJECT = "baseobject";
    private static final String ATTR_BASEITEM = "baseitem";

    /**
     * Translate the "target" parameter of actions like "look" into an entity
     * reference. Numeric parameters are treated as object IDs, alphanumeric
     * names are searched in the list of players and NPCs.
     *
     * @param target
     *			  representation of the target
     * @param player
     *			  to constraint for current zone and screen area
     * @return the entity associated either with name or id or
     *		   <code> null </code> if none was found or any of
     *		   the input parameters was <code> null </code>.
     */
    public static Entity entityFromTargetName(String target, Entity player) {
        if (target == null || player == null) {
            return null;
        }

        SimpleRPZone zone = player.getZone();
        Entity entity = null;
        //Treat as object id
        if (target.length() > 1 && target.charAt(0) == '#' && Character.isDigit(target.charAt(1))) {
            int objectId = Integer.parseInt(target.substring(1));

            RPObject.ID targetid = new RPObject.ID(objectId, zone.getID());

            if (zone.has(targetid)) {
                RPObject object = zone.get(targetid);

                if (object instanceof Entity) {
                    entity = (Entity) object;
                }
            }
        }
        //Treat as a player
        if (entity == null) {
            entity = (Entity) SimpleSingletonRepository.get().get(SimpleRPRuleProcessor.class).getPlayer(target);

            if (entity != null) {
                entity = null;
            }
        }
        return entity;
    }

    public static Entity entityFromSlot(ClientObject player, RPAction action) {
        // entity in a slot?
        if (!action.has(ATTR_BASEITEM) || !action.has(ATTR_BASEOBJECT) || !action.has(ATTR_BASESLOT)) {
            return null;
        }

        SimpleRPZone zone = player.getZone();

        int baseObject = action.getInt(ATTR_BASEOBJECT);

        RPObject.ID baseobjectid = new RPObject.ID(baseObject, zone.getID());
        if (!zone.has(baseobjectid)) {
            return null;
        }

        RPObject base = zone.get(baseobjectid);
        if (!(base instanceof Entity)) {
            // Shouldn't really happen because everything is an entity
            return null;
        }

        Entity baseEntity = (Entity) base;

        if (baseEntity.hasSlot(action.get(ATTR_BASESLOT))) {
            RPSlot slot = baseEntity.getSlot(action.get(ATTR_BASESLOT));

            if (slot.size() == 0) {
                return null;
            }

            RPObject object = null;
            int item = action.getInt(ATTR_BASEITEM);
            // scan through the slot to find the requested item
            for (RPObject rpobject : slot) {
                if (rpobject.getID().getObjectID() == item) {
                    object = rpobject;
                    break;
                }
            }

            // no item found... we take the first one
            if (object == null) {
                object = slot.iterator().next();
            }

            // It is always an entity
            return (Entity) object;
        }

        return null;
    }
}
