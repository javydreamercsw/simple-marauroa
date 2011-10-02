
package simple.server.core.engine;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.db.command.DBCommandQueue;
import simple.common.game.ClientObjectInterface;
import simple.server.core.engine.dbcommand.AbstractLogItemEventCommand;
import simple.server.core.engine.dbcommand.LogMergeItemEventCommand;
import simple.server.core.engine.dbcommand.LogSimpleItemEventCommand;
import simple.server.core.engine.dbcommand.LogSplitItemEventCommand;
import simple.server.core.entity.Entity;
import simple.server.core.entity.PassiveEntity;
import simple.server.core.entity.RPEntity;
import simple.server.core.entity.item.Item;

/**
 * Item Logger.
 *
 * @author hendrik
 */
public class ItemLogger {

    public static void addLogItemEventCommand(final AbstractLogItemEventCommand command) {
        DBCommandQueue.get().enqueue(command);
    }

    private static String getQuantity(final RPObject item) {
        int quantity = 1;
        if (item.has("quantity")) {
            quantity = item.getInt("quantity");
        }
        return Integer.toString(quantity);
    }

    public void loadOnLogin(final ClientObjectInterface player, final RPSlot slot, final Item item) {
        if (item.has(AbstractLogItemEventCommand.ATTR_ITEM_LOGID)) {
            return;
        }
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "create", item.get("name"), getQuantity(item), "olditem",
                slot.getName()));
    }

    public void destroyOnLogin(final ClientObjectInterface player, final RPSlot slot, final RPObject item) {
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "destroy", item.get("name"), getQuantity(item), "on login",
                slot.getName()));
    }

    public static void destroy(final ClientObjectInterface entity, final RPSlot slot, final RPObject item) {
        destroy(entity, slot, item, "quest");
    }

    public static void destroy(final ClientObjectInterface entity, final RPSlot slot, final RPObject item, String reason) {
        String slotName = "";
        if (slot != null) {
            slotName = slot.getName();
        }
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, entity, "destroy", item.get("name"), getQuantity(item), reason,
                slotName));
    }

    public void dropQuest(final ClientObjectInterface player, final Item item) {
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "destroy", item.get("name"), getQuantity(item), "quest", null));
    }

    public void timeout(final Item item) {
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, null, "destroy", item.get("name"), getQuantity(item), "timeout", item.getZone().getID().getID() + " " + item.getX() + " " + item.getY()));
    }

    public void displace(final ClientObjectInterface player, final PassiveEntity item, final SimpleRPZone zone, final int oldX, final int oldY, final int x, final int y) {
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "ground-to-ground", zone.getID().getID(), oldX + " " + oldY,
                zone.getID().getID(), x + " " + y));
    }

    public void equipAction(final ClientObjectInterface player, final Entity entity, final String[] sourceInfo, final String[] destInfo) {
        addLogItemEventCommand(new LogSimpleItemEventCommand(entity, player, sourceInfo[0] + "-to-" + destInfo[0], sourceInfo[1],
                sourceInfo[2], destInfo[1], destInfo[2]));
    }

    public static void merge(final RPEntity entity, final Item oldItem, final Item outlivingItem) {
        if (!(entity instanceof ClientObjectInterface)) {
            return;
        }
        final ClientObjectInterface player = (ClientObjectInterface) entity;

        addLogItemEventCommand(new LogMergeItemEventCommand(player, oldItem, outlivingItem));
    }

    public static void splitOff(final ClientObjectInterface player, final Item item, final int quantity) {
        final String oldQuantity = getQuantity(item);
        final String outlivingQuantity = Integer.toString(Integer.parseInt(oldQuantity) - quantity);
        addLogItemEventCommand(new LogSimpleItemEventCommand(item, player, "split out", "-1", oldQuantity, outlivingQuantity, Integer.toString(quantity)));
    }

    public void splitOff(final ClientObjectInterface player, final Item item, final Item newItem, final int quantity) {
        if (!(player instanceof ClientObjectInterface)) {
            return;
        }
        addLogItemEventCommand(new LogSplitItemEventCommand(player, item, newItem));
    }

    /*
    create             name         quantity          quest-name / killed creature / summon zone x y / summonat target target-slot quantity / olditem
    slot-to-slot       source       source-slot       target    target-slot
    ground-to-slot     zone         x         y       target    target-slot
    slot-to-ground     source       source-slot       zone         x         y
    ground-to-ground   zone         x         y       zone         x         y
    use                old-quantity new-quantity
    destroy            name         quantity          by admin / by quest / on login / timeout on ground
    merge in           outliving_id      destroyed-quantity   outliving-quantity       merged-quantity
    merged in          destroyed_id      outliving-quantity   destroyed-quantity       merged-quantity
    split out          new_id            old-quantity         outliving-quantity       new-quantity
    splitted out       outliving_id      old-quantity         new-quantity             outliving-quantity

    the last two are redundant pairs to simplify queries
     */
}
