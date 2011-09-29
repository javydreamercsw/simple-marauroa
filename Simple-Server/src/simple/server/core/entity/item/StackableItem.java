/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.entity.item;

import java.util.Map;
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.RPObject;
import simple.server.core.engine.SimpleRPWorld;
import simple.server.core.engine.SimpleSingletonRepository;

public class StackableItem extends Item implements Stackable {

    private static final long serialVersionUID = 1L;
    private int quantity = 1;
    private static Logger logger = Log4J.getLogger(StackableItem.class);

    public StackableItem(String name, String clazz, String subclass,
            Map<String, String> attributes) {
        super(name, clazz, subclass, attributes);
        update();
    }

    /**
     * Copy constructor.
     * 
     * @param item
     *            item to copy
     */
    public StackableItem(StackableItem item) {
        super(item);
        this.quantity = item.quantity;
        update();
    }

    @Override
    public void update() {
        super.update();
        if (has("quantity")) {
            quantity = getInt("quantity");
        }
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int amount) {
        if (amount < 0) {
            logger.error("Trying to set invalid quantity: " + amount,
                    new Throwable());
            amount = 1;
        }
        quantity = amount;
        put("quantity", quantity);
    }

    public int sub(int amount) {
        setQuantity(quantity - amount);
        return quantity;
    }

    @Override
    public int add(Stackable other) {
        setQuantity(other.getQuantity() + quantity);
        return quantity;
    }

    public StackableItem splitOff(int amountToSplitOff) {
        if ((quantity <= 0) || (amountToSplitOff <= 0)) {
            return null;
        }

        if (quantity >= amountToSplitOff) {
            StackableItem newItem = (StackableItem) SimpleSingletonRepository.getEntityManager().getItem(
                    getName());

            newItem.setQuantity(amountToSplitOff);

            String[] attributesToCopyOnSplit = new String[]{"infostring",
                "description", "bound", "persistent", "undroppableondeath",
                "amount", "frequency", "regen", "atk", "range"
            };
            for (String attribute : attributesToCopyOnSplit) {
                if (has(attribute)) {
                    newItem.put(attribute, get(attribute));
                }
            }

            sub(amountToSplitOff);

            if (quantity > 0) {
                if (isContained()) {
                    // We modify the base container if the object change.
                    RPObject base = getContainer();
                    while (base.isContained()) {
                        base = base.getContainer();
                    }
                    SimpleSingletonRepository.get().get(SimpleRPWorld.class).modify(base);
                } else {
                    try {
                        notifyWorldAboutChanges();
                    } catch (Exception e) {
                        logger.warn("isContained() returned false on contained object (bank chest bug): " + e);
                    }
                }
            } else {
                /* If quantity=0 then it means that item has to be removed */
                super.removeFromWorld();
            }

            return newItem;
        }
        return null;
    }

    @Override
    public void removeOne() {
        splitOff(1);
    }

    @Override
    public boolean isStackable(Stackable other) {
        StackableItem otheri = (StackableItem) other;

        if (!getItemClass().equals(otheri.getItemClass()) || !getItemSubclass().equals(otheri.getItemSubclass())) {
            return false;
        }

        String[] importantAttributes = new String[]{"infostring", "bound",
            "persistent", "undroppableondeath", "amount", "frequency",
            "regen", "atk", "range"
        };
        for (String iAtt : importantAttributes) {
            if (!has(iAtt) && !otheri.has(iAtt)) {
                continue;
            }
            if (has(iAtt) && otheri.has(iAtt) && get(iAtt).equals(otheri.get(iAtt))) {
                continue;
            }
            return false;
        }
        return true;
    }
}
