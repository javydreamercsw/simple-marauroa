package simple.server.extension.d20;

import java.util.Iterator;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import simple.server.core.tool.Tool;

/**
 *
 * @author Javier A. Ortiz Bultrón javier.ortiz.78@gmail.com
 */
public class D20Tool {

    /**
     * Check if slot has the specified D20Characteristic
     *
     * @param slot slot to check into.
     * @param c characteristic to look for.
     * @return true if found, false otherwise
     */
    public static boolean slotContainsCharacteristic(RPSlot slot,
            D20Characteristic c) {
        Iterator it = slot.iterator();
        boolean contained = false;
        while (it.hasNext()) {
            if ((Tool.extractName((RPObject) it.next()))
                    .equals(c.getCharacteristicName())) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    /**
     * Get value of the specified D20Characteristic
     *
     * @param slot slot to check into.
     * @param c characteristic to look for.
     * @return value if found, null otherwise
     */
    public static RPObject getValueFromSlot(RPSlot slot, D20Characteristic c) {
        RPObject result = null;
        for (RPObject next : slot) {
            if (((D20Characteristic) next).getCharacteristicName()
                    .equals(c.getCharacteristicName())) {
                result = next;
                break;
            }
        }
        return result;
    }
}
