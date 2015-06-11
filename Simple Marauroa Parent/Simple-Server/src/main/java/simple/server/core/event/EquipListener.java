
package simple.server.core.event;

/**
 * Equipable Entities implement this interface EquipListener.
 * 
 * @author hendrik
 */
public interface EquipListener {

    /**
     * Checks whether this object can be equipped in the given slot.
     *
     * @param slot
     *            name of slot
     * @return true, if it can be equipped; false otherwise
     */
    boolean canBeEquippedIn(String slot);
}
