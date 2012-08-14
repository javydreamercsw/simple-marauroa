
package simple.server.core.engine.dbcommand;


import java.sql.SQLException;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import simple.common.game.ClientObjectInterface;

/**
 * logs merging of items into a stack
 *
 * @author hendrik
 */
public class LogMergeItemEventCommand extends AbstractLogItemEventCommand {

    private RPObject liveOldItem;
    private RPObject liveOutlivingItem;
    private RPObject frozenOldItem;
    private RPObject frozenOutlivingItem;
    private ClientObjectInterface player;

    /**
     * logs merging of item stacks
     *
     * @param player   Player performing the merge
     * @param oldItem  old item being destroyed during the merge
     * @param outlivingItem item which survives the merge
     */
    public LogMergeItemEventCommand(ClientObjectInterface player, RPObject oldItem, RPObject outlivingItem) {
        this.player = player;
        this.liveOldItem = oldItem;
        this.liveOutlivingItem = outlivingItem;
        this.frozenOldItem = (RPObject) oldItem.clone();
        this.frozenOutlivingItem = (RPObject) outlivingItem.clone();
    }

    @Override
    protected void log(DBTransaction transaction) throws SQLException {
        itemLogAssignIDIfNotPresent(transaction, liveOldItem);
        itemLogAssignIDIfNotPresent(transaction, liveOutlivingItem);

        final String oldQuantity = getQuantity(frozenOldItem);
        final String oldOutlivingQuantity = getQuantity(frozenOutlivingItem);
        final String newQuantity = Integer.toString(Integer.parseInt(oldQuantity) + Integer.parseInt(oldOutlivingQuantity));

        itemLogWriteEntry(transaction, liveOldItem.getInt(ATTR_ITEM_LOGID), player, "merge in",
                liveOutlivingItem.get(ATTR_ITEM_LOGID), oldQuantity,
                oldOutlivingQuantity, newQuantity);
        itemLogWriteEntry(transaction, liveOutlivingItem.getInt(ATTR_ITEM_LOGID), player, "merged in",
                liveOldItem.get(ATTR_ITEM_LOGID), oldOutlivingQuantity,
                oldQuantity, newQuantity);
    }
}
