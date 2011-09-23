/*
 * $Rev$
 * $LastChangedDate$
 * $LastChangedBy$
 */
package simple.server.core.entity.item;

/**
 * This interface tags all items which are stackable.
 *
 * @author mtotz
 */
public interface Stackable {

    /** @return the quantity */
    int getQuantity();

    /** sets the quantity
     * @param amount
     */
    void setQuantity(int amount);

    /** adds the quantity of the other Stackable to this
     * @param other
     * @return
     */
    int add(Stackable other);

    /**
     * @param other
     * @return true when both stackables are of the same type and can be merged */
    boolean isStackable(Stackable other);
}
